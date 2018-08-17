package com.huawei.checkpoint;
 
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.huawei.checkpoint.data.FTPClientConfigure;
import com.huawei.checkpoint.data.FileMonitor;
import com.huawei.checkpoint.data.ProxyServer;
import com.huawei.checkpoint.data.SendClient;
import com.huawei.checkpoint.data.YSProxyServer;
import com.huawei.checkpoint.function.SgSubscribeNotificationsController;
import com.huawei.checkpoint.function.SgSubscribePeriodicCheckThread;
import com.huawei.checkpoint.utils.CPListConfig;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.FTPClientPool;
import com.huawei.checkpoint.utils.FTPClientWraper;
import com.huawei.checkpoint.utils.FTPConfig;
import com.huawei.checkpoint.utils.FtpClientFactory;
import com.huawei.checkpoint.utils.LaneListConfig;
import com.huawei.checkpoint.utils.ThreadPool;
import com.huawei.checkpoint.utils.VCMAccess;
import com.huawei.checkpoint.utils.VCNAccess;
import com.huawei.checkpoint.utils.VCNAccess.CLibrary;
import com.sun.jna.ptr.PointerByReference;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class SystemManager  implements SignalHandler {
	private SystemManager() {
		
	}
	private Logger log = Logger.getLogger(SystemManager.class); 
	private static volatile SystemManager instance;
	 
//	private Runnable kt;
	private KeepAliveThread kt;
//	private Runnable st;
	private SystemThread st;
	private String cookie;

	private PointerByReference handle;
	private int sessionID;
	
	private String sysType;
	/** 宇视监控路径 */
	private String [] ysPathList;
	
	private boolean istermflag = false;
	
	public static SystemManager getIns() {
		if (instance == null)
			synchronized(SystemManager.class) {
				if (instance == null)
					instance = new SystemManager();
			}
		return instance;
	}
	
	public PointerByReference getHandle(){
		return handle;
	}
	
	public int getSessionID(){
		return sessionID;
	}
	
	public String getCookie() {
		return cookie;
	}
	
	public String[] getYsPathList() {
		return ysPathList;
	}
	
	public synchronized void setCookie(String strCookie) {
		//线程同步
		cookie = strCookie;
	}
	
	private List<Map<String,Object>> listMonitor = null;
	
	private List<Map<String,Object>> ysListMonitor = null;
	
	private ProxyServer proxyServer = null;
	
	private YSProxyServer ysProxyServer = null;
	
	private boolean lyMonitorPathFlag = false;
	
	private boolean ysMonitorPathFlag = false;
	
	private Hashtable<String, FTPClientPool> ftpHT = new Hashtable<String, FTPClientPool>();;
	
	public Hashtable<String, FTPClientPool> getFtpHT() {
		return ftpHT;
	}
	

	private SgSubscribeNotificationsController ssNotiCtrl;
	
	private SgSubscribePeriodicCheckThread ssPCT;
	
	
	private JSONObject cpListJson;
	
	public JSONObject getcCPListJson() {
		return cpListJson;
	}
	
	private Hashtable<String, String> xtbhHT = null;
	
	public Hashtable<String, String> getXTBHHT() {
		return xtbhHT;
	}
	
	
	private FtpKeepAliveThread ftpkt;
	
	@SuppressWarnings("unused")
	public boolean init() {
		
		log.info("[init] begin");
		//get configure from config file
		 Config cf = Config.getIns();
		cf.getProperties();
		int cnt = cf.getMonitorCnt();
		int cnt1 = cf.getControllerCnt();
		int upLoadCnt = cf.getUploadCnt();
		ThreadPool.init(cnt, cnt1, upLoadCnt);
		
		sysType = cf.getSysType();
		if( sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_AG)) { //ag-接入网关			
			//start Monitor thread
			
			if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0){//版本控制  1 - 启动

				log.info("[init] [ag] [begin] [luoyang monitor file!]");
				Map<String,Object> map = new HashMap<String,Object>();
				String [] cpList = cf.getCheckPointMonitorPath();
				for (String tmp : cpList) {
					if(tmp.length() > 0) {
						log.debug("[init] [ag]  luoyang monitor file:" + tmp);
						listMonitor = new ArrayList<Map<String,Object>>();
						lyMonitorPathFlag = true;
						FileMonitor fm = new FileMonitor(tmp, 0);
						fm.startInotify();
						//MonitorThread mt = new MonitorThread(fm);
						//ThreadPool.executeMT(mt);
						map.put("monitorPath", tmp);
						map.put("fileMonitor", fm);
						
						listMonitor.add(map);
					}
				} 
				log.info("[init] [ag]  [end] [luoyang monitor file!]");
				// 遍历chp-xtbh-list数组
				log.info("[init] [ag]  [begin] [FTPConfig- Path,XTBH!]");
				FTPConfig ftpCf = FTPConfig.getIns();
				ftpCf.getProperties();
				JSONArray json = JSONArray.fromObject(ftpCf.getFTPList());
				int jNum = json.size();
				if (jNum > 0) { 
					 xtbhHT = new Hashtable<String, String>();
					for (int i = 0; i < jNum; i++) {
						JSONObject jsn = json.getJSONObject(i);
						xtbhHT.put(jsn.getString("ftp-path"), jsn.getString("XTBH"));
					}
				}
				log.info("[init] [ag]  [end] [FTPConfig- Path,XTBH!]");
				
			}
			if((VersionControl.VERSION_RELEASE & VersionControl.YUSHI_CAMERA) != 0){//版本控制  1 - 启动
				log.info("[init] [ag]  [begin] [yushi monitor file!]");
				Map<String,Object> map = new HashMap<String,Object>();
				String [] ysList = cf.getYushiMonitorPath();
				for (String tmp : ysList) {
					if(tmp.length() > 0) {
						ysListMonitor = new ArrayList<Map<String,Object>>();
						log.debug("[init] [ag]  yushi monitor file:" + tmp);
						ysMonitorPathFlag = true;
						ysPathList = ysList;
						FileMonitor fm = new FileMonitor(tmp, 1);
						fm.startInotify();
						//MonitorThread mt = new MonitorThread(fm);
						//ThreadPool.executeMT(mt);
						map.put("monitorPath", tmp);
						map.put("fileMonitor", fm);
						ysListMonitor.add(map);
					}
				}
				log.info("[init] [ag]  [end] [yushi monitor file!]");
			}
		}else if(sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_SG)){//sg-共享网关
			
			if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0){//版本控制  1 - 启动
				int isSend = cf.getIsSendSg();
				if(isSend == 1 && cf.getUploadFtpType() == CheckPointStaticUtils.DIRECT_UPLOAD){
					// 遍历FTP数组
					FTPConfig ftpCf = FTPConfig.getIns();
					ftpCf.getProperties();
					
					FTPClientWraper.init(ftpCf.getFixedThreadPoolNum(),ftpCf.getFtpDataTimeout());
					
					log.info("[init] [sg]  [begin] [FTPConfig- IP,PORT,USR,PASS!]");
					JSONArray json = JSONArray.fromObject(ftpCf.getFTPList());
					int jNum = json.size();
					if (jNum > 0) {
						FtpClientFactory factory = null;
						FTPClientPool ftpPool = null;
						
						for (int i = 0; i < jNum; i++) {
							JSONObject jsn = json.getJSONObject(i);
//							log.debug("ftp-path:" + jsn.get("ftp-path") + "ftp-ip:" + jsn.get("ftp-ip") + ";ftp-port:"
//									+ jsn.get("ftp-port") + ";ftp-usr:" + jsn.get("ftp-usr") + ";ftp-pass:"
//									+ jsn.get("ftp-pass"));
							// 初始号FTPClientPoll
							FTPClientConfigure config = new FTPClientConfigure();
							config.setHost(String.valueOf(jsn.get("ftp-ip")));
							config.setPort(Integer.valueOf(jsn.get("ftp-port").toString()));
							config.setUsername(String.valueOf(jsn.get("ftp-usr")));
							config.setPassword(String.valueOf(jsn.get("ftp-pass")));
							config.setTransferFileType(FTPClient.BINARY_FILE_TYPE);
							// config.setEncoding("gbk");
							config.setPassiveMode("true");  
							config.setConnectTimeout(ftpCf.getFtpConnectTimeout());
							config.setDataTimeout(ftpCf.getFtpDataTimeout());
							config.setBufferSize(ftpCf.getFtpBufferSize());
							config.setDefaultTimeout(ftpCf.getOperTimeout());
							
							factory = new FtpClientFactory(config);
							try {
								ftpPool = new FTPClientPool(ftpCf.getFtpQueuePoolNum(), factory,ftpCf.getFtpQueueTimeOut());
							} catch (Exception e) {
								//e.printStackTrace();
								log.warn("create FTPClientPool exception ip:"+config.getHost());
								log.warn("create FTPClientPool exception port:"+config.getPort());
								log.warn("create FTPClientPool exception user:"+config.getUsername());
								log.warn("create FTPClientPool exception",e);
							}

							ftpHT.put(String.valueOf(jsn.get("ftp-path")), ftpPool);
						}
					}
					log.info("[init] [sg]  [end] [FTPConfig- IP,PORT,USR,PASS!]");
					//
					ftpkt = new FtpKeepAliveThread();
					Thread threadKT = new Thread(ftpkt);
					threadKT.start(); 
				} 
			} 
			
			if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0 && 
					(VersionControl.VERSION_RELEASE & VersionControl.HENAN_SHENG) != 0 ){
				log.info("[init] [sg]  [begin] [CPListConfig!]");
				CPListConfig cplCf = CPListConfig.getIns();
				cplCf.intAllList();
				log.info("[init] [sg]  [end] [CPListConfig!]");
				
				log.info("[init] [sg]  [begin] [LaneListConfig!]");
				LaneListConfig laneCf = LaneListConfig.getIns();
				laneCf.intAllList();
				log.info("[init] [sg]  [end] [LaneListConfig!]");
			}
			
		} 
		
		
		st = new SystemThread();
		Thread threadST = new Thread(st);
		threadST.start();
		
		boolean ret = false;
		 /**  ---------VCM-----START--------   */
//		if (ysMonitorPathFlag || lyMonitorPathFlag) {// step1、step2 需要VCM。step3
														// 不需要VCM
			cookie = loginVCM();
			if (cookie == null) {
				log.error("VCM Login Error");
				return false;
			}
			if (sysType.compareTo(CheckPointStaticUtils.GATEWAY_AG) == 0) { // ag-接入网关
				initVCM(cookie, cf);
			}
			log.info("VCM Login And Init Success!");
//		}
		/**  -----------VCM-----END--------   */
		
		if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0 && 
				(VersionControl.VERSION_RELEASE & VersionControl.HENAN_SHENG) != 0 ){//版本控制  1 - 启动
			if(sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_SG)){//sg-共享网关

				//订阅通知线程启动
				ssNotiCtrl = SgSubscribeNotificationsController.getIns();
				Thread threadSsNotiCtrl = new Thread(ssNotiCtrl);
				log.info("SubscribeNotificationsController Start");
				threadSsNotiCtrl.start();
				
				ssPCT = new SgSubscribePeriodicCheckThread();
				Thread threadSsPCT = new Thread(ssPCT);
				log.info("SubscribePeriodicCheck Start");
				threadSsPCT.start();	
			}		
		}
		
		
		/** -----------VCN-----START------   */ 
		ret = VCNAccess.initSDK();
		if (!ret) {
			log.error("vcn sdk init error:" + ret);
			return ret;
		}
		sessionID = loginVCN();
		if (sessionID < 0) {
			log.error("login VCN error, sessionID:" + sessionID);
			return false;
		}else{
			log.debug("login VCN  success:" + sessionID);
		}  
		log.info("VCN Init And Login Success!");
		/** -----------VCN-----END------   */ 
		
		
		
		
//		int retStart = initVCN(sessionID);
//		if(retStart != CheckPointStaticUtils.LOGIN_SUCCESS){
//			log.error("startUploadVehicleImg error:" + sessionID);
//			return false;
//		}else{
//			log.debug("startUploadVehicleImg success:" + sessionID);
//		}
		
		//test code 
//		String  pNVRCode = cf.getDomCode();
//		VCNAccess.testPreset(sessionID,pNVRCode);
		
		//String  pNVRCode = "9cf757b7abb342aea9a8b010ec161462";
//		String  pNVRCode = cf.getDomCode();
//		File fl = new File("/root/checkpoint/123.jpg");
//		long len = fl.length();
//		byte [] fileBuffer = new byte[(int) len];
//		
//		FileInputStream fis;
//		try {
//			fis = new FileInputStream("/root/checkpoint/123.jpg");
//			try {
//				fis.read(fileBuffer, 0, (int) len);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		VCNAccess.uploadVehicleImg(sessionID, fileBuffer, pNVRCode, (int) len);
		
		//initialize vcn
//				initVCN();
		/** -----------VCN-----START------   */
		
		if(ysMonitorPathFlag || lyMonitorPathFlag){//step1、step2 需要VCM。step3 不需要VCM
			kt = new KeepAliveThread();
			Thread threadKT = new Thread(kt);
			threadKT.start();
		}
		log.info("[init] success end");
		
		if( sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_AG)) { //ag-接入网关	
			//start SendClient 
			if(lyMonitorPathFlag){
				int i = 0;
				while(true){
					i++;
					 
					try {
						if(i > cf.getClientSocketTime()) {
							//每10次执行一下连接Socket
							break;
						}
						else{
							SendClient.initClient();
							log.info("[start SendClient Socket] success!");
						}
					} catch (ClosedChannelException e) {
						log.warn("[start SendClient Socket] fail!", e);
						//e.printStackTrace();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						continue;  
					} catch (IOException e) {
						log.warn("[start SendClient Socket] fail!",e);
						//e.printStackTrace();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						continue;  
					}
					break;
				}
				
				log.info("Start SendClient Socket Success!");
				
			}
			
			if(ysMonitorPathFlag){
				ysProxyServer = new YSProxyServer(cf.getYsServerPort());
				try {
					ysProxyServer.run();
				} catch (Exception e) {
					log.warn("[start YuShi ProxyServer Socket] fail!",e);
					//e.printStackTrace();
				}
			}
			
		}else{
			//start ProxyServer 
			proxyServer = new ProxyServer(cf.getSGPort(),cf.getTooLongFrame());
			try {
				proxyServer.run();
			} catch (Exception e) {
				log.warn("[start ProxyServer Socket] fail!",e);
				//e.printStackTrace();
			}
			
			log.info("Stop ProxyServer Socket Success!");
		}
		log.info("SystemManager Init Success!");
		return true;
				
	}
	public boolean start(int type) {
		
		if (type == CheckPointStaticUtils.GATEWAY_AG_TYPE) {
			//access gateway
		}
		else if (type == CheckPointStaticUtils.GATEWAY_SG_TYPE) {
			//share gateway
		}
		return false;
	}
	
	private String loginVCM()  {
		String srtCookit = VCMAccess.vcmApplicationLogin();
		log.debug("VCM-登录成功返回的Cookie : "+srtCookit);
		return srtCookit;
	}
	
	private int loginVCN() {
		int sID = -1;
		 Config cf = Config.getIns();
		cf.getProperties();
		String ip = cf.getVCNAddr();
		int port = cf.getVCNPort();
		String user = cf.getVCNUser();
		String pass = cf.getVCNPass();
		sID = VCNAccess.logInVCN(ip, port, user, pass);
		
		return sID;
	}
	 
	 
	/**
	 * 初始化VCM
	 * 1.创建表存储空间
	 * 
	 * @param cookie
	 * @param cf
	 */
	@SuppressWarnings("unused")
	private void initVCM(String cookie, Config cf) {
		
		
		// 创建 table
		VCMAccess.vcmMNTCreateStorageSpace(cookie, VCMAccess.vehicleInfoStorageSpace, cf.getStorageName(),
				cf.getTableQuotaVehInfo(), cf.getTableNameVehInfo());

		// 创建 Pic table
		VCMAccess.vcmMNTCreateStorageSpace(cookie, VCMAccess.motorPicStorageSpace, cf.getStorageName(),
				cf.getTableQuotaVPicInfo(), cf.getTableNameVPicInfo());
	
		if((VersionControl.VERSION_RELEASE & VersionControl.KAKOU_LIST) != 0) {
			// 创建卡口LIST table
			VCMAccess.vcmMNTCreateStorageSpace(cookie, VCMAccess.chPListStorageSpace, cf.getStorageName(),
									cf.getTableQuotaChplist(), cf.getTableNameChplist());
		}
		
		if((VersionControl.VERSION_RELEASE & VersionControl.YUSHI_CAMERA) != 0) {
			// 车辆备用信息   table
			VCMAccess.vcmMNTCreateStorageSpace(cookie, VCMAccess.vInfoExtStorageSpace, cf.getStorageName(),
									cf.getTableQuotaVInfoExt(), cf.getTableNameVInfoExt());
		}
		if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0 
				&&(VersionControl.VERSION_RELEASE & VersionControl.HENAN_SHENG) != 0)  {
			// 订阅信息表   table
			VCMAccess.vcmMNTCreateStorageSpace(cookie, VCMAccess.subscribeInfoStorageSpace, cf.getStorageName(),
											cf.getTableQuotaSubInfo(), cf.getTableNameSubInfo());
		}
	}
 
	/**
	 * 初始化VCN
	 * 1.启动过车图片上传
	 * 
	 * @param sessionID
	 * @return
	 */
//	private int initVCN(int sessionID) {
//	
//	int result = -1;
//	
//	Config cf = Config.getIns();
//	cf.getProperties();
//	String pNVRCode = cf.getNVRCode();
//	String  pDomCode = cf.getDomCode();
//	String pClusterCode = cf.getClusterCode();
//	int iMode = cf.getMode();
//	handle = new PointerByReference(Pointer.NULL);
//	result = VCNAccess.startUploadVehicleImg(sessionID, handle,pNVRCode, pDomCode,pClusterCode,iMode);
//	return result;
//}

	private SignalHandler oldHandler;

	/**
	 * @重写信号处理函数
	 * @说明：当接收信号时调用调用信号处理函数
	 * @Date : 2017-06-04
	 * @param: 注册信号名称
	 */
	public void handle(Signal signal) {
		log.debug("Signal handler called for signal " + signal);
		try {
			signalAction(signal);
			// Chain back to previous handler, if one exists
//			if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
//				oldHandler.handle(signal);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void signalAction(Signal signal) {
		log.debug("Handling " + signal.getName());
		
		istermflag = true;
		waitTerm();
	}

	/**
	 * 注册KILL信号 注册CTRL+C信号
	 * 
	 * @param sm
	 * @param signalName:注册KILL信号-"TERM",注册CTRL+C信号
	 *            -"INT";
	 * @return
	 */
	public SignalHandler install(SystemManager sm, String signalName) {
		Signal diagSignal = new Signal(signalName);
		sm.oldHandler = Signal.handle(diagSignal, sm);
		return sm;
	}
	
	@SuppressWarnings("unused")
	public void waitTerm() {
		while(istermflag != true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//stop filemonitor
		if(lyMonitorPathFlag){

			if(listMonitor != null && listMonitor.size() >0){
				FileMonitor fileMonitor = null;
				for(Map map : listMonitor){
					fileMonitor = (FileMonitor)map.get("fileMonitor");
					fileMonitor.stop_notify();
				}
			}
		}
		if(ysMonitorPathFlag){

			if(ysListMonitor != null && ysListMonitor.size() >0){
				FileMonitor fileMonitor = null;
				for(Map map : ysListMonitor){
					fileMonitor = (FileMonitor)map.get("fileMonitor");
					fileMonitor.stop_notify();
				}
			}
		}
		
		//shutdown threadpool
		ThreadPool.endThreadPool();
		
		if( sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_AG)) {
			
			//stop server(if sg)
			if(lyMonitorPathFlag){
				SendClient.stopSendClient();
			} 
			if(ysMonitorPathFlag){
				ysProxyServer.closeProxyServer();
			}
		}else if( sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_SG)) {
			//stop server(if sg) 
			proxyServer.closeProxyServer();
			
			if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0){//版本控制  1 - 启动
				
				FTPClientWraper.shutdown();
				//stop FtpKeepAliveThread
				ftpkt.stopFtpKeepAliveThread();

				Config cf = Config.getIns();
				cf.getProperties();
				int isSend = cf.getIsSendSg();
				if(isSend == 1 && cf.getUploadFtpType() == CheckPointStaticUtils.DIRECT_UPLOAD){

					//destroy FTPClient
					FTPConfig ftpCf = FTPConfig.getIns();
					JSONArray json = JSONArray.fromObject(ftpCf.getFTPList());
					int jNum = json.size();
					if (jNum > 0) { 
						for (int i = 0; i < jNum; i++) {
							JSONObject jsn = json.getJSONObject(i); 
							FTPClientPool ftpClientPool = ftpHT.get(String.valueOf(jsn.get("ftp-path")));
							try {
								ftpClientPool.close();
							} catch (Exception e) {
								e.printStackTrace();
							}  
						}

					} 
				}
			}
			
		}
		
		//stop keepalive
		if(ysMonitorPathFlag || lyMonitorPathFlag){//step1、step2 需要VCM。step3 不需要VCM
			kt.stopKeepAliveThread();
		}
		//stop system thread
		st.stopSystemThread();
		

		if((VersionControl.VERSION_RELEASE & VersionControl.LUOYANG_KAKOU) != 0 &&
				(VersionControl.VERSION_RELEASE & VersionControl.HENAN_SHENG) != 0){//版本控制  1 - 启动
			if(sysType != null && sysType.equals(CheckPointStaticUtils.GATEWAY_SG)){//sg-共享网关
				//stop Subscribe Notifications
				ssNotiCtrl.stopSubscribeNotifications();
				ssPCT.stopSubscribePeriodicCheck();
			}
		}
		
		//logout vcn/vcm
		//登出VCM
//		if(ysMonitorPathFlag || lyMonitorPathFlag){//step1、step2 需要VCM。step3 不需要VCM
			VCMAccess.mntLogout(cookie);
			log.info("VCM Logout success!");
//		}
		//登出VCN
		int vcncon = VCNAccess.logOutVCN(sessionID);
		if(vcncon == 0){
			log.info("VCN Logout success!");
			log.debug("VCN Logout success!" + vcncon);
		}else{
			log.warn("VCN Logout fail!" + vcncon);
		}
		CLibrary.INSTANCE.IVS_SDK_Cleanup();
		//Log exit system 
		log.info("Exit System Success!");
		System.exit(0);
	}
}
