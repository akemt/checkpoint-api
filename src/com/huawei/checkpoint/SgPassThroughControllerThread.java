package com.huawei.checkpoint;
import com.huawei.checkpoint.function.SgPassThroughController;

public class SgPassThroughControllerThread implements Runnable {
	
	private SgPassThroughController imp;
	
	public SgPassThroughControllerThread(SgPassThroughController ctrl) {
		imp = ctrl;
	}
	
	public void run() {
		imp.doWork();
		
	}

}
