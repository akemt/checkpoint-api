package com.huawei.checkpoint.function;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.data.SubscribeStatus;
import com.huawei.checkpoint.data.SubscribesInfo;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.SubscribeUtils;
import com.huawei.checkpoint.utils.VCMAccess;
public class SgSubscribePeriodicCheckThread implements Runnable{
	private static Logger log = Logger.getLogger(SgSubscribePeriodicCheckThread.class);
	
	private static final String pattern19 = "yyyy-MM-dd HH:mm:ss";
	// 标记线程是否需要运行
	private volatile boolean running = true;	
	
	@SuppressWarnings("unchecked")
	public void run() {
		Config cf = Config.getIns();
		String strCookie = SystemManager.getIns().getCookie();
		String subId = null;
		long interval = cf.getSubscribeCheckInterval();
		List <SubscribesInfo> subInfoList = null;

		while(running) {			
			subInfoList = new ArrayList<SubscribesInfo>();
			//检索VCM，是否有订阅信息
			long beginTime = System.currentTimeMillis();
			log.debug("SgSubscribePeriodicCheckThread beginTime:" + beginTime);

			String reStr = VCMAccess.querySubInfo(strCookie, 100, subId);
			try {
				Document document = DocumentHelper.parseText(reStr);
				String code = document.getRootElement().element("result").element("code").getTextTrim();
				if(code.equals("0")){
					int totalCount = Integer.parseInt(document.getRootElement().element("result").element("totalCount").getTextTrim());
					if (totalCount > 0) {
						List<Element> elements= document.getRootElement().element("result").element("meta-data").elements();
						for (int i = 0; i < elements.size(); i++){
							String curTime = getCurrentSystemTime(pattern19);							
							String startTime = elements.get(i).element("StartTime").getTextTrim();
							String startTime19 = getWantDate(startTime, pattern19);
							String endTime = elements.get(i).element("EndTime").getTextTrim();
							String endTime19 = getWantDate(endTime, pattern19);
							log.debug("Current time = " + curTime);
							log.debug("Start time = " + startTime19);
							log.debug("End time = " + endTime19);
							if ((1 == compare_date(curTime, startTime19)) &&
								(1 == compare_date(endTime19, curTime))) {
								SubscribesInfo subInfo = new SubscribesInfo();
								String strSubId = elements.get(i).element("SubscribeID").getTextTrim();
								String url = elements.get(i).element("ReceiveAddr").getTextTrim();
								String title = elements.get(i).element("Title").getTextTrim();
								String subCategory = elements.get(i).element("SubCategory").getTextTrim();
								subInfo.setSubCategory(subCategory);
								subInfo.setReceiveAddr(url);
								subInfo.setSubscribeID(strSubId);
								subInfo.setTitle(title);
								subInfoList.add(subInfo);								
							}						
						}
						boolean motorVehicle = false;
						boolean tollgate = false;
						boolean laneB = false;
						if (subInfoList.size() > 0) {
							SgSubscribeNotificationsController.getIns().setSubInfoList(subInfoList);
							for (SubscribesInfo subInfo : subInfoList) {
								if (subInfo.getSubCategory().equals(SubscribeUtils.SUB_DEVICE_ACQUISITION_DATA)) {
									motorVehicle = true;																
								} else if (subInfo.getSubCategory().equals(SubscribeUtils.SUB_CHECKPOINT_STATUS_DIRECTORY)) {
									tollgate = true;									
								}else if (subInfo.getSubCategory().equals(SubscribeUtils.SUB_VEHICLE_LANE_STATUS_DIRECTORY)) {
									log.info("SgSubscribePeriodicCheckThread 车道状态及目录 !");	
									laneB = true;
								}
							}
						}
						SubscribeStatus.getIns().setSubscribedMotorVehicle(motorVehicle);
						SubscribeStatus.getIns().setSubscribedTollgate(tollgate);
						
						SubscribeStatus.getIns().setSubscribedLaneInfo(laneB);
					} else {
						SubscribeStatus.getIns().setSubscribedMotorVehicle(false);
						SubscribeStatus.getIns().setSubscribedTollgate(false);
						
						SubscribeStatus.getIns().setSubscribedLaneInfo(false);
					}
				}	
			}
			catch (DocumentException e){
				log.warn("SgSubscribePeriodicCheckThread DocumentException: " , e);
				//e.printStackTrace();
			}catch (Exception e) {
				log.warn("SgSubscribePeriodicCheckThread Exception: ",  e);
				//e.printStackTrace();
			}
			long finishTime = System.currentTimeMillis();
			log.debug("SgSubscribePeriodicCheckThread finishTime:" + finishTime);				
			long time = interval - finishTime + beginTime;
			if(time > 0) {
				try {
					log.debug("SgSubscribePeriodicCheckThread sleep:" + time + "begin");
					Thread.sleep(time);
					log.debug("SgSubscribePeriodicCheckThread sleep:" + time + "end");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	/**
	 * 设置线程运行状态
	 * 
	 * @param running
	 */
	public void setRunning(boolean running) {  
        this.running = running;  
    }
	
	/**
	 * 停止线程
	 */
	public void stopSubscribePeriodicCheck() {
		log.info("stop SystemThread : false");
        this.setRunning(false); 
	}
	
	/*
	 * 获取当前系统时间
	 * 
	 */
	public String getCurrentSystemTime(String pattern) {
		SimpleDateFormat dataFormat = new SimpleDateFormat(pattern);
		Date current = new Date();
		return dataFormat.format(current);
	}
	
	/*
	 * 获得想要的时间格式
	 * 
	 */
	public static String getWantDate(String dateStr, String wantFormat) {
		String strDate = "";
		SimpleDateFormat sdfe = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
		if (!"".equals(dateStr) && dateStr != null) {
			try {
				Date date1 = sdfe.parse(dateStr);
				SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
				strDate = sdf.format(date1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.warn("getWantDate Exception: ",  e);
				//e.printStackTrace();
			}
		}
		return strDate;
	}
	/*
	 * 比较时间大小
	 */
    public static int compare_date(String date1, String date2) {        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                //System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                //System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            //exception.printStackTrace();
        	log.warn("compare_date Exception: " , exception);
        }
        return 0;
    }
}
