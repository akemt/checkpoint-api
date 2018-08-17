package com.huawei.checkpoint;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.VCMAccess;


public class KeepAliveThread implements Runnable{
	
	private Logger log = Logger.getLogger(KeepAliveThread.class);
	// 标记线程是否需要运行
	private volatile boolean running = true;  
	
	public void run() {
		log.info("start KeepAliveThread");
		Config cf = Config.getIns();
		int iKeepAlive = cf.getVCMKeepAlive();
		//--登录VCM成功后的Cookie
		String strCookie = SystemManager.getIns().getCookie();
		
		while (running) {
			
			try {
				Thread.sleep(iKeepAlive);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				//每间隔xxxx毫秒，运行一次-保活信息。
				String strNewCookie = VCMAccess.mntVAGetOPtions(strCookie);
				if(strNewCookie != null){
					SystemManager.getIns().setCookie(strNewCookie);
				}
			}catch (Exception e) {
				log.warn("keepalive Exception " , e);
				//e.printStackTrace();
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
	public void stopKeepAliveThread() {
		log.info("stop KeepAliveThread : false");
        this.setRunning(false); 
	}

}
