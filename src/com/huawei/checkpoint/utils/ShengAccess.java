package com.huawei.checkpoint.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;

public class ShengAccess {
	static HttpClientUtil httpClientUtil = new HttpClientUtil();
	
	private static Logger log = Logger.getLogger(ShengAccess.class);
	
	public static String uploadVehicleInfo(JSONArray uploadInfo, String url) {
		String strHttpURI = url;
		//String strHttpURI = "http://10.1.26.80:65430/HttpServer/VIID/SubscribeNotifications";
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("json", uploadInfo.toString());
		log.debug("uploadVehicleInfo JSON = " + uploadInfo.toString());
		String strHttpResponse = httpClientUtil.SUB_POST(strHttpURI, createMap, "UTF-8");
		log.debug("uploadVehicleInfo strHttpResponse = " + strHttpResponse);
		return strHttpResponse;
	}
}
