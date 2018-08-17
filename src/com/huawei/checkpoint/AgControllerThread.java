package com.huawei.checkpoint;
import java.util.ArrayList;

import com.huawei.checkpoint.function.AgController;

public class AgControllerThread implements Runnable {
	
	private AgController imp;
	private ArrayList<String> path;
	private int type;
	
	public AgControllerThread(ArrayList<String> path, int type) {
		this.path = path;
		this.type = type;
	}
	
	public void run() {
		imp = new AgController(path,type);
		imp.doWork();
		
	}

}
