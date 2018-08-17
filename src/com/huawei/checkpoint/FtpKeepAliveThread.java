package com.huawei.checkpoint;

import java.util.Hashtable;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.utils.FTPClientPool;
import com.huawei.checkpoint.utils.FTPConfig; 

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FtpKeepAliveThread implements Runnable {
	
	private Logger log = Logger.getLogger(FtpKeepAliveThread.class);
	
	// 标记线程是否需要运行
	private volatile boolean running = true;  

	public void run() {
		
		FTPConfig ftpCf = FTPConfig.getIns();
		
		JSONArray json = JSONArray.fromObject(ftpCf.getFTPList());
		int jNum = json.size();
		Hashtable<String, FTPClientPool> ftpHT = SystemManager.getIns().getFtpHT();
		
		while (running) {
			log.info("start FtpKeepAliveThread");
			try {
				Thread.sleep(ftpCf.getFtpKeepAliveTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
				
				if (jNum > 0) { 
					FTPClientPool ftpClientPool = null;
					for (int i = 0; i < jNum; i++) {
						JSONObject jsn = json.getJSONObject(i);

						if (ftpHT.containsKey(String.valueOf(jsn.get("ftp-path")))) {
							ftpClientPool = ftpHT.get(String.valueOf(jsn.get("ftp-path")));
								try {
									boolean flag = ftpClientPool.validateFTPClient();
									log.debug("FtpKeepAliveThread-ftp-path:"+jsn.get("ftp-path")+",status:"+flag);
								} catch (NoSuchElementException e) {
									//e.printStackTrace();
									log.warn("FtpKeepAliveThread-ftp-path exception:"+jsn.get("ftp-path"),e);
									
								} catch (IllegalStateException e) {
									//e.printStackTrace();
									log.warn("FtpKeepAliveThread-ftp-path exception:"+jsn.get("ftp-path"),e);
								} catch (Exception e) {
									//e.printStackTrace();
									log.warn("FtpKeepAliveThread-ftp-path exception:"+jsn.get("ftp-path"),e);
								}
						}
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
	 * 停止线程KeepAliveThread
	 */
	public void stopFtpKeepAliveThread() {
		log.info("stop FtpKeepAliveThread : false");
        this.setRunning(false); 
	}

}
