package com.huawei.checkpoint.function;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.data.FTPClientConfigure;
import com.huawei.checkpoint.data.MessageUtils;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.FTPClientPool;
import com.huawei.checkpoint.utils.FtpClientUtil;

/**
 * 共享网关Controller--SG
 * 
 * @author Xialf
 *
 */
public class SgPassThroughController {

	private static Logger log = Logger.getLogger(SgPassThroughController.class);

	private MessageUtils msgUtil;

	public SgPassThroughController(MessageUtils msg) {
		msgUtil = msg;
	}

	public int doWork() {

		Config cf = Config.getIns();
		log.info("[SgPassThroughController] [doWork] [start]>>>>>>>>>>>>>>>>>>>>");
		log.debug("[SgPassThroughController] [doWork] SgReDataType:"+msgUtil.getSgReDataType());
		if (msgUtil.getSgReDataType() == CheckPointStaticUtils.SG_PASS_THROUGH) {// 从ag
			
			log.debug("[SgPassThroughController] [doWork] UploadFtpType:" + cf.getUploadFtpType());
			// 接入网关来的数据

			if (cf.getUploadFtpType() == CheckPointStaticUtils.DOWNLOAD_UPLOAD) {
				// 1.登录LYFTP
				FTPClient lyftpClient = FtpClientUtil.getFTPClient(cf.getLyFtpAddr(), cf.getLyFtpPass(),
						cf.getLyFtpUsr(), cf.getLyFtpPort());
				// 2.从FtpServer下载数据readConfigFileForFTP
				ArrayList<Map<String, String>> list = FtpClientUtil.readXmlFiles(lyftpClient, msgUtil.getFilePathUrl());
				// 3.登录目标FTP
				FTPClient ftpClient = FtpClientUtil.getTargetFTPClient(cf.getPPAddr(), cf.getPPPass(), cf.getPPUsr(),
						cf.getPPPort());
				// 4.把数据上传到目标FTP文件夹中
				FtpClientUtil.uploadFtpByXmlLists(ftpClient, list);
			} else if (cf.getUploadFtpType() == CheckPointStaticUtils.DIRECT_UPLOAD) {
				
				//一路FTP
//				FTPClient ftpClient = FtpClientUtil.getFTPClient( cf.getPPAddr(), cf.getPPPass(), cf.getPPUsr(), cf.getPPPort());
//				//2.把数据上传到目标FTP文件夹中
//				FtpClientUtil.uploadFiles(ftpClient, msgUtil.getFilePathUrl());
				FTPClient ftpClient=null;
				FTPClientPool ftpClientPool = null;
				//多路FTP
				ArrayList<String> filePathUrl = msgUtil.getFilePathUrl();
				for (int i = 0; i < filePathUrl.size(); i++) {
					String filePath = filePathUrl.get(i);
					String strFtpPath = new File(filePath).getParent();

					Hashtable<String, FTPClientPool> ftpHT = SystemManager.getIns().getFtpHT();
					if (ftpHT.containsKey(strFtpPath)) {
						ftpClientPool = ftpHT.get(strFtpPath);
						File f = new File(filePath);
						// 1.登录目标FTP
//						FTPClient ftpClient = FtpClientUtil.getFTPClient(ftpConfig.getHost(), ftpConfig.getPassword(),
//								ftpConfig.getUsername(), ftpConfig.getPort());
							if(ftpClientPool != null && ftpClientPool.isStatus()){
								try {
									ftpClient = ftpClientPool.borrowObject();
									
									// 2.把数据上传到目标FTP文件夹中
									FtpClientUtil.uploadFiles(ftpClient, filePath); 
									
								} catch (NoSuchElementException e) {
									f.delete();
									log.warn("上传文件:" + filePath +"to:"+ftpClientPool.getDestHost(), e);
									//e.printStackTrace();
								} catch (IllegalStateException e) {
									f.delete();
									log.warn("上传文件:" + filePath +"to:"+ftpClientPool.getDestHost(),e);
									//e.printStackTrace();
								} catch (Exception e) {
									f.delete();
									//e.printStackTrace();
									log.warn("上传文件:" + filePath +"to:"+ftpClientPool.getDestHost(),e);
								}finally {
									if(ftpClient != null){
										try {
											ftpClientPool.returnObject(ftpClient);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											//e.printStackTrace();
											f.delete();
											log.warn("上传文件:" + filePath +"to:"+ftpClientPool.getDestHost()+" return exception",e);
										}
									}
								}
							}else{
								if(ftpClientPool == null){
									log.error("上传文件:" + filePath +" failed for pool  is null!");
								}
								else{
									log.warn("上传文件:" + filePath +"to:"+ftpClientPool.getDestHost() +" failed for pool status is false!");
								}
								f.delete();
							}
						
					}

				}

			}

		} else {// 从VCM 查询出来的数据

		}

		log.info("[SgPassThroughController] [doWork] [end]<<<<<<<<<<<<<<<<<<<<<");
		return 1;
	}

}
