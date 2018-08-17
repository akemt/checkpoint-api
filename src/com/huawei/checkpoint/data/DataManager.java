package com.huawei.checkpoint.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.utils.Config;

public class DataManager {
	private DataManager() {
	}

	private Logger log = Logger.getLogger(DataManager.class);
	private static volatile DataManager instance;

	public static DataManager getIns() {
		if (instance == null)
			synchronized (DataManager.class) {
				if (instance == null) {
					instance = new DataManager();
					instance.init();
				}
			}
		return instance;
	}
 

	private LinkedBlockingQueue<MessageUtils> queueStr;

	protected void init() {
 

		queueStr = new LinkedBlockingQueue<MessageUtils>();

		Config cf = Config.getIns();

		cf.getProperties();

	}
	 
	public void setNewData(String fileFullName, int platType) { //1:yushi;0:luoyang;2-14:县
		log.info("[DataManager] [setNewData] [set new file to queue!]");
		log.debug("[set new file to queue]:" + platType + ":" + fileFullName);
//		if (platType == 0) {
			//Parser parser = new LuoyangParser(fileFullName);
			MessageUtils msg = new MessageUtils(); 
			msg.setStrFilePathUrl(fileFullName);
			msg.setSgReDataType(platType);
			
			queueStr.add(msg);
			// queue.notify();
//		} else if (platType == 1) {
//			MessageUtils msg = new MessageUtils(); 
//			msg.setStrFilePathUrl(fileFullName);
//			msg.setSgReDataType(platType);
//			
//			queueStr.add(msg);
//		} else {
//
//		}

	}

	public int getData( List<MessageUtils>  result, int max) {
		// queue.wait(10);
		MessageUtils msg = null;
		int xmlNum =0;
//		try {
//			msg = queueStr.take(); 
//			result.add(msg);
//			if(max > 1){
//				xmlNum = queueStr.drainTo(result, max-1);	
//				log.info("[get new file to queue - xmlNum]"+xmlNum);
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return xmlNum+1;
		
			if(max > 0){
				xmlNum = queueStr.drainTo(result,max);
				//xmlNum = queueStr.drainTo(result, max);	
//				log.info("[get new file to queue - xmlNum]"+xmlNum);
			}
			return xmlNum;
	}

	/**
	 * 并发：把Xml数据放到生产者内
	 * 
	 * @param strXmlData
	 */
	public void setSgData(MessageUtils msg) {

		try {
			queueStr.put(msg);
			log.info("[DataManager] [setSgData] [set msg to queue!]");
			log.debug("[put msg to sg]:" + msg);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			log.warn("[put msg to sg excption]:" + msg, e);
		}
	}

	/**
	 * 并发：从生成者中读取XML数据到消费者
	 * 
	 * @return
	 */
	public MessageUtils getSgData() {
		// queue.wait(10);
		MessageUtils msg = null;
		try {
			msg = queueStr.take();
			log.info("[DataManager] [getSgData] [sg get msg from queue!]");
			log.debug("[sg get msg from queue]:" + msg);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			log.warn("[sg get msg from queue exception]:" + msg, e);
		}
		return msg;
	}

}
