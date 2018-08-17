package com.huawei.checkpoint.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;

/**
 * 既能设置buffer大小，也能定时刷新(无论是否达到设定的buffer大小)的appender;
 * 适用于既想使用buffer的IO提高性能，又想定时强制输出以不影响某些依赖日志输出的后续流程的场景
 * 
 */
public class TimedBufferedDailyRollingFileAppender extends DailyRollingFileAppender {
	
	private static Logger log = Logger.getLogger(TimedBufferedDailyRollingFileAppender.class);

	private static final int CHECK_INTERVAL = 5;
	private static final Object appendersLock = new Object();
	private static final List<TimedBufferedDailyRollingFileAppender> appenders = new ArrayList<TimedBufferedDailyRollingFileAppender>();
	static {
		
		Config cf = Config.getIns();
		cf.getProperties(); 
		if(cf.getLog4jBufferedIO()){
			new Thread(new Runnable() {

				public void run() {
					while (true) {
						try {
							synchronized (appendersLock) {
								for (TimedBufferedDailyRollingFileAppender appender : appenders)
									appender.flush();
							}
							Thread.sleep(CHECK_INTERVAL * 1000);
						} catch (Throwable t) {
							// ignore...
						}
					}
				}
			}, "TimedBufferedDailyRollingFileAppender-timed-flush").start();
		}
	}
	
	/** 默认1MB的buffer*/
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024; 

	/** // 默认的定时刷新间隔(秒)*/
	protected int flushInterval = 60; 

	private Date flushTime = new Date(); // 下一次刷新的时间点

	public TimedBufferedDailyRollingFileAppender() {
		super();
		Config cf = Config.getIns();
		cf.getProperties();
		if(cf.getLog4jBufferedIO()){
			setFlushInterval(cf.getLog4jFlushInterval());
			this.setBufferedIO(true); 
			this.setBufferSize(cf.getLog4jBufferSize());
			this.setImmediateFlush(false);
		}else{
			this.setBufferedIO(false);
			this.setImmediateFlush(true);
		}
		synchronized (appendersLock) {
			appenders.add(this);
		}
	}

	public TimedBufferedDailyRollingFileAppender(Layout layout, String filename, String datePattern)
			throws IOException {
		super(layout, filename, datePattern);
		this.setBufferedIO(true);
		this.setBufferSize(DEFAULT_BUFFER_SIZE);
		this.setImmediateFlush(false);
		synchronized (appendersLock) {
			appenders.add(this);
		}
	}

	private void flush() {
		if (!(new Date()).after(flushTime))
			return;
		if (!checkEntryConditions())
			return;
		qw.flush();
		this.flushTime = new Date(System.currentTimeMillis() + this.flushInterval * 1000);
	}

	public void setFlushInterval(int flushInterval) {
		if (flushInterval < CHECK_INTERVAL)
			flushInterval = CHECK_INTERVAL;// 至少CHECK_INTERVAL秒�
		this.flushInterval = flushInterval;
	}

	/**
	 * 本appender必须是bufferedIO, 否则没意义
	 */
	@Override
	public boolean getBufferedIO() {
		return true;
	}

	@Override
	public void setBufferedIO(boolean bufferedIO) {
		super.setBufferedIO(bufferedIO);
	}

	@Override
	public void setImmediateFlush(boolean value) {
		super.setImmediateFlush(value);
	}

	@Override
	public boolean getImmediateFlush() {
		return false;
	}
}