package com.huawei.checkpoint.function;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.checkpoint.SgSubscribeNotificationsControllerThread;
import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.data.SubscribeNotificationsConstant;
import com.huawei.checkpoint.data.SubscribeStatus;
import com.huawei.checkpoint.data.SubscribesInfo;
import com.huawei.checkpoint.utils.CPListConfig;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.LaneListConfig;
import com.huawei.checkpoint.utils.SubscribeUtils;
import com.huawei.checkpoint.utils.ThreadPool;
import com.huawei.checkpoint.utils.VCMAccess;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SgSubscribeNotificationsController implements Runnable {
	private static Logger log = Logger.getLogger(SgSubscribeNotificationsController.class);

	private static volatile SgSubscribeNotificationsController instance;
	public static SgSubscribeNotificationsController getIns() {
		if (instance == null)
			synchronized(SgSubscribeNotificationsController.class) {
				if (instance == null)
					instance = new SgSubscribeNotificationsController();
			}
		return instance;
	}
	// 标记线程是否需要运行
	private volatile boolean running = true;

	private static final String pattern19 = "yyyy-MM-dd HH:mm:ss";
	private static final String pattern14 = "yyyyMMddHHmmss";

	private static final String orgCode = "410300000000";
	private static final String typeCode = "04";
	private static final String serialNumber = "00";

	List <SubscribesInfo> subInfoList = null;

	public List<SubscribesInfo> getSubInfoList() {
		return subInfoList;
	}

	public void setSubInfoList(List<SubscribesInfo> subInfoList) {
		this.subInfoList = subInfoList;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Config cf = Config.getIns();
		String strCookie = SystemManager.getIns().getCookie();
		long interval = cf.getSubscribeNotificationsInterval();

		String startTime = getCurrentSystemTime(pattern19);	
		String endTime = startTime;	
		while(running) {		
			long beginTime = System.currentTimeMillis();
			log.debug("SubscribeNotifications beginTime:" + beginTime);
			boolean tollgateUpdateStatus = SubscribeStatus.getIns().isTollgateUpdateStatus();
			boolean subScribedTollgate = SubscribeStatus.getIns().isSubscribedTollgate();
			boolean subScribedMotorVehicle = SubscribeStatus.getIns().isSubscribedMotorVehicle();
			
			log.info("SubscribeNotifications start >>>>>>>>>>>>>>>>>>>>" );
			log.debug("SubscribeNotifications tollgateUpdateStatus:" + tollgateUpdateStatus +";subScribedTollgate:"+ subScribedTollgate+";subScribedMotorVehicle:"+subScribedMotorVehicle);
			
			if (tollgateUpdateStatus & subScribedTollgate) {
				JSONArray uploadInfoList = null;
				for (SubscribesInfo subInfo : subInfoList){
					if (subInfo.getSubCategory().equals(SubscribeUtils.SUB_CHECKPOINT_STATUS_DIRECTORY)) {
						uploadInfoList = new JSONArray();
						JSONObject uploadInfo = new JSONObject();
						String strTriggerTime = getCurrentSystemTime(pattern19);
						String strCurTime = getCurrentSystemTime(pattern14);
						String notifyID = orgCode + strCurTime + typeCode + serialNumber;
						uploadInfo.put("NotificationID", notifyID);
						uploadInfo.put("TriggerTime", strTriggerTime);
						uploadInfo.put("InfoIDs", "TollgateInfo");
						uploadInfo.put("SubscribeID", subInfo.getSubscribeID());
						uploadInfo.put("Title", subInfo.getTitle());
						
						uploadInfo.put("TollgateList", CPListConfig.getIns().getALLList());
						uploadInfoList.add(uploadInfo);

						SgSubscribeNotificationsControllerThread ssNCT = new SgSubscribeNotificationsControllerThread(uploadInfoList, subInfo.getReceiveAddr());
						log.info("execute Upload Info List, subinfo:" + subInfo);
						ThreadPool.executeUpload(ssNCT);
						SubscribeStatus.getIns().setTollgateUpdateStatus(false);
					}
				}				
			}

			if (subScribedMotorVehicle) {
				//检索VCM，是否有订阅内容				
				if (!endTime.equals(startTime)) {
					boolean isHandle = false;
					int totalCount = 0;
					String reStr = VCMAccess.queryVehInfoByTime(strCookie, 1, startTime, endTime);					
					try {
						Document document = DocumentHelper.parseText(reStr);
						String code = document.getRootElement().element("result").element("code").getTextTrim();
						if(code.equals("0")){
							totalCount = Integer.parseInt(document.getRootElement().element("result").element("totalCount").getTextTrim());
							log.info("SubscribeNotifications queryVehInfoByTime TotalCount:" + totalCount);
							if (totalCount > 0) {
								isHandle = true;
								//log.debug("SubscribeNotifications isHandle:" + isHandle);
							}
						}		
					}
					catch (DocumentException e){
						log.warn("SubscribeNotifications DocumentException:" , e);
					}catch (Exception e) {
						log.warn("SubscribeNotifications Exception: ", e);
						//e.printStackTrace();
					}
					if (isHandle) {
						reStr = VCMAccess.queryVehInfoByTime(strCookie, totalCount, startTime, endTime);
						JSONArray uploadInfoList = null;
						JSONObject uploadInfo = new JSONObject();
						try {
							//							log.warn("SubscribeNotifications step1 start");
							Document document = DocumentHelper.parseText(reStr);
							Element rootElement = document.getRootElement();
							//							log.warn("SubscribeNotifications step2 start");
							Element result = rootElement.element("result");
							String strCode = result.element("code").getTextTrim();
							log.info("SubscribeNotifications queryVehInfoByTime 2 strCode:" + strCode);
							if (strCode.equals("0")) {
								Element metadata = result.element("meta-data");
								List<Element> elements = metadata.elements();
								if (0 < elements.size()) {
									log.info("SubscribeNotifications vcmToNotitications make uploadInfo" );
									//									log.warn("SubscribeNotifications SubscribeNotificationsConstant start");
									uploadInfo = SubscribeNotificationsConstant.vcmToNotitications(elements);
									//									log.warn("SubscribeNotifications SubscribeNotificationsConstant end ");
								}
							}
						}
						catch (DocumentException e){
							log.warn("DocumentException: " , e);
							//e.printStackTrace();
							isHandle = false;
						}
						catch (Exception e) {
							log.warn("Exception: " , e);
							//e.printStackTrace();
							isHandle = false;
						}
						
						if (isHandle) {
							//						log.warn("SubscribeNotifications step3 start");
							String strTriggerTime = getCurrentSystemTime(pattern19);
							String strCurTime = getCurrentSystemTime(pattern14);
							String notifyID = orgCode + strCurTime + typeCode + serialNumber;
							//						log.warn("SubscribeNotifications strTriggerTime" + strTriggerTime);
							uploadInfo.put("NotificationID", notifyID);
							uploadInfo.put("TriggerTime", strTriggerTime);
							uploadInfo.put("InfoIDs", "VehicleInfo");
							//						log.warn("SubscribeNotifications NotificationID" + notifyID);
							if (subInfoList != null) {
								log.debug("subInfoList size = " + subInfoList.size());
								log.debug("subInfoList = " + subInfoList);
								for (SubscribesInfo subInfo : subInfoList){
									if (subInfo.getSubCategory().equals(SubscribeUtils.SUB_DEVICE_ACQUISITION_DATA)) {
										uploadInfoList = new JSONArray();
										uploadInfo.put("SubscribeID", subInfo.getSubscribeID());
										uploadInfo.put("Title", subInfo.getTitle());					

										uploadInfoList.add(uploadInfo);
										log.info("executeUpload SgSubscribeNotificationsControllerThread,subinfo:" + subInfo);
										//								log.warn("new SgSubscribeNotificationsControllerThread");
										SgSubscribeNotificationsControllerThread ssNCT = new SgSubscribeNotificationsControllerThread(uploadInfoList, subInfo.getReceiveAddr());
										//log.info("executeUpload");
										ThreadPool.executeUpload(ssNCT);
									}
								}
							} else {
								log.warn("sub info list is NULL");
							}
						}
					}
				}
			}
			
			boolean laneUpdateStatus = SubscribeStatus.getIns().isLaneUpdateStatus();
			boolean subscribedLaneInfo = SubscribeStatus.getIns().isSubscribedLaneInfo();
			log.debug("SubscribeNotifications laneUpdateStatus:" + laneUpdateStatus +";subscribedLaneInfo:"+ subscribedLaneInfo);
			//车道
			if (laneUpdateStatus & subscribedLaneInfo) {
				JSONArray uploadInfoList = null;
				for (SubscribesInfo subInfo : subInfoList){
					if (subInfo.getSubCategory().equals(SubscribeUtils.SUB_VEHICLE_LANE_STATUS_DIRECTORY)) {
						uploadInfoList = new JSONArray();
						JSONObject uploadInfo = new JSONObject();
						String strTriggerTime = getCurrentSystemTime(pattern19);
						String strCurTime = getCurrentSystemTime(pattern14);
						String notifyID = orgCode + strCurTime + typeCode + serialNumber;
						uploadInfo.put("NotificationID", notifyID);
						uploadInfo.put("TriggerTime", strTriggerTime);
						uploadInfo.put("InfoIDs", "TollgateInfo");
						uploadInfo.put("SubscribeID", subInfo.getSubscribeID());
						uploadInfo.put("Title", subInfo.getTitle());
						
						uploadInfo.put("LaneList", LaneListConfig.getIns().getALLList());
						uploadInfoList.add(uploadInfo);

						SgSubscribeNotificationsControllerThread ssNCT = new SgSubscribeNotificationsControllerThread(uploadInfoList, subInfo.getReceiveAddr());
						log.info("execute Upload Info List, subLaneinfo:" + subInfo);
						ThreadPool.executeUpload(ssNCT);
						SubscribeStatus.getIns().setLaneUpdateStatus(false);
						break;
					}
				}				
			}
			
			long finishTime = System.currentTimeMillis();
			//log.debug("SubscribeNotifications finishTime:" + finishTime);		
			log.info("SubscribeNotifications end <<<<<<<<<<<<<<<<<<<<<<<<" );
			long time = interval - finishTime + beginTime;
			if(time > 0) {
				try {
					log.debug("SubscribeNotifications sleep:" + time + "begin");
					Thread.sleep(time);
					log.debug("SubscribeNotifications sleep:" + time + "end");
				} catch (InterruptedException e) {
					log.warn("InterruptedException: " ,e);
					//e.printStackTrace();
				}
			} 
			startTime = endTime;
			endTime = getCurrentSystemTime(pattern19);	
			
		}
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
	public void stopSubscribeNotifications() {
		log.info("stop SystemThread : false");
		this.setRunning(false); 
	}

	/**
	 * 线程状态
	 */
	public boolean isRunning() {
		return this.running;
	}
}

