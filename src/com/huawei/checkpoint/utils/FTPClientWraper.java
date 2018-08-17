package com.huawei.checkpoint.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

/**
 * FTPClient 封装类 Future
 * 
 * @author Xialf
 *
 */
public class FTPClientWraper {

	private static Logger logger = Logger.getLogger(FTPClientWraper.class);

	private static ExecutorService exec = null;
	
	private volatile static int iDataTimeouts = 1;
	
	private volatile static boolean flag = false;

	private volatile static boolean flaglogout = false;

	private volatile static boolean flagNoop = false;
	
	private volatile static boolean flagStoreFile = false;
	
	public static void init(int iFixedThreadPoolNum,int dataTimeouts) {
		iDataTimeouts = dataTimeouts;
		exec = Executors.newFixedThreadPool(iFixedThreadPoolNum);
		
	}

	/**
	 * 连接--FTPClient
	 * 
	 * @param ftpClient
	 * @param strHost
	 * @param iPort
	 * @return
	 */
	public static FTPClient connect(FTPClient ftpClient, String strHost, int iPort,int iFutureTimeout) {
//		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				// Thread.sleep(1000 * 5);
				ftpClient.connect(strHost, iPort);
				return "success!";
			}
		};

		try {
			Future<String> future = exec.submit(call);
			String obj = future.get(iFutureTimeout, TimeUnit.MILLISECONDS); // 任务处理超时时间设置
			logger.info("FTPServer call connection " + obj);
		} catch (TimeoutException ex) {
			logger.warn("FTPServer call connection Timeout...."+ex);
		} catch (Exception e) {
			logger.warn("FTPServer call connection false!"+e);
		}

		logger.info("FTPServer connect Finshed!");

		return ftpClient;
	}

	/**
	 * 登录 ftpClient
	 * 
	 * @param ftpClient
	 * @param strUsername
	 * @param strPassword
	 * @return
	 */
	public static boolean login(FTPClient ftpClient, String strUsername, String strPassword,int iFutureTimeout) {
//		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				// Thread.sleep(1000 * 5);
				flag = ftpClient.login(strUsername, strPassword);
				logger.info("FTPServer call login" + flag);
				return "success!";
			}
		};

		try {
			Future<String> future = exec.submit(call);
			String obj = future.get(iFutureTimeout, TimeUnit.MILLISECONDS); // 任务处理超时时间设置
			logger.info("FTPServer call login " + obj);
		} catch (TimeoutException ex) {
			logger.warn("FTPServer call login Timeout...."+ex);
		} catch (Exception e) {
			logger.warn("FTPServer call login false!"+e);
		}

		logger.info("FTPServer login Finshed!");

		return flag;
	}

	/**
	 * 登出 logout
	 * 
	 * @param ftpClient
	 * @return
	 */
	public static boolean logout(FTPClient ftpClient,int iFutureTimeout) {
//		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				// Thread.sleep(1000 * 5);
				flaglogout = ftpClient.logout();
				logger.info("FTPServer call logout" + flaglogout);
				return "success!";
			}
		};

		try {
			Future<String> future = exec.submit(call);
			String obj = future.get(iFutureTimeout, TimeUnit.MILLISECONDS); // 任务处理超时时间设置
			logger.info("FTPServer call logout " + obj);
		} catch (TimeoutException ex) {
			logger.warn("FTPServer call logout Timeout...."+ex);
		} catch (Exception e) {
			logger.warn("FTPServer call logout false!"+e);
		}

		logger.info("FTPServer call logout Finshed!");

		return flaglogout;

	}

	/**
	 * 验证 sendNoOp
	 * 
	 * @param ftpClient
	 * @return
	 */
	public static boolean sendNoop(FTPClient ftpClient,int iFutureTimeout) {
//		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				// Thread.sleep(1000 * 5);
				try {
					flagNoop = ftpClient.sendNoOp();
				} catch (IOException e) {
					logger.warn("client to sendNoOp false!");
					flagNoop = false;
				}
				logger.info("FTPServer call sendNoop" + flagNoop);
				return "success!";
			}
		};

		try {
			Future<String> future = exec.submit(call);
			String obj = future.get(iFutureTimeout, TimeUnit.MILLISECONDS); // 任务处理超时时间设置
			logger.info("FTPServer call sendNoop " + obj);
		} catch (TimeoutException ex) {
			logger.warn("FTPServer call sendNoop Timeout...."+ex);
		} catch (Exception e) {
			logger.warn("FTPServer call sendNoop false!"+e);
		}

		logger.info("FTPServer sendNoop Finshed!");

		return flagNoop;
	}
	
	/**
	 * 上传 storeFile
	 * 
	 * @param ftpClient
	 * @return
	 */
	public static boolean storeFile(FTPClient ftpClient,String remoteFileName,InputStream in) {
//		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				// Thread.sleep(1000 * 5);
				try {
					flagStoreFile = ftpClient.storeFile(remoteFileName, in);
				} catch (IOException e) {
					logger.warn("client to storeFile false!");
					flagStoreFile = false;
				}
				logger.info("FTPServer call storeFile" + flagStoreFile);
				return "success!";
			}
		};

		try {
			Future<String> future = exec.submit(call);
			String obj = future.get(iDataTimeouts, TimeUnit.MILLISECONDS); // 任务处理超时时间设置
			logger.info("FTPServer call storeFile " + obj);
		} catch (TimeoutException ex) {
			logger.warn("FTPServer call storeFile Timeout...."+ex);
		} catch (Exception e) {
			logger.warn("FTPServer call storeFile false!"+e);
		}

		logger.info("FTPServer storeFile Finshed!");

		return flagStoreFile;
	}
	
	public static void shutdown(){
		// 关闭线程池
		exec.shutdown();
	}

}
