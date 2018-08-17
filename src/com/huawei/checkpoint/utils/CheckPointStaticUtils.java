package com.huawei.checkpoint.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckPointStaticUtils {

	/** 卡口分类：洛阳-0 */
	public static final int LUO_YANG = 0;
	/** 卡口分类：宇视-1 */
	public static final int YU_SHI = 1;
	/** 卡口分类：其他类型 */
	public static final int OTHER_TYPE = 2;

	/** 共享网关sg 接收数据的方式：从接入网关接收数据到共享网关-0 */
	public static final int SG_PASS_THROUGH = 0;
	/** 共享网关sg 接收数据的方式：从VCM上查询数据到共享网关-1 */
	public static final int SG_NOT_PASS_THROUGH = 1;

	/** 网关：接入网关-0 access gateway*/
	public static final String GATEWAY_AG = "0";
	/** 网关：共享网关-1 Shared gateway*/
	public static final String GATEWAY_SG = "1";
	
	/** 网关：上传FTP方式-0  从原FTP上读取文件下载上传到目标FTP*/
	public static final int DOWNLOAD_UPLOAD = 0;
	/** 网关：上传FTP方式-1 直接从原FTP上传到目标FTP*/
	public static final int DIRECT_UPLOAD = 1;
	
	/**
	 * 随机生成序号ID
	 * 
	 * @return
	 */
	public static final String getUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		String uuidStr = str.replace("-", "");
		return uuidStr;
	}

	/** 网关：接入网关-0 access gateway type*/
	public static final int GATEWAY_AG_TYPE = 0;
	/** 网关：共享网关-1 Shared gateway type*/
	public static final int GATEWAY_SG_TYPE = 1;
	
	/** VCM VCN 登陆成功 1*/
	public static final int LOGIN_SUCCESS = 1;
	
	/** Thread.sleep 1000*/
	public static final int THREAD_SLEEP = 1000;
	
	/** 正斜杠  /  */
	public static final String FORWARD_SLASH = "/";
	/** 反斜杠  /  */
	public static final String BACK_SLASH = "'\'";

	/** 判断字符串是否为base64转换后的正则表达式 */
	public static final String IS_BASE64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

	/** VCM 成功CODE*/
	public static final String VCM_SUCCESS = "0";
	
	/** VCM 未登陆CODE*/
	public static final String VCM_LOGIN_TIME_OUT = "30870239110000";
	
	
	/** 字符串中包含 .xml*/
	public static final String STR_XML = "(.*).xml";
	
	/** 字符串中包含 .json*/
	public static final String STR_JSON = "(.*).json";
	
	
	
	@SuppressWarnings("serial")
	public static final Map<String, String> YUSHI_NODE = new HashMap<String, String>() {
		{
			put("CamID", "CamID");
			put("DevID", "DevID");
			put("EquipmentType", "EquipmentType");
			put("PanoramaFlag", "PanoramaFlag");
			put("RecordID", "RecordID");
			put("TollgateID", "TollgateID");
			put("TollgateName", "TollgateName");
			put("PassTime", "PassTime");
			put("PassTimeMill", "PassTimeMill");
			put("PlaceCode", "PlaceCode");
			put("PlaceName", "PlaceName");
			put("LaneID", "LaneID");
			put("LaneType", "LaneType");
			put("Direction", "Direction");
			put("DirectionName", "DirectionName");
			put("CarPlate", "CarPlate");
			put("PlateConfidence", "PlateConfidence");
			put("PlateType", "PlateType");
			put("PlateColor", "PlateColor");
			put("PlateNumber", "PlateNumber");
			put("PlateCoincide", "PlateCoincide");
			put("RearVehiclePlateID", "RVehiclePlateID");
			put("RearPlateConfidence", "RPlateConfidence");
			put("RearPlateColor", "RearPlateColor");
			put("RearPlateType", "RearPlateType");
			put("PicNumber", "PicNumber");
			put("VehicleSpeed", "VehicleSpeed");
			put("LimitedSpeed", "LimitedSpeed");
			put("MarkedSpeed", "MarkedSpeed");
			put("DriveStatus", "DriveStatus");
			put("VehicleBrand", "VehicleBrand");
			put("VehicleBody", "VehicleBody");
			put("VehicleType", "VehicleType");
			put("VehicleLength", "VehicleLength");
			put("VehicleColor", "VehicleColor");
			put("VehicleColorDept", "VehicleColorDept");
			put("DressColor", "DressColor");
			put("RedLightStartTime", "RLStartTime");
			put("RedLightStartTimeMill", "RLStartTimeMill");
			put("RedLightEndTime", "RLEndTime");
			put("RedLightEndTimeMill", "RLEndTimeMill");
			put("RedLightTime", "RedLightTime");
			put("DealTag", "DealTag");
			put("IdentifyStatus", "IdentifyStatus");
			put("IdentifyTime", "IdentifyTime");
			put("ApplicationType", "ApplicationType");
			put("GlobalComposeFlag", "GComposeFlag");
			put("VideoURL", "VideoURL");
			put("VideoURL2", "VideoURL2");
			put("ImageURL2", "ImageURL2");
			put("ImageURL3", "ImageURL3");
			put("ImageURL4", "ImageURL4");
			put("ID", "ID");
			put("DomCode", "DomCode");
			put("NVRCode", "NVRCode");
			put("Image", "Image");
		}
	};
	
	/**
	 * 验证startStr中是否包含targetStr 包含返回true 不包含返回false
	 * 
	 * @param startStr
	 * @param targetStr
	 * @return
	 */
	public static boolean isMatchesStr(String startStr, String targetStr) {

		boolean flag = false;
		if (startStr != null && targetStr != null) {
			flag = startStr.toLowerCase().matches(targetStr.toLowerCase());
		} else {

		}

		return flag;
	}
	
	/**
	 * 验证特殊字符中英文问号
	 * 
	 * @param str
	 */
	public static boolean VerifySpecialCharacters(String str) {
		// 要验证的字符串
		String regEx = "^[?|？]";
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		// 忽略大小写的写法
		// Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		// 字符串是否与正则表达式相匹配
		boolean rs = matcher.matches();
		return rs;
	} 
	
	/**
	 * 存在特殊符号- 重新复制
	 * oldStr 原值
	 * targetStr 目标值
	 * 
	 * @param str 存在则 targetStr
	 */
	public static String NoRecognize(String oldStr, String targetStr) {
		
		if (oldStr.indexOf("-")!= -1 || oldStr.indexOf("——")!= -1) {
			oldStr = targetStr;
		}
		return oldStr;
	} 
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String strSubString(String str){
		double value = Double.parseDouble(str);
		int ivalue = (int)value;
		return String.valueOf(ivalue);
	}
	
	/**
	 * 存在特殊符号? 重新复制
	 * oldStr 原值
	 * targetStr 目标值
	 * 
	 * @param str
	 */
	public static String transformRange(String oldStr, String targetStr) {
		
		if (oldStr.indexOf("?")!= -1 || oldStr.indexOf("？")!= -1) {
			oldStr = targetStr;
		}
		return oldStr;
	} 
	
	/**
	 * DecimalFormat转换保留两位小数
	*/
	 public static String m2(Double d) {
		 DecimalFormat df = new DecimalFormat("#0.00");
		 return df.format(d);
		 
	}
	 
	 /**
	 * DecimalFormat转换保留两位小数
	*/
	 public static String calculation(String data) {
		 String str = "";
		 if("".equals(data)){
			 str = "0.00";
		 }else{
			Double d = Double.valueOf(data);
			d = d / 100;
			str = m2(d);
		 }
		 return str;
	} 
	 
	 /**
	  * 去掉字符串前面的零（正则表达式）
	  * 
	  * @param str
	  * @return 如果：0000 或者 "" ，则 0；否则去掉前面的零
	  */
	public static String dropStrBeforeZero(String str) {
		String newStr = str.replaceAll("^(0+)", "");
		if(newStr.equals("")){
			newStr = "0";
		}
		return newStr;
	} 
	
	/**
	 * 删除ArrayList中重复元素，保持顺序
	 * 
	 * @param list
	 */
    public static List<String> removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
//        list.clear();
//      list.addAll(newList);
        return newList;
    }
}
