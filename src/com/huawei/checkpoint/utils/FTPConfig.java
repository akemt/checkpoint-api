package com.huawei.checkpoint.utils; 
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FTPConfig {

	private Logger log = Logger.getLogger(FTPConfig.class);

	private FTPConfig() {
		initFlag = false;
	}

	private static volatile FTPConfig instance;

	private boolean initFlag;

	public static FTPConfig getIns() {
		if (instance == null)
			synchronized (FTPConfig.class) {
				if (instance == null)
					instance = new FTPConfig();
			}
		return instance;
	}

	private Map<String, String> configs;

	@SuppressWarnings("rawtypes")
	public void getProperties() {

		if (initFlag)
			return;

		initFlag = true;

		Properties props = new Properties();
		configs = new HashMap<String, String>();
		String configFile = null;
		try {
			// log.info("config.properties ：" +
			// getClass().getResource("/config.properties"));
//			InputStream in = getClass().getResourceAsStream("/ftp-config.properties");
			 configFile = System.getProperty("com.huawei.checkpoint.utils.ftpconfig");
			 File f = new File(configFile);
			 InputStream in = new FileInputStream(f);
			props.load(in);
			Enumeration en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String property = props.getProperty(key);
				if (property != null && !property.equals("")) {
					configs.put(key, property.trim());
				} else {
					configs.put(key, property);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			log.error("read config file:" + configFile + " exception", e);
		}

	}

	/**
	 * FTP List
	 * 
	 * @return
	 */
	public String getFTPList() {
		String str = configs.get("ftp-list");
		return str;
	} 
	
	/**
	 * ConnectTimeout
	 * @return
	 */
	public int getFtpConnectTimeout() {
		String str = configs.get("ftp-connectTimeout");
		if (str.length() > 0)
			return Integer.parseInt(str)*1000;
		else
			return 1000;
	}
	
	/**
	 * FtpDataTimeout
	 * @return
	 */
	public int getFtpDataTimeout() {
		String str = configs.get("ftp-dataTimeout");
		if (str.length() > 0)
			return Integer.parseInt(str)*1000;
		else
			return 1000;
	}

	/**
	 * FtpQueuePoolNum
	 * @return
	 */
	public int getFtpQueuePoolNum() {
		String str = configs.get("ftp-queuepoolnum");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 1;
	}
	
	
	/**
	 * FtpKeepAliveTime
	 * @return
	 */
	public int getFtpKeepAliveTime() {
		String str = configs.get("ftp-keepAliveTime");
		if (str.length() > 0)
			return Integer.parseInt(str)*1000;
		else
			return 1000;
	}
	
	/**
	 * getFtpQueueTimeOut
	 * @return
	 */
	public int getFtpQueueTimeOut() {
		String str = configs.get("ftp-queueTimeOut");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 1;
	}
	public int getFtpBufferSize() {
		String str = configs.get("ftp-bufferSize");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 1024;
	}
	
	
	public int getOperTimeout() {
		String str = configs.get("ftp-OperTimeout");
		if (str.length() > 0)
			return Integer.parseInt(str)*1000;
		else
			return 1000;
	}
	
	public int getFixedThreadPoolNum() {
		String str = configs.get("ftp-FixedThreadPoolNum");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 10;
	}
	
}
