package com.huawei.checkpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.data.DataManager;
import com.huawei.checkpoint.data.MessageUtils;
import com.huawei.checkpoint.function.AgController;
import com.huawei.checkpoint.function.SgPassThroughController;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.ThreadPool;

public class SystemThread implements Runnable{
	
	private Logger log = Logger.getLogger(SystemThread.class);
	
	// 标记线程是否需要运行
	private volatile boolean running = true;  
	
	public void run() {
		log.info("start  SystemThread!");
		Config cf = Config.getIns();
		//判断是接入网关ag-0 还是共享网关sg-1
		String strSysType = cf.getSysType();
		int num = cf.getPerThreadDataNum();
		int maxNum = cf.getMaxNum();
		long interval = cf.getSystemThreadInterval();
		
		ArrayList<MessageUtils> msgList = new ArrayList<MessageUtils>();
		while(running) {
			//log.debug("start SystemThread");
			if(strSysType != null && strSysType.equals(CheckPointStaticUtils.GATEWAY_AG)){//接入网关ag-0
				
				long beginTime = System.currentTimeMillis();
				
				msgList.clear();

				int cnt  = DataManager.getIns().getData(msgList, maxNum);
				int i = 0;
//				log.debug("SystemThread get data num:" + cnt);
				for (i = 0; i < cnt / num; i++) {

					MessageUtils msg = null;
					ArrayList<String> strList = new ArrayList<String>();
					// String[] filePath = null;
					for (int j = 0; j < num; j++) {
						msg = msgList.get(i * num + j);
						strList.add(msg.getStrFilePathUrl());

					}
					// filePath = strList.toArray(new String[strList.size()]);
					// AgController ctrl = new
					// AgController(filePath,msg.getSgReDataType());
					if (msg.getSgReDataType() == 1) {

						List<String> camIDListAll = new ArrayList<String>();
						// 把XML文件名分组，相同的CamID起一个线程。
						for (int d = 0; d < strList.size(); d++) {
							String strXmlName = strList.get(d);
							String strCamID = strXmlName.substring(0, strXmlName.lastIndexOf("-"));
							camIDListAll.add(strCamID);
						}
						// 去掉重复的CamID
						List<String> camIDListRemove = CheckPointStaticUtils.removeDuplicateWithOrder(camIDListAll);
						for (int m = 0; m < camIDListRemove.size(); m++) {
							String mCamID = camIDListRemove.get(m);
							ArrayList<String> listNew = new ArrayList<String>();
							for (int n = 0; n < camIDListAll.size(); n++) {
								String jtems = camIDListAll.get(n);
								if (mCamID.equals(jtems)) {
									listNew.add(strList.get(n));
								}
							}
							// 查找一样的CamID ,启动一个线程
							AgControllerThread ct = new AgControllerThread(listNew, msg.getSgReDataType());
							ThreadPool.executeCtrl(ct);
						}
					}else if (msg.getSgReDataType() == 0) {

						AgControllerThread ct = new AgControllerThread(strList, msg.getSgReDataType());
						ThreadPool.executeCtrl(ct);
					}

					// log.debug("SystemThread start thread:" + i +" : files" +
					// strList);
				}
				int k = cnt%num;
				if (k != 0) {
					MessageUtils msg = null;
					ArrayList<String> strList = new ArrayList<String>();
					//String[] filePath = null;
					for(int j = 0; j < k ;j++){
						msg = msgList.get(i*num+j);
						strList.add(msg.getStrFilePathUrl());
						
					}
					
					if (msg.getSgReDataType() == 1) {

						List<String> camIDListAll = new ArrayList<String>();
						// 把XML文件名分组，相同的CamID起一个线程。
						for (int d = 0; d < strList.size(); d++) {
							String strXmlName = strList.get(d);
							String strCamID = strXmlName.substring(0, strXmlName.lastIndexOf("-"));
							camIDListAll.add(strCamID);
						}
						// 去掉重复的CamID
						List<String> camIDListRemove = CheckPointStaticUtils.removeDuplicateWithOrder(camIDListAll);
						for (int m = 0; m < camIDListRemove.size(); m++) {
							String mCamID = camIDListRemove.get(m);
							ArrayList<String> listNew = new ArrayList<String>();
							for (int n = 0; n < camIDListAll.size(); n++) {
								String jtems = camIDListAll.get(n);
								if (mCamID.equals(jtems)) {
									listNew.add(strList.get(n));
								}
							}
							// 查找一样的CamID ,启动一个线程
							AgControllerThread ct = new AgControllerThread(listNew, msg.getSgReDataType());
							ThreadPool.executeCtrl(ct);
						}
					}else if (msg.getSgReDataType() == 0) {

						AgControllerThread ct = new AgControllerThread(strList, msg.getSgReDataType());
						ThreadPool.executeCtrl(ct);
					}
					//filePath = strList.toArray(new String[strList.size()]);
	//					AgController ctrl = new AgController(filePath,msg.getSgReDataType());
//					AgControllerThread ct = new AgControllerThread(strList,msg.getSgReDataType());
//					ThreadPool.executeCtrl(ct);
//					log.debug("SystemThread start thread:" + i +" : files" + strList);
				}
				
				long finishTime = System.currentTimeMillis();
				
				long time = interval - finishTime+beginTime;
				if(time > 0) {
					try {
//						log.debug("SystemThread sleep:" + time + "begin");
						Thread.sleep(time);
//						log.debug("SystemThread sleep:" + time + "end");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else if(strSysType != null && strSysType.equals(CheckPointStaticUtils.GATEWAY_SG)){//共享网关sg
				//监听Server Socket中数据变化，当有数据变化。则启动SgController
				MessageUtils msg = DataManager.getIns().getSgData();
				
				if (msg != null) {
					SgPassThroughController ctrl = new SgPassThroughController(msg);
					SgPassThroughControllerThread ct = new SgPassThroughControllerThread(ctrl);
					ThreadPool.executeCtrl(ct);
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
	 * 停止线程SystemThread
	 */
	public void stopSystemThread() {
		log.info("stop SystemThread : false");
        this.setRunning(false); 
	}

}
