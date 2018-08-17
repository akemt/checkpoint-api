package com.huawei.checkpoint.data;

/**
 * FTPClient配置类，封装了FTPClient的相关配置
 *
 * @author heaven
 */
public class FTPClientConfigure {
	private String host;
	private int port;
	private String username;
	private String password;
	private String passiveMode;
	private String encoding; 
	private int threadNum;
	private int transferFileType;
	private boolean renameUploaded;
	private int retryTimes;
	private int connectTimeout;
	private int dataTimeout;
	private int bufferSize;
	private int defaultTimeout;

	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public int getBufferSize() {
		return bufferSize;
	}
	
	public void  setBufferSize(int bufferSize) {
		 this.bufferSize = bufferSize;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getDataTimeout() {
		return dataTimeout;
	}

	public void setDataTimeout(int dataTimeout) {
		this.dataTimeout = dataTimeout;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassiveMode() {
		return passiveMode;
	}

	public void setPassiveMode(String passiveMode) {
		this.passiveMode = passiveMode;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
 

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getTransferFileType() {
		return transferFileType;
	}

	public void setTransferFileType(int transferFileType) {
		this.transferFileType = transferFileType;
	}

	public boolean isRenameUploaded() {
		return renameUploaded;
	}

	public void setRenameUploaded(boolean renameUploaded) {
		this.renameUploaded = renameUploaded;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	@Override
	public String toString() {
		return "FTPClientConfig [host=" + host + "\n port=" + port + "\n username=" + username + "\n password="
				+ password + "\n passiveMode=" + passiveMode + "\n encoding=" + encoding + "\n threadNum=" + threadNum + "\n transferFileType=" + transferFileType
				+ "\n renameUploaded=" + renameUploaded + "\n retryTimes=" + retryTimes + "]";
	}

}