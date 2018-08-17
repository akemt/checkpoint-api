package com.huawei.checkpoint.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	private static ExecutorService workerMT = null;
	private static ExecutorService workerCtrl = null;
	private static ExecutorService workerUpload = null;
	//创建线程池
	public static void init(int numMT, int numCtrl, int upload) {
		workerMT = Executors.newFixedThreadPool(numMT);  
		workerCtrl = Executors.newFixedThreadPool(numCtrl);
		workerUpload = Executors.newFixedThreadPool(upload);
	}

	public static void executeMT(Runnable thd) {
		workerMT.execute(thd);
	}

	public static void executeCtrl(Runnable thd) {
		workerCtrl.execute(thd);
	}

	public static void executeUpload(Runnable thd) {
		workerUpload.execute(thd);
	}


	public static void endThreadPool() {
		workerMT.shutdown(); // This will make the executor accept no new threads and finish all existing threads in the queue
		while (!workerMT.isTerminated()) {
		}

		workerCtrl.shutdown(); // This will make the executor accept no new threads and finish all existing threads in the queue
		while (!workerCtrl.isTerminated()) {
		}

		workerUpload.shutdown(); // This will make the executor accept no new threads and finish all existing threads in the queue
		while (!workerUpload.isTerminated()) {
		}
	}
}
