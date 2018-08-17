package com.huawei.checkpoint.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {

	private Logger log = Logger.getLogger(Config.class);

	private Config() {
		initFlag = false;
	}

	private static volatile Config instance;
	private static DesUtils des = null;

	private boolean initFlag;

	public static Config getIns() {
		if (instance == null)
			synchronized (Config.class) {
				if (instance == null)
					instance = new Config();
			}
		return instance;
	}

	private Map<String, String> configs;

	public int getMonitorCnt() {
		String port = configs.get("monitor-threads");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 1;
	}

	public int getControllerCnt() {
		String port = configs.get("controller-threads");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 80;
	}
	
	public int getUploadFtpType() {
		String  str = configs.get("uploadFtpType");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 1;
	}

	@SuppressWarnings("rawtypes")
	public void getProperties() {

		if (initFlag)
			return;

		initFlag = true;

		Properties props = new Properties();
		configs = new HashMap<String, String>();
		String configFile = null;
		try {
			des = new DesUtils();
//			log.info("config.properties ：" + getClass().getResource("/config.properties"));
//			InputStream in = getClass().getResourceAsStream("/config.properties");
			 configFile = System.getProperty("com.huawei.checkpoint.utils.config");
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
			log.error("getconfig exception:"+configFile, e);
		}

	}

	public String getSysType() {
//		String str = configs.get("sys-type");
		String str = System.getProperty("com.huawei.checkpoint.utils.systype");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public String[] getYushiMonitorPath() {
		String list = configs.get("yushi-monitor-path");
		return list.split(";");
	}

	public String[] getCheckPointMonitorPath() {
		String list = configs.get("checkpoint-monitor-path");
		return list.split(";");
	}

	public String[] getCheckPointFTPPath() {
		String list = configs.get("checkpoint-ftp-path");
		if (list != null && list.length() > 0) {
			return list.split(";");
		} else {
			return null;
		}
	}

	public Map<String, String> getFtpAndMonitormap() {
		Map<String, String> map = new HashMap<String, String>();
		String[] monArr = getCheckPointMonitorPath();
		String slash = "";
		if (getCheckPointFTPPath().equals("")) {
			slash = "/";
			for (int i = 0; i < monArr.length; i++) {
				map.put(monArr[i], slash);
			}
		} else {
			String[] fTPArr = getCheckPointFTPPath();
			for (int i = 0; i < monArr.length; i++) {
				map.put(monArr[i], fTPArr[i]);
			}
		}
		return map;
	}

	public String getFtpPathByMonitorPath(String monKey) {
		Map<String, String> map = getFtpAndMonitormap();
		return map.get(monKey);
	}

	public String getVCNAddr() {
		String str = configs.get("vcn-addr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "127.0.0.1";
		}
	}

	public int getVCNPort() {

		String port = configs.get("vcn-port");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 9999;
	}

	public String getVCMAddr() {
		String str = configs.get("vcm-addr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "127.0.0.1";
		}
	}

	public String getVCNUser() {
		String str = configs.get("vcn-usr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public String getVCMUser() {
		String str = configs.get("vcm-usr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public String getVCNPass() {
		String str = configs.get("vcn-pass");
		if (str != null && !str.equals("")) {
			try {
				return des.decrypt(str);
			} catch (Exception e) {
			}
		} else {
			return "-1";
		}
		return "-1";
	}

	public String getVCMPass() {
		String str = configs.get("vcm-pass");
		if (str != null && !str.equals("")) {
			try {
				return des.decrypt(str);
			} catch (Exception e) {
			}
		} else {
			return "-1";
		}
		return "-1";
	}

	public String getSGAddr() {
		String str = configs.get("sg-addr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "127.0.0.1";
		}
	}

	public int getSGPort() {
		String port = configs.get("sg-port");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 9999;
	}

	public String getVCMCharset() {
		String str = configs.get("vcm-charset");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "UTF-8";
		}
	}

	/**
	 * 网关存储空间storage-Space-Name
	 * 
	 * @return
	 */
	public String getStorageName() {
		String str = configs.get("storage-name");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "no storage-name";
		}
	}

	/**
	 * 网关车辆信息表Table-Name
	 * 
	 * @return
	 */
	public String getTableNameVehInfo() {
		String str = configs.get("table-name-VehicleInfo");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "no table-name-VehicleInfo";
		}
	}

	/**
	 * 网关车辆信息表Table-空间大小
	 * 
	 * @return
	 */
	public int getTableQuotaVehInfo() {
		String str = configs.get("table-quota-VehicleInfo");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 1000;
		}
	}

	/**
	 *  网关车辆图片信息表Table-空间大小
	 * 
	 * @return
	 */
	public int getTableQuotaVPicInfo() {
		String str = configs.get("table-quota-VPicInfo");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 1000;
		}
	}

	/**
	 * 网关车辆图片信息表 Table-Name
	 * 
	 * @return
	 */
	public String getTableNameVPicInfo() {
		String str = configs.get("table-name-VPicInfo");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "no table-name-VPicInfo";
		}
	}
 

	/**
	 * 卡口LIST Table-Name
	 * 
	 * @return
	 */
	public String getTableNameChplist() {
		String str = configs.get("table-name-chplist");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "no table-name-chplist";
		}
	}

	/**
	 *卡口LISTTable-空间大小
	 * 
	 * @return
	 */
	public int getTableQuotaChplist() {
		String str = configs.get("table-quota-chplist");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 1000;
		}
	}
	
	/**
	 * 车辆备用信息 Table-Name
	 * 
	 * @return
	 */
	public String getTableNameVInfoExt() {
		String str = configs.get("table-name-VInfoExt");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "no table-name-chplist";
		}
	}

	/**
	 *车辆备用信息 Table-空间大小
	 * 
	 * @return
	 */
	public int getTableQuotaVInfoExt() {
		String str = configs.get("table-quota-VInfoExt");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 1000;
		}
	}

	/**
	 * 每次接受Queue接收数据的大小
	 * 
	 * @return
	 */
	public int getQueueDataNum() {
		String num = configs.get("queuedatanum");
		if (num.length() > 0)
			return Integer.parseInt(num);
		else
			return 1;
	}

	public int getVCMKeepAlive() {
		String port = configs.get("vcm-keepalive");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 0;
	}

	public int getClientSocketTime() {
		String port = configs.get("clientSocketTime");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 1;
	}

	public String getPPAddr() {
		String str = configs.get("pp-addr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "127.0.0.1";
		}
	}
	
	public int getPPPort() {
		String port = configs.get("pp-port");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 9999;
	}

	public int getIsSendSg() {
		String str = configs.get("isSendSg");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 99;
	}
	public String getPPUsr() {
		String str = configs.get("pp-usr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public String getPPPass() {
		String str = configs.get("pp-pass");
		if (str != null && !str.equals("")) {
			try {
				return des.decrypt(str);
			} catch (Exception e) {
			}
		} else {
			return "-1";
		}
		return "-1";
	}

	public String getDomCode() {
		String str = configs.get("DomCode");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}
	
	public String getClusterCode() {
		String str = configs.get("ClusterCode");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public int getMode() {
		String str = configs.get("mode");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 99;
		}
	}
	

	public String getNVRCode() {
		String str = configs.get("NVRCode");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public String getLyFtpAddr() {
		String str = configs.get("lyftp-addr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "127.0.0.1";
		}
	}

	public int getLyFtpPort() {
		String port = configs.get("lyftp-port");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 9999;
	}

	public String getLyFtpUsr() {
		String str = configs.get("lyftp-usr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}

	public String getLyFtpPass() {
		String str = configs.get("lyftp-pass");
		if (str != null && !str.equals("")) {
			try {
				return des.decrypt(str);
			} catch (Exception e) {
			}
		} else {
			return "-1";
		}
		return "-1";
	}
	
	/**
	 * 接受Queue接收数据的最大数量
	 * 
	 * @return
	 */
	public int getMaxNum() {
		String num = configs.get("MaxNum");
		if (num.length() > 0)
			return Integer.parseInt(num);
		else
			return 1;
	}

	/**
	 * 线程间隔时间
	 * 
	 * @return
	 */
	public int getSystemThreadInterval() {
		String num = configs.get("SystemThreadInterval");
		if (num.length() > 0)
			return Integer.parseInt(num);
		else
			return 1;
	}

	/**
	 * 每次接受Queue接收数据的大小
	 * 
	 * @return
	 */
	public int getPerThreadDataNum() {
		String num = configs.get("PerThreadDataNum");
		if (num.length() > 0)
			return Integer.parseInt(num);
		else
			return 1;
	}
	
	/**
	 * 宇视Server port
	 * 
	 * @return
	 */
	public int getYsServerPort() {
		String port = configs.get("ys-serverport");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 9999;
	}
	 
	/**
	 * 取线程的周期间隔
	 * 
	 * @return
	 */
	public int getSubscribeNotificationsInterval() {
		String num = configs.get("SubscribeNotificationsInterval");
		if (num.length() > 0)
			return Integer.parseInt(num);
		else
			return 1;
	}
	

	/**
	 * 取线程订阅Check的周期间隔
	 * 
	 * @return
	 */
	public int getSubscribeCheckInterval() {
		String num = configs.get("SubscribeCheckInterval");
		if (num.length() > 0)
			return Integer.parseInt(num);
		else
			return 1;
	}
	
	/**
	 * 每次取上传个数
	 * @return
	 */
	public int getSgUploadNum() {
		String uploadNum = configs.get("sg-uploadnum");
		if (uploadNum.length() > 0)
			return Integer.parseInt(uploadNum);
		else
			return 2000;	
	}
	
	/*
	 * 获取HttpServerIP
	 * 
	 */
	public String getHttpServerAddr() {
		String str = configs.get("httpServer-addr");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}
	
	/*
	 * 获取HttpServer Port
	 * 
	 */
	public String getHttpServerPort() {
		String str = configs.get("httpServer-port");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "-1";
		}
	}
	/**
	 * 订阅信息-table
	 * @return
	 */
	public String getTableNameSubInfo() {
		String str = configs.get("table-name-subInfo");
		if (str != null && !str.equals("")) {
			return str;
		} else {
			return "no table-name-chplist";
		}
	}

	/**
	 *订阅信息 Table-空间大小
	 * 
	 * @return
	 */
	public int getTableQuotaSubInfo() {
		String str = configs.get("table-quota-subInfo");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 1000;
		}
	}
	

	/**
	 *vcm-connect-timeout=5000
	*/
	public int getVCMConnectTimeout() {
		String str = configs.get("vcm-connect-timeout");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 5000;
		}
	}
	 /*
	 * vcm-socket-timeout=5000
	 */
	public int getVCMSocketTimeout() {
		String str = configs.get("vcm-socket-timeout");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 5000;
		}
	}
	/*
	 * vcm-connect-pool-max=200
	 */
	public int getVCMConnectPoolMax() {
		String str = configs.get("vcm-connect-pool-max");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 200;
		}
	}
	
	/*
	 * sheng-connect-timeout=5000
	 */
	public int getShengConnectTimeout() {
		String str = configs.get("sheng-connect-timeout");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 5000;
		}
	}
	
	 /*
	 * sheng-socket-timeout=5000
	 */
	public int getShengSocketTimeout() {
		String str = configs.get("sheng-socket-timeout");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 5000;
		}
	}
	
	/*
	 * sheng-connect-pool-max=200
	 */
	public int getShengConnectPoolMax() {
		String str = configs.get("sheng-connect-pool-max");
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return 200;
		}
	}
	
	/*
	 * 获取上传线程个数
	 */	
	public int getUploadCnt() {
		String port = configs.get("upload-threads");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 80;
	}
	
	/**
	 * 单条消息的最大长度
	 */	
	public int getTooLongFrame() {
		String port = configs.get("tooLongFrame");
		if (port.length() > 0)
			return Integer.parseInt(port);
		else
			return 1024;
	}
	
	/**
	 * log4j 内存大小
	 */	
	public int getLog4jBufferSize() {
		String str = configs.get("log4j-bufferSize");
		if (str.length() > 0)
			return Integer.parseInt(str)*1024;
		else
			return 1024 * 1024;
	}
	
	/**
	 * log4j 启用内存设置true.
	 */	
	public boolean getLog4jBufferedIO() {
		String str = configs.get("log4j-bufferedIO");
		if(str.equals("true") || str.equals("TRUE")){
			return true;
		}else{
			return false;
		} 
	}
	
	/**
	 * log4j 定时刷新间隔 ( S )
	 */	
	public int getLog4jFlushInterval() {
		String str = configs.get("log4j-flushInterval");
		if (str.length() > 0)
			return Integer.parseInt(str);
		else
			return 60;
	}
}
