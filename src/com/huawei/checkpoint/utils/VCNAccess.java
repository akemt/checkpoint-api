package com.huawei.checkpoint.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class VCNAccess {

	private static final int MAX_IMAGES = 90;
	private static Logger log = Logger.getLogger(VCNAccess.class);

	public interface CLibrary extends Library {
		// NativeLibrary.addSearchPath("libIVS_SDK.so", "");

		CLibrary INSTANCE = (CLibrary) Native.loadLibrary("IVS_SDK", CLibrary.class);

		public int IVS_SDK_Init();

		public int IVS_SDK_SetLogPath(String path);

		public int IVS_SDK_Cleanup();

		public int IVS_SDK_GetVersion();

		public int IVS_SDK_Login(StructUtil.IVS_LOGIN_INFO.ByReference info, IntByReference sessionID);

		public int IVS_SDK_Logout(int sessionID);

		public int IVS_SDK_StartUploadVehicleImg(int sessionID, StructUtil.IVS_IMG_STORE_NVR_INFO.ByReference info,
				PointerByReference handle);

		public int IVS_SDK_UploadVehicleImg(int sessionID, Pointer handle, PointerByReference pointer, int picNum);

		public int IVS_SDK_UploadVehicleImg(int sessionID, Pointer handle,
		 StructUtil.IVS_IMG_STORE_INFO.ByReference [] pointer, int picNum);
		public int IVS_SDK_StopUploadVehicleImg(int sessionID, Pointer handle);

		public int IVS_SDK_PlatformSnapshot(int iSessionID, String pCameraCode);

		public int IVS_SDK_GetPTZPresetList(int iSessionID, String pCameraCode,
				StructUtil.IVS_PTZ_PRESET.ByReference pPTZPresetList, int uiBufferSize, IntByReference uiPTZPresetNum);

		public int IVS_SDK_PtzControl(int iSessionID, String pCameraCode, int iControlCode, String pControlPara1,
				String pControlPara2, IntByReference pLockStatus);

		public int IVS_SDK_PtzControlWithLockerInfo(int iSessionID, String pCameraCode, int iControlCode,
				String pControlPara1, String pControlPara2,
				StructUtil.IVS_PTZ_CONTROL_INFO.ByReference pPtzControlInfo);

		public int IVS_SDK_QueryDeviceList(int iSessionID, String pDomainCode, int uiDeviceType,
				StructUtil.IVS_QUERY_UNIFIED_FORMAT.ByReference pQueryFormat, Pointer pBuffer, int uiBufferSize);

		public int IVS_SDK_GetDomainDeviceList(int iSessionID, String pDomainCode, int uiDeviceType,
				StructUtil.IVS_INDEX_RANGE.ByReference pIndexRange, Pointer pDeviceList, int uiBufferSize);
	}

	public static boolean initSDK() {
		int result = -1;

		result = CLibrary.INSTANCE.IVS_SDK_Init();
		log.debug("initSDK=" + result);
		if (result != 0) {
			log.error("initSDK=" + result);
			return false;
		}
		return true;
	}

	public static int logInVCN(String vcnIP, int vcnPort, String user, String pass) {
		int ret = -1;
		int result = -1;
		StructUtil.IVS_LOGIN_INFO.ByReference info = new StructUtil.IVS_LOGIN_INFO.ByReference();

		System.arraycopy(user.getBytes(), 0, info.cUserName, 0, user.length());
		System.arraycopy(pass.getBytes(), 0, info.pPWD, 0, pass.length());

		info.uiPort = vcnPort;

		info.uiClientType = 0;
		info.uiLoginType = 0;

		System.arraycopy(vcnIP.getBytes(), 0, info.stIP.cIP, 0, vcnIP.length());

		info.stIP.uiIPType = 0;
		IntByReference sessionID = new IntByReference();

		result = CLibrary.INSTANCE.IVS_SDK_Login(info, sessionID);
		if (result != 0)
			ret = -1;
		ret = sessionID.getValue();
		return ret;
	}

	public static int logOutVCN(int sessionID) {
		return CLibrary.INSTANCE.IVS_SDK_Logout(sessionID);
	}

	public static String startUploadVehicleImgCluster(int sessionID, PointerByReference handle,String pDomCode,String pClusterCode) {
		String ret = null;
		int result = -1;

		StructUtil.IVS_IMG_STORE_NVR_INFO.ByReference info = new StructUtil.IVS_IMG_STORE_NVR_INFO.ByReference();

		
		//Arrays.fill(info.cDomainCode, 0, info.cDomainCode.length, (byte)0);
		//Arrays.fill(info.cClusterCode, 0, info.cClusterCode.length, (byte)0);
		//Arrays.fill(info.cNVRCode, 0, info.cNVRCode.length, (byte)0);
		
		System.arraycopy(pDomCode.getBytes(), 0, info.cDomainCode, 0, pDomCode.length());
		
		System.arraycopy(pClusterCode.getBytes(), 0, info.cClusterCode, 0,pClusterCode.length());
		
		result = CLibrary.INSTANCE.IVS_SDK_StartUploadVehicleImg(sessionID, info, handle);
		if (result != 0) {// fail
			log.warn("startUploadVehicleImg-fail:"+result);
			ret = null;
		} else {// success
			info.read();  
			//try {
				//info.cNVRCode[32] = '\0';
				ret = new String(info.cNVRCode, 0, 32);
				
			//} catch (UnsupportedEncodingException e) {
			//	ret = null;
			//	e.printStackTrace();
			//} 
		}

		return ret;
	}

	
	public static int startUploadVehicleImg(int sessionID, PointerByReference handle, String pNVRCode,String pDomCode) {
		int ret = -1;
		int result = -1;

		StructUtil.IVS_IMG_STORE_NVR_INFO.ByReference info = new StructUtil.IVS_IMG_STORE_NVR_INFO.ByReference();

		//Arrays.fill(info.cDomainCode, 0, info.cDomainCode.length, (byte)0);
	
		//Arrays.fill(info.cNVRCode, 0, info.cNVRCode.length, (byte)0);
		System.arraycopy(pDomCode.getBytes(), 0, info.cDomainCode, 0, pDomCode.length());
		
		
		System.arraycopy(pNVRCode.getBytes(), 0, info.cNVRCode, 0, pNVRCode.length());
	
		result = CLibrary.INSTANCE.IVS_SDK_StartUploadVehicleImg(sessionID, info, handle);
		if (result != 0) {// fail
			log.warn("startUploadVehicleImg-fail:"+result);
			ret = -1;
		} else {// success
			ret = 0;
			
		}

		return ret;
	}

	public static long[] uploadVehicleImgs(int sessionID, String pNVRCode,String pDomCode,String pClusterCode,ArrayList<byte[]> picsBuffer,
			long[] snapTime,PointerByReference handle, int mode) {

		int num = 0;
		int result = -1;
//		PointerByReference handle = new PointerByReference(Pointer.NULL);
//		String outNvrCode=null;
//		
//		if(mode == 0) {
//			result = startUploadVehicleImg(sessionID, handle, pNVRCode,pDomCode);
//			outNvrCode = pNVRCode;
//			if (result != 0) {
//				return null;
//			}
//		}
//		else if(mode == 1) {
//			outNvrCode = startUploadVehicleImgCluster(sessionID, handle,pDomCode, pClusterCode);
//			if (outNvrCode == null) {
//				return null;
//			}
//		}
//		
//		if (outNvrCode == null) {
//			return null;
//		}
		num = picsBuffer.size();
		if (num > MAX_IMAGES) {
			num = MAX_IMAGES;
		}

		long ret[] = new long[num];
		StructUtil.IVS_IMG_STORE_INFO.ByReference infoarry[] = (StructUtil.IVS_IMG_STORE_INFO.ByReference[]) new StructUtil.IVS_IMG_STORE_INFO.ByReference()
				.toArray(num);
		//Pointer pinfo = infoarry[0].getPointer();
		//PointerByReference ppinfo = new PointerByReference(pinfo);

		for (int i = 0; i < num; i++) {
			byte buffer[] = (byte[]) picsBuffer.get(i);
			infoarry[i].ulPicLen = buffer.length;
			infoarry[i].ulPicBufLen = 1024 * 1024 * 2;
			infoarry[i].setData(buffer);
			infoarry[i].ullSnapTime = snapTime[i];
			
			//Arrays.fill(infoarry[i].cNVRCode, 0, infoarry[i].cNVRCode.length, (byte)0);
			System.arraycopy(pNVRCode.getBytes(), 0, infoarry[i].cNVRCode, 0, pNVRCode.length());

		}

		result = CLibrary.INSTANCE.IVS_SDK_UploadVehicleImg(sessionID, handle.getValue(), infoarry, num);
		if (result != 0) {
			log.warn("IVS_SDK_UploadVehicleImg error:" + result);
			log.info("[VCNAccess] [stopUploadVehicleImg] [start]");
			stopUploadVehicleImg(sessionID, handle.getValue());
			log.info("[VCNAccess] [stopUploadVehicleImg] [end]");
			for (int i = 0; i < num; i++) {
				long peer = Pointer.nativeValue(infoarry[i].pPictureBuf);
				Native.free(peer);
				Pointer.nativeValue(infoarry[i].pPictureBuf, 0);
			}
			ret = null;
			return ret;
		}
		for (int i = 0; i < num; i++) {
			infoarry[i].read();
			ret[i] = infoarry[i].ullPictureID;
			long peer = Pointer.nativeValue(infoarry[i].pPictureBuf);
			Native.free(peer);
			Pointer.nativeValue(infoarry[i].pPictureBuf, 0);
		}
		log.info("[VCNAccess] [stopUploadVehicleImg] [start]");
		int stopUpload = stopUploadVehicleImg(sessionID, handle.getValue());
		if(stopUpload < 0){
			log.warn("stopUploadVehicleImg error :" + stopUpload);
			ret = null;
			return ret;
		}
		log.info("[VCNAccess] [stopUploadVehicleImg] [end]");
		
		return ret;
	}

//	public static long uploadVehicleImg(int sessionID, String pNVRCode,String pDomCode,String pClusterCode, byte[] cPictureBuf, long snapTime,int mode) {
//		long ret = -1;
//		int result = -1;
//		PointerByReference handle = new PointerByReference(Pointer.NULL);
//		result = startUploadVehicleImg(sessionID, handle, pNVRCode,pDomCode,pClusterCode,mode);
//		if (result != 0) {
//			return ret;
//		}
//
//		// 图片数据
//		if (cPictureBuf == null) {
//			log.warn("pass cPictureBuf == null ");
//			return -1;
//		}
//
//		int ulPicLen = cPictureBuf.length;
//		if (ulPicLen <= 0) {
//			log.warn("pass cPictureBuf lenth :" + ulPicLen);
//			return -1;
//		}
//		StructUtil.IVS_IMG_STORE_INFO.ByReference info = new StructUtil.IVS_IMG_STORE_INFO.ByReference();
//		Pointer pinfo = info.getPointer();
//
//		PointerByReference ppinfo = new PointerByReference(pinfo);
//		// 每次上传1张图片
//		int cPicNum = 1;
//
//		// 图片缓冲区长度
//		info.ulPicLen = ulPicLen;
//
//		// 图片缓冲区长度
//		info.ulPicBufLen = 1024 * 1024 * 2;
//
//		info.setData(cPictureBuf);
//
//		// 图片对应的抓拍时间
//		info.ullSnapTime = snapTime;
//
//		// 保存图片的NVR编码
//		System.arraycopy(pNVRCode.getBytes(), 0, info.cNVRCode, 0, pNVRCode.length());
//
//		result = CLibrary.INSTANCE.IVS_SDK_UploadVehicleImg(sessionID, handle.getValue(), ppinfo, cPicNum);
//		if (result != 0) {
//			log.warn("UploadVehicleImg error:" + result);
//			ret = -1;
//			return ret;
//		}
//		info.read();
//		ret = info.ullPictureID;
//
//		return ret;
//	}

	public static int stopUploadVehicleImg(int sessionID, Pointer handle) {
		int ret = -1;
		int result = -1;

		result = CLibrary.INSTANCE.IVS_SDK_StopUploadVehicleImg(sessionID, handle);
		if (result != 0) {// fail
			log.warn("stopUploadVehicleImg error:" + result);
			ret = -1;
		} else {// success
			ret = sessionID;
		}

		return ret;
	}

	public static int testPreset(int iSessionID, String pDomainCode) {

		StructUtil.IVS_CAMERA_BRIEF_INFO_LIST.ByReference pDeviceList = new StructUtil.IVS_CAMERA_BRIEF_INFO_LIST.ByReference(
				10);
		int uiBufferSize = pDeviceList.size();
		StructUtil.IVS_INDEX_RANGE.ByReference pIndexRange = new StructUtil.IVS_INDEX_RANGE.ByReference();
		pIndexRange.uiFromIndex = 1;
		pIndexRange.uiToIndex = 10;
		int ret = CLibrary.INSTANCE.IVS_SDK_GetDomainDeviceList(iSessionID, pDomainCode, 2, pIndexRange,
				pDeviceList.getPointer(), uiBufferSize);
		pDeviceList.read();
		return ret;

	}

}
