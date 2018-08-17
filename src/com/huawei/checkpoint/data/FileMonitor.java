package com.huawei.checkpoint.data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationMonitor;

import org.apache.log4j.Logger;

import com.den_4.inotify_java.EventQueueFull;
import com.den_4.inotify_java.Inotify;
import com.den_4.inotify_java.InotifyEvent;
import com.den_4.inotify_java.InotifyEventListener;
import com.den_4.inotify_java.exceptions.InotifyException;
import com.den_4.inotify_java.enums.Event;

public class FileMonitor {
	private WatchService watchService;

//	private FileAlterationObserver observer;
	private FileAlterationMonitor monitor;
	private Inotify notify;
	private int fd;
	private InotifyEventListener e;

	private Logger log = Logger.getLogger(FileMonitor.class);
	private boolean notDone = true;
	private int platFormType;

	private List<String> currentFile;
	private String monitorPath;

	public FileMonitor() { 
	}
	
	public FileMonitor(String dirPath, int type) {
		platFormType = type;
		monitorPath = dirPath;
		// init_watchService(dirPath);
		// init_commonIO(dirPath);
		// File fd = new File(dirPath);
		init_notify(dirPath);

	}

	private void init_notify(String dirPath) {
		try {
			notify = new Inotify();
			fd = notify.addWatch(dirPath, Event.Close_Write);
			e = new InotifyEventListener() {

				@Override
				public void filesystemEventOccurred(InotifyEvent arg0) {
					// TODO Auto-generated method stub
					if (arg0.isCloseWrite()) {
						String fullFileName = monitorPath + "/" + arg0.getName();
						log.debug("[FileMonitor][init_notify][文件传输]:" + fullFileName);
						log.info("[FileMonitor][init_notify][文件传输完成!]");
						// currentFile.remove(name);
						DataManager.getIns().setNewData(fullFileName, platFormType);
					}

				}

				@Override
				public void queueFull(EventQueueFull arg0) {
					// TODO Auto-generated method stub

				}
			};

		} catch (InotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 停止监视文件
	 */
	public void stop_notify(){
		notify.removeListener(fd, e);
		log.info("[stop_notify success]");
	}

//	private void init_commonIO(String dirPath) {
//		long interval = TimeUnit.SECONDS.toMillis(5);
//		currentFile = new ArrayList<String>();
//		observer = new FileAlterationObserver(dirPath,
//				FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".xml")), // 过滤文件格式
//				null);
//
//		observer.addListener(new FileListener(platFormType)); // 设置文件变化监听器
//		// 创建文件变化监听器
//		monitor = new FileAlterationMonitor(interval, observer);
//	}
//
//	private void init_watchService(String dirPath) {
//		currentFile = new ArrayList<String>();
//		Path path = Paths.get(dirPath);
//		try {
//			watchService = FileSystems.getDefault().newWatchService(); // 创建watchService
//			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
//					StandardWatchEventKinds.ENTRY_DELETE); // 注册需要监控的事件,ENTRY_CREATE
//															// 文件创建,ENTRY_MODIFY
//															// 文件修改,ENTRY_MODIFY
//															// 文件删除
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public void startInotify() {
		notify.addListener(fd, e);
	}

	public void startCommonIO() {
		if (monitor != null) {
			try {
				monitor.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes") 
	public void startWatchService() {
		log.debug("watch...start");
		while (notDone) {
			try {
				// WatchKey watchKey = watchService.poll(10,
				// TimeUnit.MILLISECONDS);
				// //此处将处于等待状态,等待检测到文件夹下得文件发生改变,返回WatchKey对象
				WatchKey watchKey = watchService.take();
				log.debug("have key");
				if (watchKey != null) {
					List<WatchEvent<?>> events = watchKey.pollEvents(); // 获取所有得事件
					for (WatchEvent event : events) {
						WatchEvent.Kind<?> kind = event.kind();
						if (kind == StandardWatchEventKinds.OVERFLOW) {
							// 当前磁盘不可用
							continue;
						}
						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = event;
						Path path = ev.context();
						String name = path.getFileName().toString();
						if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
							currentFile.add(name);
							log.debug("[create]: " + path.getFileName());
						} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
							if (!currentFile.contains(name)) {
								log.warn("[invalid modify]: " + path.getFileName());
								continue;
							}

							RandomAccessFile randomFile = null;
							try {
								randomFile = new RandomAccessFile(name, "rw");

								if (randomFile != null) {
									try {
										randomFile.close();
										randomFile = null;
									} catch (IOException e1) {
									}

									log.info("[文件传输完了]:" + name);
									currentFile.remove(name);
									DataManager.getIns().setNewData(name, platFormType);
								}  

							} catch (IOException e) {
								e.printStackTrace();
								log.error("[文件正在传输]:" + name);
							} finally {
								if (randomFile != null) {
									try {
										randomFile.close();
										randomFile = null;
									} catch (IOException e1) {
									}
								}
							}
							log.debug("[modify]: " + path.getFileName());
						} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
							log.debug("delete " + path.getFileName());
						}
					}
					if (!watchKey.reset()) {
						// 已经关闭了进程
						log.error("exit watch server");
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
