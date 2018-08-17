package com.huawei.checkpoint;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.utils.ShengAccess;

import net.sf.json.JSONArray;

public class SgSubscribeNotificationsControllerThread implements Runnable{
	private static Logger log = Logger.getLogger(SgSubscribeNotificationsControllerThread.class);
	private JSONArray uploadInfo;
	private String url;

	public SgSubscribeNotificationsControllerThread(JSONArray uploadInfo, String url) {
		log.info("SgSubscribeNotificationsControllerThread");
		this.uploadInfo = uploadInfo;
		this.url = url;
	}
	
	public void run() {
		log.debug("SgSubscribeNotificationsControllerThread RUN " + this.url);
		ShengAccess.uploadVehicleInfo(this.uploadInfo, this.url);
	}
}
