package com.huawei.checkpoint.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

public class FileListener extends FileAlterationListenerAdaptor {
	private List<String> currentFile;
	private Logger log = Logger.getLogger(FileListener.class);
	private int platFormType;

	public FileListener(int type) {
		super();
		platFormType = type;
		currentFile = new ArrayList<String>();
	}

	/**
	 * 文件创建执行
	 */
	@Override
	public void onFileCreate(File file) {
		log.info("[新建]:" + file.getAbsolutePath());
		currentFile.add(file.getName());
	}

	/**
	 * 文件创建修改
	 */
	@Override
	public void onFileChange(File file) {
		log.info("[修改]:" + file.getAbsolutePath());
		RandomAccessFile randomFile = null;
		String name = file.getName();
		if (!currentFile.contains(name)) {
			log.warn("[invalid modify]:" + file.getAbsolutePath());
			return;
		}
		try {

			randomFile = new RandomAccessFile(name, "rw");

			if (randomFile != null) {
				try {
					randomFile.close();
					randomFile = null;
				} catch (IOException e1) {
				}

				log.info("[文件传输完了]:" + file.getAbsolutePath());
				currentFile.remove(name);
				DataManager.getIns().setNewData(name, platFormType);
			}  

		} catch (IOException e) {
			//e.printStackTrace();
			log.warn("[文件正在传输]:" + file.getAbsolutePath(),e);
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
					randomFile = null;
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * 文件删除
	 */
	@Override
	public void onFileDelete(File file) {
		log.info("[删除]:" + file.getAbsolutePath());
	}

	/**
	 * 目录创建
	 */
	@Override
	public void onDirectoryCreate(File directory) {
		log.info("[新建]:" + directory.getAbsolutePath());
	}

	/**
	 * 目录修改
	 */
	@Override
	public void onDirectoryChange(File directory) {
		log.info("[修改]:" + directory.getAbsolutePath());
	}

	/**
	 * 目录删除
	 */
	@Override
	public void onDirectoryDelete(File directory) {
		log.info("[删除]:" + directory.getAbsolutePath());
	}

	@Override
	public void onStart(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		super.onStart(observer);
	}

	@Override
	public void onStop(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		super.onStop(observer);
	}

}
