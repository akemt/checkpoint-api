package com.huawei.checkpoint.function;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.VersionControl;
import com.huawei.checkpoint.data.ChPListConstant;
import com.huawei.checkpoint.data.LuoyangMotor;
import com.huawei.checkpoint.data.LuoyangParser;
import com.huawei.checkpoint.data.Parser;
import com.huawei.checkpoint.data.SendClient;
import com.huawei.checkpoint.data.VCMImageMotor;
import com.huawei.checkpoint.data.YuShiImagesMotor;
import com.huawei.checkpoint.data.YuShiMotor;
import com.huawei.checkpoint.data.YuShiVideoMotor;
import com.huawei.checkpoint.utils.Base64Trans;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.VCMAccess;
import com.huawei.checkpoint.utils.VCNAccess;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * ag 接入网关Controller
 * 
 * @author Xialf
 *
 */
public class AgController {

	private Logger log = Logger.getLogger(AgController.class);
	private Parser xmlParser = null;
	private static ThreadLocal<Parser> parser = new ThreadLocal<Parser>() {
		protected Parser initialValue() {
			Parser newParser = new LuoyangParser();
			return newParser;
		};

	};

	// public AgController(Parser parser) {
	// xmlParser = parser;
	// }
	public AgController(ArrayList<String> path, int type) {
		// xmlParser = parser;
		xmlParser = parser.get();
		xmlParser.init(path, type);
	}

	// public AgController(Parser parser) {
	// xmlParser = parser;
	// }

	public int doWork() {
		int ret = 0;
		// Parser xmlParser = parser.get();
		int iType = xmlParser.getType();

		Config cf = Config.getIns();
		log.info("[AgController] [doWork] [start]>>>>>>>>>>>>>>>>>>>>");

		if (iType == CheckPointStaticUtils.LUO_YANG) {// 0: 洛阳
			log.debug("AgController---start----parse-xml:"+xmlParser.getPath());
			log.info("[AgController] [doWork] [Parse] [start]");
			// 1.通过XML转成实体Bean
			List<LuoyangMotor> lyMotorList = xmlParser.parse(LuoyangMotor.class);
			log.info("[AgController] [doWork] [Parse] [end]");
			LuoyangMotor lyMotor = null;
			LuoyangMotor lyMotorTime = null;
			LuoyangMotor lyMotorPic = null;
			// PicList
			ArrayList<byte[]> picsBuffer = new ArrayList<>();

			// List<LuoyangMotor> lyLists = new ArrayList<LuoyangMotor>();

			log.info("[AgController] [doWork] [VCN] [start]");
			if (lyMotorList != null && !lyMotorList.isEmpty()) {
				int cnt = lyMotorList.size();
				// log.warn("xmlParser--cnt--:"+cnt);
				for (int i = 0; i < cnt; i++) {

					lyMotor = lyMotorList.get(i);
					// VCM-----ID 
					String strID = CheckPointStaticUtils.getUUID();
					lyMotor.setID(strID);// 主键ID
					lyMotor.setDomCode(cf.getDomCode());
//					Base64Trans.generateImagebyByte(lyMotor.getPic().get(i));
					// setPic
					int num = Integer.parseInt(lyMotor.getPicNum());
					ArrayList<byte[]> picData = lyMotor.getPic();
					if((num > 0) && (picData != null)){
						picsBuffer.addAll(picData); 
						// lyMotor.setNVRCode(cf.getNVRCode());
						// lyLists.add(lyMotor);
					}
				}

				// 取实体Bean 转换成VCM需要的XML----发送到共享网关sg------
				// String filePathUrl =
				// cf.getFtpPathByMonitorPath(xmlParser.getFilePathUrl());

				int isSend = cf.getIsSendSg();
				log.info("[AgController] [doWork] [Send] [start]");
				if (isSend == 0) {// no send
					log.debug("AgController---start----Send:"+isSend);
					for (String str : xmlParser.getPath()) {
						String strFile = str;
						File f = new File(strFile);
						f.delete();
					}
					log.debug("AgController---end----Send:"+isSend);
				} else if (isSend == 1) {
					log.debug("AgController---start----Send:"+isSend);
					SendClient.send(CheckPointStaticUtils.SG_PASS_THROUGH, xmlParser.getPath());
					log.debug("AgController---end----Send:"+isSend);
				}
				log.info("[AgController] [doWork] [Send] [end]");

				// 2.取实体Bean中图片上传到VCN-----------start------
				// 获取VCN登录成功后的SessionID
				int iSessionID = SystemManager.getIns().getSessionID();
				String pNVRCode = cf.getNVRCode();

				String pDomCode = cf.getDomCode();

				String pClusterCode = cf.getClusterCode();

				long[] picIDs = null;

				List<VCMImageMotor> mPicList = new ArrayList<VCMImageMotor>();

				VCMImageMotor mPic = null;
				// 随机生成序号ID
				// log.warn("vcn----start:"+xmlParser.getPath());
				if (picsBuffer != null && !picsBuffer.isEmpty()) { 

					Date dWatchTime = null;
					long[] snapTime = null;
					int j = 0;
					int lpcon = lyMotorList.size();
					int picb = picsBuffer.size();
					snapTime = new long[picb];
					int cnt2 = 0;
					for (int i = 0; i < lpcon; i++) {

						lyMotorTime = lyMotorList.get(i);
						String strWatcheTime = lyMotorTime.getWatchTime();
						try {
							if ("".equals(strWatcheTime)) {
								dWatchTime = new Date();
							} else {
								SimpleDateFormat oritime = null;
								if(strWatcheTime.length() > 19){
									oritime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								}else{
									oritime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								}
							
								dWatchTime = oritime.parse(strWatcheTime);
							}

						} catch (ParseException e) {
							log.warn("parse excption" + xmlParser.getPath(), e);
							dWatchTime = new Date();
							// e.printStackTrace();
						}

						// 抓拍时间-watchTime-组装抓拍时间

						for (j = 0; j < Integer.valueOf(lyMotorTime.getPicNum()); j++) {
							// 把抓拍时间i的值写入lo中
							snapTime[j + cnt2] = dWatchTime.getTime();
						}
						cnt2 += j;
					}

					// PointerByReference handle =
					// SystemManager.getIns().getHandle();

					// 上传过车图片--单张图片
					// picID = VCNAccess.uploadVehicleImg(iSessionID, pNVRCode,
					// strPic.getBytes(),snapTime);
					// 上传过车图片--多张图片
					int result = -1;
					int mode = cf.getMode();
					PointerByReference handle = new PointerByReference(Pointer.NULL);
					String outNvrCode = null;
					log.info("[AgController] [doWork] [startUploadVehicleImg] [start]");
					if (mode == 0) {
						result = VCNAccess.startUploadVehicleImg(iSessionID, handle, pNVRCode, pDomCode);
						outNvrCode = pNVRCode;
						if (result != 0) {
							log.warn("VCNAccess.startUploadVehicleImg error : " + result);
						}
					} else if (mode == 1) {
						outNvrCode = VCNAccess.startUploadVehicleImgCluster(iSessionID, handle, pDomCode, pClusterCode);
						if (outNvrCode == null) {
							log.warn("VCNAccess.startUploadVehicleImgCluster error outNvrCode == null");
						}
					}
					log.info("[AgController] [doWork] [startUploadVehicleImg] [end]");
					log.info("[AgController] [doWork] [uploadVehicleImgs] [start]");
					if (outNvrCode != null) {
						log.debug("VCNAccess.startUploadVehicleImg return NVRCode : " + outNvrCode + ":len:"
								+ outNvrCode.length());
						for (int i = 0; i < cnt; i++) {

							lyMotor = lyMotorList.get(i);
							lyMotor.setNVRCode(outNvrCode);
							// lyLists.add(lyMotor);
						}
						picIDs = VCNAccess.uploadVehicleImgs(iSessionID, outNvrCode, pDomCode, pClusterCode, picsBuffer,
								snapTime, handle, mode);
					}
					log.debug("parse vehicle imge success-picIDs(long[]) : " + Arrays.asList(picIDs));
					log.info("[AgController] [doWork] [uploadVehicleImgs] [end]");

					int cnt1 = 0;

					// 组装ID，PicID, PicName
					if (picIDs != null && picIDs.length > 0) {
						int pcon = lyMotorList.size();
						for (int i = 0; i < pcon; i++) {

							lyMotorPic = lyMotorList.get(i);
							// setPic
							for (j = 0; j < Integer.valueOf(lyMotorPic.getPicNum()); j++) {
								mPic = new VCMImageMotor();
								// 把lo的值遍历出来
								mPic.setAutoID(lyMotorPic.getID());
								mPic.setPicID(String.valueOf(picIDs[cnt1 + j]));
								mPic.setImageType("1");
								mPic.setWatchTimeMill(VCMAccess.setTimeMill(lyMotorPic.getWatchTime()));
								mPic.setWatchTime(VCMAccess.setTime(lyMotorPic.getWatchTime()));
								mPic.setImageHeight("0");
								mPic.setImageWidth("0");
								mPicList.add(mPic);
							}
							cnt1 += j;
						}
					}
				} else {
					log.warn("parse vehicle imge failed:" + xmlParser.getFileName());
				}
				log.info("[AgController] [doWork] [VCN] [end]");
				// ------------------VCN-----------End------

				// test code
				// 图片base64,暂生成图片存放在本地E盘下
				// if (!"".equals(lyMotor.getPic1()))
				// Base64Trans.generateImage(lyMotor.getPic1());
				// if (!"".equals(lyMotor.getPic2()) &&
				// !"?".equals(lyMotor.getPic2()))
				// Base64Trans.generateImage(lyMotor.getPic2());

				// 3.取实体Bean中的数据上传到VCM-----------start------
				log.info("[AgController] [doWork] [VCM] [start]");
				log.info("[AgController] [doWork] [VCM] [start] [insert data-->VehicleInfo]");
				// ----1.过车数据-根据实体Bean转换成VCM需要的XML格式
				String xmlMetadataIndex = VCMAccess.getVCMBean(cf.getStorageName(), cf.getTableNameVehInfo(),
						lyMotorList, CheckPointStaticUtils.LUO_YANG);

				// ----2.通过HTTP请求上传到VCM
				// --登录VCM成功后的Cookie
				String strCookie = SystemManager.getIns().getCookie();
				// 把转换后的过车数据XML-存储到表中
				VCMAccess.vcmVACreateMetadataIndex(strCookie, xmlMetadataIndex);
				log.info("[AgController] [doWork] [VCM] [end] [insert data-->VehicleInfo]");
				if (mPicList != null && mPicList.size() > 0 && picIDs != null) {
					log.info("[AgController] [doWork] [VCM] [start] [insert data-->VPicInfo]");
					// 把转换后的过车图片数据XML-存储到表中MotorPic
					String xmlMotorPicData = VCMAccess.getVCMBean(cf.getStorageName(), cf.getTableNameVPicInfo(), mPicList,
							CheckPointStaticUtils.OTHER_TYPE);

					VCMAccess.vcmVACreateMetadataIndex(strCookie, xmlMotorPicData);
					log.info("[AgController] [doWork] [VCM] [end] [insert data-->VPicInfo]");
				}
				// 取实体Bean中的数据上传到VCM-----------end------
				// log.warn("AgController----end:"+xmlParser.getPath());
				 if((VersionControl.VERSION_RELEASE & VersionControl.KAKOU_LIST) != 0) {
					 log.info("[AgController] [doWork] [VCM] [start] [insert data-->CheckPointInfo]");
					/** ---------卡口LIST-----*/
					ChPListConstant.saveLYChPList(strCookie, lyMotorList, cf);
					log.info("[AgController] [doWork] [VCM] [end] [insert data-->CheckPointInfo]");
				 }
				 
				 log.info("[AgController] [doWork] [VCM] [end]");
				// 清空List,ArrayList
				lyMotorList.clear();
				lyMotorList = null;

				picsBuffer.clear();
				picsBuffer = null;

				mPicList.clear();
				mPicList = null;

			}
		} else if (iType == CheckPointStaticUtils.YU_SHI) {// 1: 宇视

			log.debug("AgController---start----yuShiParse-xml:"+xmlParser.getPath());
			log.info("[AgController] [doWork] [yuShiParse] [start]");
			List<YuShiMotor> ySMotorList = xmlParser.yuShiParse(YuShiMotor.class);
			log.info("[AgController] [doWork] [yuShiParse] [end]");

			// PicList
			ArrayList<byte[]> picsBuffer = new ArrayList<>();
			//宇视实体
			YuShiMotor ysMotor = null;
			//宇视图片实体
			YuShiMotor ysMotorPic = null;
			//宇视图像实体
			YuShiVideoMotor videoMotor = null;
			//宇视图像实体LIST
			List<YuShiVideoMotor> videoList = new ArrayList<YuShiVideoMotor>();
			
			YuShiMotor ysMotorTime = null;
			

			YuShiImagesMotor mPic = null;


			/** 取图片信息 */
			List<YuShiImagesMotor> mPicList = new ArrayList<YuShiImagesMotor>();
			log.info("[AgController] [doWork] [VCN] [start]");
			int conPicList = 0;
			if (ySMotorList != null && !ySMotorList.isEmpty()) {
				log.debug("宇视 Motor List" + ySMotorList);

				int con = ySMotorList.size();
				for (int i = 0; i < con; i++) {
					ysMotor = ySMotorList.get(i);
					// VCM-----ID
					String strID = CheckPointStaticUtils.getUUID();
					ysMotor.setAutoID(strID);;// 主键ID
					ysMotor.setDomCode(cf.getDomCode());
					
					String strVideoURL = ysMotor.getVideoURL();
					String strVideoURL2 = ysMotor.getVideoURL2();
					if(!strVideoURL.equals("") || !strVideoURL2.equals("")){
						videoMotor = new YuShiVideoMotor();
						videoMotor.setAutoID(strID);
						videoMotor.setVideoURL(strVideoURL);
						videoMotor.setVideoURL2(strVideoURL2);
						videoList.add(videoMotor);
					}
					
					mPicList = ysMotor.getImage();
					conPicList = mPicList.size();
					if (mPicList != null && conPicList > 0) {
						for (int p = 0; p < conPicList; p++) {
							mPic = mPicList.get(p);
							if (mPic.getImageData() != null && !"".equals(mPic.getImageData())) {
								picsBuffer.add(Base64.getDecoder().decode(mPic.getImageData()));
							}
						}
					}
				}

				/** 是否删除接收文件 */
				int isSend = cf.getIsSendSg();
				log.info("[AgController] [doWork] [Send] [start]");
				if (isSend == 0) {// no send
					log.debug("AgController---start----Send:"+isSend);
					for (String str : xmlParser.getPath()) {
						String strFile = str;
						File f = new File(strFile);
						f.delete();
					}
					log.debug("AgController---start----Send:"+isSend);
				}
				log.info("[AgController] [doWork] [Send] [end]");
				// 2.取实体Bean中图片上传到VCN-----------start------
				// 获取VCN登录成功后的SessionID
				int iSessionID = SystemManager.getIns().getSessionID();
				String pNVRCode = cf.getNVRCode();

				String pDomCode = cf.getDomCode();

//				String pClusterCode = cf.getClusterCode();
				String pClusterCode = "";

				long[] picIDs = null;

				// --登录VCM成功后的Cookie
				String strCookie = SystemManager.getIns().getCookie();

				List<VCMImageMotor> vcmPicList = new ArrayList<VCMImageMotor>();
				
				// 随机生成序号ID
				if (picsBuffer != null && !picsBuffer.isEmpty()) {
					SimpleDateFormat format = null;
					
//					SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
					long[] snapTime = null;
					int j = 0;
					try {
						int pcon = ySMotorList.size();
						int picb = picsBuffer.size();
						snapTime = new long[picb];
//						int cnt2 = 0;
						for (int i = 0; i < pcon; i++) {
							// 抓拍时间-watchTime-组装抓拍时间
							ysMotorTime = ySMotorList.get(i);
							for (j = 0; j < Integer.valueOf(ysMotorTime.getPicNumber()); j++) {
								mPic = ysMotorTime.getImage().get(j);
								String passTime = mPic.getPassTime();
								if(passTime.equals("")){
									passTime = "11111111111111";
								}
								if(passTime.length() > 14){
									format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
								}else{
									format = new SimpleDateFormat("yyyyMMddHHmmss");
								}
								// 把抓拍时间i的值写入lo中
								snapTime[j] = format.parse(passTime).getTime();
//								snapTime[j + cnt2] = format.parse(passTime).getTime();
							}
//							cnt2 += j;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// 上传过车图片--单张图片
					// picID = VCNAccess.uploadVehicleImg(iSessionID, pNVRCode,
					// strPic.getBytes(),snapTime);
					// 上传过车图片--多张图片
					int result = -1;
					int mode = cf.getMode();
					PointerByReference handle = new PointerByReference(Pointer.NULL);
					String outNvrCode = null;
					log.info("[AgController] [doWork] [startUploadVehicleImg] [start]");
					if (mode == 0) {
						result = VCNAccess.startUploadVehicleImg(iSessionID, handle, pNVRCode, pDomCode);
						outNvrCode = pNVRCode;
						if (result != 0) {
							log.warn("VCNAccess.startUploadVehicleImg error : " + result);
						}
					} else if (mode == 1) {
						
						String lCamID = ysMotor.getCamID();
						String strClusterCode = VCMAccess.queryChPointListByCamID(strCookie, lCamID, CheckPointStaticUtils.YU_SHI, cf);
						if(strClusterCode.equals("")){
							log.warn("VCNAccess.startUploadVehicleImg error ClusterCode is null; ");
						}else{
							log.debug("VCNAccess.startUploadVehicleImg  ClusterCode: "+strClusterCode);
							outNvrCode = VCNAccess.startUploadVehicleImgCluster(iSessionID, handle, pDomCode, strClusterCode);
							if (outNvrCode == null) {
								log.warn("VCNAccess.startUploadVehicleImgCluster error outNvrCode == null");
							}
						}
					}
					log.info("[AgController] [doWork] [startUploadVehicleImg] [end]");
					log.info("[AgController] [doWork] [uploadVehicleImgs] [start]");
					if (outNvrCode != null) {
						log.debug("VCNAccess.startUploadVehicleImg return NVRCode : " + outNvrCode + ":len:"
								+ outNvrCode.length());
						for (int i = 0; i < con; i++) {
							ysMotor = ySMotorList.get(i);
							ysMotor.setNVRCode(outNvrCode);
						}
						picIDs = VCNAccess.uploadVehicleImgs(iSessionID, outNvrCode, pDomCode, pClusterCode, picsBuffer,
								snapTime, handle, mode);
						log.debug("parse vehicle imge success-picIDs(long[]) : " + picIDs);
					}
					log.info("[AgController] [doWork] [uploadVehicleImgs] [start]");

//					int cnt1 = 0;

					// 组装ID，PicID, PicName
					if (picIDs != null && picIDs.length > 0) {
						int pcon = ySMotorList.size();
						
						VCMImageMotor vcmPic = null;

						for (int i = 0; i < pcon; i++) {

							ysMotorPic = ySMotorList.get(i);
							// setPic
							for (j = 0; j < Integer.valueOf(ysMotorPic.getPicNumber()); j++) {
								mPic = ysMotorPic.getImage().get(j);
								vcmPic = new VCMImageMotor();
								// 把lo的值遍历出来
								vcmPic.setAutoID(ysMotorPic.getAutoID());
//								vcmPic.setPicID(String.valueOf(picIDs[cnt1 + j]));
								vcmPic.setPicID(String.valueOf(picIDs[j]));
								vcmPic.setImageType(mPic.getImageType());
								vcmPic.setWatchTimeMill(VCMAccess.setYsTimeMill(mPic.getPassTime()));
								vcmPic.setWatchTime(VCMAccess.setYsTime(mPic.getPassTime()));
								vcmPic.setImageHeight(mPic.getImageHeight());
								vcmPic.setImageWidth(mPic.getImageWidth());
								vcmPicList.add(vcmPic);
							}
//							cnt1 += j;
						}
					}
				} else {
					log.warn("no yushi vehicle imge");
				}
				log.info("[AgController] [doWork] [VCN] [end]");
				// ------------------VCN-----------End------

				// 3.取实体Bean中的数据上传到VCM-----------start------
				log.info("[AgController] [doWork] [VCM] [start]");
				log.info("[AgController] [doWork] [VCM] [start] [insert data-->VehicleInfo]");
				// ----1.过车数据-根据实体Bean转换成VCM需要的XML格式
				String xmlMetadataIndex = VCMAccess.getVCMBean(cf.getStorageName(), cf.getTableNameVehInfo(),
						ySMotorList, CheckPointStaticUtils.YU_SHI);

				// ----2.通过HTTP请求上传到VCM
				// 把转换后的过车数据XML-存储到表中
				VCMAccess.vcmVACreateMetadataIndex(strCookie, xmlMetadataIndex);
				log.info("[AgController] [doWork] [VCM] [start] [insert data-->VehicleInfo]");
				if (mPicList != null && mPicList.size() > 0 && picIDs != null) {
					log.info("[AgController] [doWork] [VCM] [start] [insert data-->VPicInfo]");
					// 把转换后的过车图片数据XML-存储到表中MotorPic
					String xmlMotorPicData = VCMAccess.getVCMBean(cf.getStorageName(), cf.getTableNameVPicInfo(),
							vcmPicList, CheckPointStaticUtils.OTHER_TYPE);

					VCMAccess.vcmVACreateMetadataIndex(strCookie, xmlMotorPicData);
					log.info("[AgController] [doWork] [VCM] [end] [insert data-->VPicInfo]");
				}
				
				//录像信息
				if (videoList != null && videoList.size() > 0) {
					log.info("[AgController] [doWork] [VCM] [start] [insert data-->VInfoExt]");
					// 把转换后的过车图片数据XML-存储到表中MotorPic
					String xmlVInfoExtData = VCMAccess.getVCMBean(cf.getStorageName(), cf.getTableNameVInfoExt(),
							videoList, CheckPointStaticUtils.OTHER_TYPE);

					VCMAccess.vcmVACreateMetadataIndex(strCookie, xmlVInfoExtData);
					log.info("[AgController] [doWork] [VCM] [end] [insert data-->VInfoExt]");
				}
				
				log.info("[AgController] [doWork] [VCM] [end]");

				/** ---------卡口LIST-----*/
//				ChPListConstant.saveYSChPList(strCookie, ySMotorList, cf);

				ySMotorList.clear();
				ySMotorList = null;

				picsBuffer.clear();
				picsBuffer = null;

				mPicList.clear();
				mPicList = null;
				
				vcmPicList.clear();
				vcmPicList = null;
				
				videoList.clear();
				videoList = null;

			}

		}
		log.info("[AgController] [doWork] [end]<<<<<<<<<<<<<<<<<<<<<");
		return ret;
	}
}
