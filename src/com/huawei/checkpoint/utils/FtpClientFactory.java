package com.huawei.checkpoint.utils;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;

import com.huawei.checkpoint.data.FTPClientConfigure;

/**
 * FTPClient工厂类，通过FTPClient工厂提供FTPClient实例的创建和销毁
 * 
 * @author xialf
 */
public class FtpClientFactory implements PoolableObjectFactory<FTPClient> {
	private static Logger logger = Logger.getLogger(FtpClientFactory.class);
	private FTPClientConfigure config;

	// 给工厂传入一个参数对象，方便配置FTPClient的相关参数
	public FtpClientFactory(FTPClientConfigure config) {
		this.config = config;
	}

	public String getDestHost(){
		if(config != null) {
			return config.getHost();
		}
		else
			return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	public FTPClient makeObject() throws Exception {
		FTPClient ftpClient = new FTPClient();
		
		try {
			FTPClientWraper.connect(ftpClient ,config.getHost(), config.getPort(),config.getConnectTimeout());
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				logger.warn("FTPServer refused connection IP:"+config.getHost()+";userName:" + config.getUsername() + " ; password:" + config.getPassword());
				return null;
			}
			boolean result = FTPClientWraper.login(ftpClient,config.getUsername(), config.getPassword(),config.getDefaultTimeout());
//			boolean result = ftpClient.login(config.getUsername(), config.getPassword());
			if (!result) {
				logger.info("FTPServer connection false");
				ftpClient.disconnect();
				ftpClient = null;
				logger.warn("ftpClient登陆失败! IP:"+config.getHost()+";userName:" + config.getUsername() + " ; password:" + config.getPassword());
			}else{
				ftpClient.setFileType(config.getTransferFileType());
				//ftpClient.setBufferSize(1024);
				ftpClient.setBufferSize(config.getBufferSize());
				
				ftpClient.setDefaultTimeout(config.getDefaultTimeout());
				ftpClient.setConnectTimeout(config.getConnectTimeout());
				ftpClient.setDataTimeout(config.getDataTimeout());
				 ftpClient.setControlEncoding(config.getEncoding());
				if (config.getPassiveMode().equals("true")) {
					ftpClient.enterLocalPassiveMode();
				}
				logger.debug("ftpClient登陆成功! IP:"+config.getHost()+";userName:" + config.getUsername() + " ; password:" + config.getPassword());
				logger.info("FTPServer connection success");
			}
			} catch (SocketException e) {
			//e.printStackTrace();
			logger.warn("FTP的IP地址可能错误1，请正确配置。IP:"+config.getHost()+";userName:" + config.getUsername() + " ; password:" + config.getPassword(),e);
		} catch (IOException e) {
			logger.warn("FTP的IP地址可能错误2，请正确配置。IP:"+config.getHost()+";userName:" + config.getUsername() + " ; password:" + config.getPassword(),e);
			ftpClient = null;
			//e.printStackTrace();
		}catch (Exception e) {
			logger.warn("FTP的IP地址可能错误3，请正确配置。IP:"+config.getHost()+";userName:" + config.getUsername() + " ; password:" + config.getPassword(),e);
			ftpClient = null;
			//e.printStackTrace();
		}
		return ftpClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.
	 * Object)
	 */
	public void destroyObject(FTPClient ftpClient) throws Exception {
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				FTPClientWraper.logout(ftpClient,config.getDefaultTimeout());
			}
		} catch (Exception io) {
			logger.warn("logout exception IP:"+config.getHost(),io);
			//io.printStackTrace();
		} finally {
			// 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
			try {
				ftpClient.disconnect();
				logger.info("FTPServer ftpClient.disconnect Success");
			} catch (IOException io) {
				//io.printStackTrace();
				logger.warn("disconnect exception IP:"+config.getHost(),io);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.
	 * Object)
	 */
	public boolean validateObject(FTPClient ftpClient) {
		return FTPClientWraper.sendNoop(ftpClient,config.getDefaultTimeout());
	}

	public void activateObject(FTPClient ftpClient) throws Exception {
	}

	public void passivateObject(FTPClient ftpClient) throws Exception {

	}
}
