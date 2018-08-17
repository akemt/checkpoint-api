package com.huawei.checkpoint.utils;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;

/**
 * 实现了一个FTPClient连接池
 * 
 * @author xialf
 */
public class FTPClientPool implements ObjectPool<FTPClient> {
	private static Logger logger = Logger.getLogger(FTPClientPool.class);
	private static final int DEFAULT_POOL_SIZE = 10;
	private final BlockingQueue<FTPClient> pool;
	private final FtpClientFactory factory;

	private final int queueTimeOut;
	
	private final int pool_size;
	
	private static final int DEFAULT_QueueTimeOut = 1;

	private volatile boolean status = false;
	
	public boolean isStatus() {
		return status;
	}

	public synchronized void setStatus(boolean status) {
		this.status = status;
	}

	public String getDestHost() {
		return factory.getDestHost();
	}
	/**
	 * 初始化连接池，需要注入一个工厂来提供FTPClient实例
	 * 
	 * @param factory
	 * @throws Exception
	 */
	public FTPClientPool(FtpClientFactory factory) throws Exception {
		this(DEFAULT_POOL_SIZE, factory,DEFAULT_QueueTimeOut);
	}

	/**
	 *
	 * @param maxPoolSize
	 * @param factory
	 * @throws Exception
	 */
	public FTPClientPool(int poolSize, FtpClientFactory factory,int iQueueTimeOut) {
		this.factory = factory;
		this.queueTimeOut = iQueueTimeOut;
		this.pool_size = poolSize;
		pool = new ArrayBlockingQueue<FTPClient>(poolSize);
		try {
			initPool(poolSize);
			setStatus(true);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.warn("FTPClientPool connection exception:" + factory.getDestHost(),e);
			setStatus(false);
		}
	}

	/**
	 * 初始化连接池，需要注入一个工厂来提供FTPClient实例
	 * 
	 * @param maxPoolSize
	 * @throws Exception
	 */
	private void initPool(int maxPoolSize) throws Exception {
		for (int i = 0; i < maxPoolSize; i++) {
			// 往池中添加对象
			addObject();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.ObjectPool#borrowObject()
	 */
	public FTPClient borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
		logger.info("FTPClient start pool.size():"+pool.size());
		//FTPClient client = pool.take();
		FTPClient client = pool.poll(queueTimeOut, TimeUnit.SECONDS);
		logger.debug("FTPClient :"+client + ",IP :" + this.getDestHost());
		if (client == null) {
			logger.warn("FTPClient is NULL !");
			client = factory.makeObject();
			if(client !=null){
				addObject();
			}
		} 
		else if (!factory.validateObject(client)) {// 验证不通过
			// 使对象在池中失效
			factory.destroyObject(client);
			// 制造并添加新对象到池中
			client = factory.makeObject();
			if(client !=null){
				addObject();
			}
			logger.warn("FTPClient Check False !");
		}
//		else{
//			pool.offer(client, 3, TimeUnit.SECONDS);
//		}
		logger.info("FTPClient end pool.size():"+pool.size());
		return client;

	}
	
//	public FTPClient borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
//		logger.info("FTPClient start pool.size():"+pool.size());
//		//FTPClient client = pool.take();
//		FTPClient client = pool.poll(queueTimeOut, TimeUnit.SECONDS);
//		logger.debug("FTPClient :"+client + ",IP :" + this.getDestHost());
//		if (client != null) {
//			if (!factory.validateObject(client)) {// 验证不通过
//				// 使对象在池中失效
//				invalidateObject(client);
//				// 制造并添加新对象到池中
//				client = null;
//				logger.warn("FTPClient Check False !");
//			}
//		}else{
//			logger.warn("FTPClient is NULL !");
//		}
//		logger.info("FTPClient end pool.size():"+pool.size());
//		return client;
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.ObjectPool#returnObject(java.lang.Object)
	 */
	public void returnObject(FTPClient client) throws Exception {
		if ((client != null) && !pool.offer(client, queueTimeOut, TimeUnit.SECONDS)) {
			try {
				factory.destroyObject(client);
			} catch (IOException e) {
				//e.printStackTrace();
				logger.warn("FTPClientPool returnObject exception:" + factory.getDestHost(),e);
			}
		}
	}

	public void invalidateObject(FTPClient client) throws Exception {
		// 移除无效的客户端
		pool.remove(client);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.ObjectPool#addObject()
	 */
	public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
		FTPClient client  = factory.makeObject();
		if(client != null) {
			// 插入对象到队列
			pool.offer(client, queueTimeOut, TimeUnit.SECONDS);
		}else{
			throw new Exception("FTPServer connection false!");
		}
		
	}

	public int getNumIdle() throws UnsupportedOperationException {
		return 0;
	}

	public int getNumActive() throws UnsupportedOperationException {
		return 0;
	}

	public void clear() throws Exception, UnsupportedOperationException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.ObjectPool#close()
	 */
	public void close() throws Exception {
		while (pool.iterator().hasNext()) {
			FTPClient client = pool.take();
			factory.destroyObject(client);
		}
	}

	public void setFactory(PoolableObjectFactory<FTPClient> factory)
			throws IllegalStateException, UnsupportedOperationException {

	}
	
	
	public boolean validateFTPClient() throws Exception, NoSuchElementException, IllegalStateException {
		
		boolean flag = false;
		FTPClient client = factory.makeObject();
		if(client != null){

			flag = factory.validateObject(client);
			factory.destroyObject(client);
		}
			
		if(flag){
			if(flag != status){  
					setStatus(true);
			}
		}else{
			if(flag != status){ 
				setStatus(false);
			}
		} 
		
		return flag;
	}
}
