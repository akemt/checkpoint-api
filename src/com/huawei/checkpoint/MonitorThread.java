package com.huawei.checkpoint;

import com.huawei.checkpoint.data.FileMonitor;

public class MonitorThread implements Runnable{
	private FileMonitor imp;
	
	public MonitorThread(FileMonitor fm) {
		imp = fm;
	}
	
	public void run() {
		//imp.startWatchService();
		//imp.startCommonIO();
		imp.startInotify();
	}

}
