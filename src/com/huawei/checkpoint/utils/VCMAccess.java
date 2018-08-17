package com.huawei.checkpoint.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dozer.DozerBeanMapper;

import com.huawei.checkpoint.data.LuoyangMotor;
import com.huawei.checkpoint.data.LyConstant;
import com.huawei.checkpoint.data.VCMImageMotor;
import com.huawei.checkpoint.data.VCMMotor;
import com.huawei.checkpoint.data.YsConstant;

/**
 * VCM 公共类
 * 
 * @author Xialf
 *
 */
public class VCMAccess {

	private static Logger log = Logger.getLogger(VCMAccess.class);

	/** https 头 */
	public static String strHttp = "https://";

	/** 登陆接口MNT_Login-URL */
	public static String applicationLogin = "/sdk_service/rest/management/application-login";

	/** 建立存储空间-URL */
	public static String createStorageSpace = "/sdk_service/rest/image-library/create-storage-space";

	/** 创建元数据索引-URL */
	public static String batchInsertData = "/sdk_service/rest/adaptor/batchInsertData";

	/** 元数据检索(VA_Metadata_Search)-URL */
	public static String queryVAMetadata = "/sdk_service/rest/adaptor/queryData";

	/** 建立卡口数据存储空间-XML */
	public static String vehicleInfoStorageSpace = "/resource/VehicleInfoStorageSpace.xml";

	/** 建立卡口图片存储空间-XML */
	public static String motorPicStorageSpace = "/resource/VPicInfoStorageSpace.xml";
	
	/** 建立卡口List存储空间-XML */
	public static String chPListStorageSpace = "/resource/CheckPointInfoStorageSpace.xml";
	
	/** 建立车辆备用信息存储空间-XML */
	public static String vInfoExtStorageSpace = "/resource/VInfoExtStorageSpace.xml";
	
	/** 建立订阅信息表存储空间-XML */
	public static String subscribeInfoStorageSpace = "/resource/SubscribeInfoStorageSpace.xml";

	/** 元数据检索(VA_Metadata_Search)-XML */
	public static String luoyangMetadataSearch = "/resource/LuoyangMetadataSearch.xml";

	/** 获取存储空间列表(MNT_List_Storage_Space)-URL */
	public static String queryVCMStorageSpaceUrl = "/sdk_service/rest/image-library/list-storage-space";

	/** 保活消息(MNT_VA_Get_Options)-URL */
	public static String mntVAGetOPtionsUrl = "/sdk_service/rest/management/options";

	/** 退出登陆接口(MNT_Logout)-URL */
	public static String mntLogout = "/sdk_service/rest/management/application-logout";

	/** xml 洛阳转模板 */
	public static String LYDataStencil = "resource/LYDataStencil.xml";
	public static String YSDataStencil = "resource/YSDataStencil.xml";

	public static String invalidData = "-1";

	public static String UnrecognizedData = "?";

	static HttpClientUtil httpClientUtil = new HttpClientUtil();
	
	/**
	 * 静态的DozerBeanMapper
	 */
	private static DozerBeanMapper lyMapper = new DozerBeanMapper();
	
	private static DozerBeanMapper ysMapper = new DozerBeanMapper();
	static {
			List<String> myMappingFiles = new ArrayList<String>();
			myMappingFiles.add(LYDataStencil);
			lyMapper.setMappingFiles(myMappingFiles);
			
			List<String> ysMappingFiles = new ArrayList<String>();
			ysMappingFiles.add(YSDataStencil);
			ysMapper.setMappingFiles(ysMappingFiles);
	}

	/**
	 * 登陆接口MNT_Login
	 * 
	 * @return
	 */
	public static String vcmApplicationLogin() {

		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + applicationLogin;
		Map<String, String> createMap = new HashMap<String, String>();
		// createMap.put("Keep-alive", "true");
		createMap.put("APP_NAME", cf.getVCMUser());

		createMap.put("REGISTER_CODE", cf.getVCMPass());
		String httpOrgCreateTestRtn = httpClientUtil.MNT_Login(httpOrgCreateTest, createMap, cf.getVCMCharset());
		String result2 = "";
		if (httpOrgCreateTestRtn != null) {
			String[] result = httpOrgCreateTestRtn.split(" ");
			result2 = result[1].replace(";", "");
		}
		log.debug("登陆接口MNT_Login result: " + result2);
		return result2;
	}

	public static String isLogin(String httpOrgCreateTestRtn) {

		Document doc = null;
		String codeStr = "";
		String cookie = null;
		try {
			doc = DocumentHelper.parseText(httpOrgCreateTestRtn);
			codeStr = doc.getRootElement().element("result").element("code").getText();
			if (!CheckPointStaticUtils.VCM_SUCCESS.equals(codeStr)
					&& CheckPointStaticUtils.VCM_LOGIN_TIME_OUT.equals(codeStr)) {
				log.warn("登陆超时，即将为您重新登录");
				cookie = vcmApplicationLogin();
			}
		} catch (DocumentException e) {
			log.warn("VCMAccess islogin documentException " , e);
			//e.printStackTrace();
		}catch (Exception e) {
			log.warn("VCMAccess islogin Exception " , e);
			//e.printStackTrace();
		}
		return cookie;
	}

	/**
	 * 建立数据存储空间
	 * 
	 * @param cookie
	 * @param strMotorStorageSpace
	 * @param strStorageName
	 * @param strQuota
	 * @param strTableName
	 * @return
	 */
	public static String vcmMNTCreateStorageSpace(String cookie, String strMotorStorageSpace, String strStorageName,
			int strQuota, String strTableName) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + createStorageSpace;
		Map<String, String> createMap = new HashMap<String, String>();
		String xmlData = httpClientUtil.readTxtFile(strMotorStorageSpace);
		String newXmlData = updataVCMStorageXmlElment(strStorageName, strQuota, strTableName, xmlData);
		
		String httpOrgCreateTestRtn = null;
		if(newXmlData != null) {
			createMap.put("xml", newXmlData);
			// createMap.put("xml",
			// httpClientUtil.readTxtFile(luoyangMotorStorageSpace));
			httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
			log.debug("建立存储空间:StorageName-" + strStorageName + ";Quota(M)-" + strQuota + ";Table-" + strTableName + ";"
					+ httpOrgCreateTestRtn);
		}
		return httpOrgCreateTestRtn;
	}

	/**
	 * 建立图片存储空间
	 * 
	 * @param cookie
	 * @return
	 */
	public static String vcmMNTCreatePicStorageSpace(String cookie, String strStorageName, String strQuota,
			String strTableName) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + createStorageSpace;
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("xml", httpClientUtil.readTxtFile(motorPicStorageSpace));
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("建立图片存储空间  : " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}

	/**
	 * 创建元数据索引
	 * 
	 * @param cookie
	 * @param strXml
	 * @return
	 */
	public static String vcmVACreateMetadataIndex(String cookie, String strXml) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + batchInsertData;
		Map<String, String> createMap = new HashMap<String, String>();
		// createMap.put("xml", httpClientUtil.readTxtFile(strXml));
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("创建元数据索引:" + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}

	/**
	 * 元数据检索(VA_Metadata_Search)
	 * 
	 * @param cookie
	 * @return
	 */
	public static String vcmVAMetadataSearch(String cookie) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("xml", httpClientUtil.readTxtFile(luoyangMetadataSearch));
		// createMap.put("xml",luoyangMetadataSearch);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("元数据检索(VA_Metadata_Search) : " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}

	/**
	 * 获取存储空间列表(MNT_List_Storage_Space)
	 * 
	 * @param cookie
	 * @return
	 */
	public static String queryVCMStorageSpaceList(String cookie) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVCMStorageSpaceUrl;
		String httpOrgCreateTestRtn = httpClientUtil.MNT_GET(httpOrgCreateTest, cf.getVCMCharset(), cookie);
		log.debug("获取存储空间列表(MNT_List_Storage_Space) : " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}

	/**
	 * 保活消息(MNT_VA_Get_Options)
	 * 
	 * @param cookie
	 * @return
	 */
	public static String mntVAGetOPtions(String cookie) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + mntVAGetOPtionsUrl;
		Map<String, String> createMap = new HashMap<String, String>();
		// createMap.put("xml",luoyangMetadataSearch);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		String cookieNew = "";
		if(httpOrgCreateTestRtn != null){ 
			cookieNew = isLogin(httpOrgCreateTestRtn);
		}
		log.debug("保活消息(MNT_VA_Get_Options) : " + httpOrgCreateTestRtn);
		return cookieNew;
	}

	/**
	 * 退出登陆接口(MNT_Logout)
	 * 
	 * @param cookie
	 * @return
	 */
	public static String mntLogout(String cookie) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + mntLogout;
		Map<String, String> createMap = new HashMap<String, String>();
		// createMap.put("xml",luoyangMetadataSearch);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
//		String cookieNew = isLogin(httpOrgCreateTestRtn);
		log.debug("退出登陆接口(MNT_Logout) : " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}

	/**
	 * 洛阳过车时间转换
	 * 
	 * @param lyList
	 * @return
	 */
	public static List<LuoyangMotor> setMotor(List<LuoyangMotor> lyList) {
		for (int i = 0; i < lyList.size(); i++) {
			LuoyangMotor motor = lyList.get(i);
			motor.setWatchTimeMill(setTimeMill(motor.getWatchTime()));
			motor.setWatchTime(setTime(motor.getWatchTime()));
			motor.setWatchTime1Mill(setTimeMill(motor.getWatchTime1()));
			motor.setWatchTime1(setTime(motor.getWatchTime1()));
			motor.setWatchTime2Mill(setTimeMill(motor.getWatchTime2()));
			motor.setWatchTime2(setTime(motor.getWatchTime2()));
			motor.setWatchTime3Mill(setTimeMill(motor.getWatchTime3()));
			motor.setWatchTime3(setTime(motor.getWatchTime3()));
			motor.setWatchTime4Mill(setTimeMill(motor.getWatchTime4()));
			motor.setWatchTime4(setTime(motor.getWatchTime4()));
		}

		return lyList;
	}
	/**
	 * 洛阳协议 格式化 日期 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param timeStr
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String setTime(String timeStr) {
		String timeReturn = "1111-11-11 11:11:11";
		if ("".equals(timeStr) || UnrecognizedData.equals(timeStr)) {
			return timeReturn;
		} else {
			SimpleDateFormat oritime = null;
			if(timeStr.length() > 19){
				oritime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			}else{
				oritime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = oritime.parse(timeStr);
				timeReturn = time.format(date);
				return timeReturn;
			} catch (ParseException e) {
				return timeReturn;
			}
		}
	} 

	/**
	 * 洛阳协议 格式化毫秒
	 * 
	 * @param timeStr
	 * @return
	 */
	public static String setTimeMill(String timeStr) {

		String timeReturn = invalidData;

		if ("".equals(timeStr) || UnrecognizedData.equals(timeStr)) {
			return timeReturn;
		} else {
			SimpleDateFormat oritime = null;
			if(timeStr.length() > 19){
				oritime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			}else{
				oritime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return "0";
			}
			SimpleDateFormat millTime = new SimpleDateFormat("SSS");
			try {
				Date date = oritime.parse(timeStr);
				timeReturn = millTime.format(date);
				return timeReturn;
			} catch (ParseException e) {
				return timeReturn;
			}
		}
	}
	
	/**
	 * 宇视协议 格式化 日期
	 * 
	 * @param timeStr
	 * @return
	 */
	public static String setYsTime(String timeStr) {
		String timeReturn = "1111-11-11 11:11:11";
		if ("".equals(timeStr) || UnrecognizedData.equals(timeStr)) {
			return timeReturn;
		} else {
			SimpleDateFormat oritime = null;
			if(timeStr.length() > 14){
				oritime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			}else{
				oritime = new SimpleDateFormat("yyyyMMddHHmmss");
			}
			SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = oritime.parse(timeStr);
				timeReturn = time.format(date);
				return timeReturn;
			} catch (ParseException e) {
				return timeReturn;
			}
		}
	}
	public static void main(String[] args) {
		setYsTimeMill("20170102112233");
	}

	/**
	 * 宇视协议 格式化毫秒
	 * 
	 * @param timeStr
	 * @return
	 */
	public static String setYsTimeMill(String timeStr) {

		String timeReturn = invalidData;

		if ("".equals(timeStr) || UnrecognizedData.equals(timeStr)) {
			return timeReturn;
		} else {
			SimpleDateFormat oritime = null;
			if(timeStr.length() > 14){
				oritime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			}else{
				oritime = new SimpleDateFormat("yyyyMMddHHmmss");
				return "0";
			}
			SimpleDateFormat millTime = new SimpleDateFormat("SSS");
			try {
				Date date = oritime.parse(timeStr);
				timeReturn = millTime.format(date);
				return timeReturn;
			} catch (ParseException e) {
				return timeReturn;
			}
		}
	}

	/**
	 * 修改VCM XML 中的字段值
	 * 
	 * @param spaceName
	 * @param quota
	 * @param tableName
	 * @param xmlData
	 * @return
	 */
	public static String updataVCMStorageXmlElment(String spaceName, int quota, String tableName, String xmlData) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xmlData);
			Element root = doc.getRootElement();
			root.element("space-name").setText(spaceName);
			root.element("quota").setText(String.valueOf(quota));
			root.element("table-name").setText(tableName);
			
			if(doc != null ){
				return doc.asXML();
			}
		} catch (DocumentException e) {
			log.warn("updataVCMStorageXmlElment documentException " , e);
			//e.printStackTrace();
		}catch (Exception e) {
			log.warn("updataVCMStorageXmlElment Exception " , e);
			//e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 通过IP、查询VCM是否存在数据。
	 * 
	 * @param cookie
	 * @param strClientIP
	 * @return
	 */
	public static String queryChPointListByIP(String cookie, String strClientIP) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameChplist()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>30</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";// IP-传入
		strXml += "<key>IP</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>" + strClientIP + "</value>";
		strXml += "</condition>";
		strXml += "<condition>";// SourceType=1 宇視
		strXml += "<key>SourceType</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>"+CheckPointStaticUtils.YU_SHI+"</value>";
		strXml += "</condition>";
		strXml += "<condition>";// SourceType=1 宇視
		strXml += "<key>XTBH</key>";
		strXml += "<oper>2</oper>";
		strXml += "<value>null</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";
		
		
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("queryChPointListByIP XML: " + strXml);
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("queryChPointListByIP Response: " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}
	/**
	 * 将过车数据或过车图片转成VCM需要的xml格式
	 * 
	 * @param spaceNames
	 * @param tableNames
	 * @param list
	 * @param type
	 *            0:洛阳，1：宇视, 2：VCM图片
	 * @return
	 */
	public static String getVCMBean(String spaceNames, String tableNames, List<?> list, int type) {

		List<Object> motorlist = new ArrayList<Object>();
		if (type == 0 || type == 1) {
			for (int i = 0; i < list.size(); i++) {
				VCMMotor motor = null;
				if (type == 0) {// 洛阳
					motor = (VCMMotor) lyMapper.map(list.get(i), VCMMotor.class);
					LyConstant.lyToCommon(motor);
				} else if (type == 1) {// 宇视
					motor = (VCMMotor) ysMapper.map(list.get(i), VCMMotor.class);
					YsConstant.ysToCommon(motor);
				}

				motorlist.add(motor);
			}
		} else if (type == 2) {
			motorlist = (List<Object>) list;
		}

		String motorXML = "";
		if(motorlist != null && motorlist.size() > 0){
			motorXML = getMetadataIndexStr(spaceNames, tableNames, motorlist);

//			log.debug("生成VCM需要的xml:将过车数据转成VCM需要的xml格式" + motorXML);
		}
		return motorXML;
	}
	

	/**
	 * 生成VCM需要的XML文件：创建元数据索引XML(VA_Create_Metadata_Index)
	 * 
	 * @param spaceNames
	 * @param tableNames
	 * @param motor
	 * @return
	 */
	public static String getMetadataIndexStr(String spaceNames, String tableNames, List<Object> Motorlist) {

		Document document = DocumentHelper.createDocument();// 创建根节点
		Element request = document.addElement("request");
		Element spaceName = request.addElement("space-name");
		spaceName.setText(spaceNames);
		Element tableName = request.addElement("table-name");
		tableName.setText(tableNames);
		Element count = request.addElement("count");
		count.setText(String.valueOf(Motorlist.size()));
		Element metaData = request.addElement("meta-data");

		for (int i = 0; i < Motorlist.size(); i++) {
			Element element = metaData.addElement("element");
			element = getElement(element, Motorlist.get(i));
		}
		log.debug("生成VCM需要的XML文件：创建元数据索引XML:" + document.asXML());
		return document.asXML();
	}

	/**
	 * VCMMotorData
	 * 
	 * @param element
	 * @param object
	 * @return
	 */
	public static Element getElement(Element element, Object object) {
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				String methName = fields[i].getName();
				boolean exists = existsField(object.getClass(), methName);
				if (exists) {
					// 反射get方法
					Method meth = object.getClass().getMethod("get" + methName);
					// 为二级节点添加属性，属性值为对应属性的值
					element.addElement(methName).setText(meth.invoke(object).toString());
				}
			}
		} catch (NoSuchMethodException | SecurityException e) {
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}
		return element;
	}

	public static boolean existsField(Class<?> clz, String fieldName) {
		try {
			return clz.getDeclaredField(fieldName) != null;
		} catch (Exception e) {
		}
		if (clz != Object.class) {
			return existsField(clz.getSuperclass(), fieldName);
		}
		return false;
	}
	 
	/**
	 * 通过DeviceID、SourceType，查询VCM是否存在数据。
	 * 
	 * @param cookie
	 * @param strDeviceID 设备编号
	 * @param sourceType 信息来源
	 * @return
	 */
	public static boolean queryChPointListByDeviceID(String cookie, String strDeviceID,int sourceType,Config cf) {
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameChplist()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>30</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";// IP-传入
		strXml += "<key>DeviceID</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>" + strDeviceID + "</value>";
		strXml += "</condition>";
		strXml += "<condition>";// SourceType=1 宇視
		strXml += "<key>SourceType</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>"+sourceType+"</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";
		
		
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("queryChPointListByDeviceID XML: " + strXml);
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("queryChPointListByDeviceID Response: " + httpOrgCreateTestRtn);
		boolean isExits = isExitsByTollgateID(httpOrgCreateTestRtn);
		return isExits;
	}
	
	//TODO kakoulist start
	/**
	 * 查询全部卡口编号
	 * 
	 * @param cookie
	 * @param strDeviceID 设备编号
	 * @param sourceType 信息来源
	 * @return
	 */	
	public static String queryAllChPointList(String cookie, int sourceType, Config cf) {
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameChplist()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>30</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";// IP-传入
		strXml += "<key>DeviceID</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>" + "" + "</value>";    //devide id -> null
		strXml += "</condition>";
		strXml += "<condition>";// SourceType=1 宇視
		strXml += "<key>SourceType</key>";
		strXml += "<oper>2</oper>";                        // 2 -> !=
		strXml += "<value>"+sourceType+"</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";
		
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("queryAllChPointList XML: " + strXml);
		createMap.put("xml", strXml);
		
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("queryAllChPointList Response: " + httpOrgCreateTestRtn);
		
		return httpOrgCreateTestRtn;
	}

	//TODO kakoulist end

	/**
	 * 验证卡口编号，在卡口LIST中是否存在
	 * 
	 * @param ckListReturn
	 * @return true- 存在  ；false - 不存在
	 */
	public static boolean isExitsByTollgateID(String ckListReturn) {
		boolean isExits = false;
		try {
			Document document = DocumentHelper.parseText(ckListReturn);
//			int totalCount = Integer.parseInt((document.selectSingleNode("//totalCount")).getText());
			String code = document.getRootElement().element("result").element("code").getTextTrim();
			if(code.equals("0")){

				int totalCount = Integer.parseInt(document.getRootElement().element("result").element("totalCount").getTextTrim());
				if (totalCount > 0) {
					isExits = true;
				}
			}
			
		} catch (DocumentException e) {
			log.warn("VCMAccess isExitsByTollgateID documentException: "+ckListReturn ,e);
			//e.printStackTrace();
		}catch (Exception e) {
			log.warn("VCMAccess isExitsByTollgateID Exception: "+ckListReturn , e);
			//e.printStackTrace();
		}
		return isExits;
	}
	
	
	
	/**
	 * 通过strCamID、SourceType，查询VCM卡口List表中的ClusterCode。
	 * 
	 * @param cookie
	 * @param strCamID 相机编号
	 * @param sourceType 信息来源
	 * @return
	 */
	public static String queryChPointListByCamID(String cookie, String strCamID,int sourceType,Config cf) {
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameChplist()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>30</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";// IP-传入
		strXml += "<key>CamID</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>" + strCamID + "</value>";
		strXml += "</condition>";
		strXml += "<condition>";// SourceType=1 宇視
		strXml += "<key>SourceType</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>"+sourceType+"</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";
		
		
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("queryChPointListByCamID XML: " + strXml);
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("queryChPointListByCamID Response: " + httpOrgCreateTestRtn);
		boolean isExits = isExitsByTollgateID(httpOrgCreateTestRtn);
		String strClusterCode ="";
		if(isExits){
			Document document;
			try {
				document = DocumentHelper.parseText(httpOrgCreateTestRtn);
				strClusterCode = document.getRootElement().element("result").element("meta-data").element("element").element("ClusterCode").getTextTrim();
			} catch (DocumentException e) {
				log.warn("VCMAccess queryChPointListByCamID documentException: "+strCamID , e);
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}catch (Exception e) {
				log.warn("VCMAccess queryChPointListByCamID Exception: "+strCamID, e);
				//e.printStackTrace();
			}
		}else{
			strClusterCode ="";
		}
		return strClusterCode;
	} 
	
	/**
	 * 查询VCM是否存在iqueryNum条数据。
	 * 
	 * @author dong_b
	 * @param cookie
	 * @param iqueryNum
	 * @return
	 */
	public static String queryVehInfoByTime(String cookie, int num, String startTime, String endTime) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameVehInfo()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>"+ num +"</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";
		strXml += "<key>UploadTime</key>";
		strXml += "<oper>3</oper>";
		strXml += "<value>" + startTime + "</value>";
		strXml += "</condition>";
		strXml += "<condition>";
		strXml += "<key>UploadTime</key>";
		strXml += "<oper>5</oper>";
		strXml += "<value>" + endTime + "</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";		
		
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("queryVehInfoByTime XML: " + strXml);
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("queryVehInfoByTime Response: " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}
	
	/**
	 * 查询VCM是否存在iqueryNum条数据。
	 * 
	 * @author dong_b
	 * @param cookie
	 * @param iqueryNum
	 * @return
	 */
	public static String queryVPicInfoByAutoId(String cookie, int iqueryNum, String autoId) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameVPicInfo()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>" + iqueryNum + "</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";
		strXml += "<key>AutoID</key>";
		strXml += "<oper>1</oper>";
		strXml += "<value>" + autoId + "</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";		
		
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("queryVehInfoByAutoId XML: " + strXml);
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("queryVehInfoByAutoId Response: " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}
	
	/**
	 * 查询VCM是否存在订阅信息。
	 * 
	 * @author dong_b
	 * @param cookie
	 * @param iqueryNum
	 * @return
	 */
	public static String querySubInfo(String cookie, int iqueryNum, String strSubId) {
		Config cf = Config.getIns();
		String httpOrgCreateTest = strHttp + cf.getVCMAddr() + queryVAMetadata;
		
		String strXml = "";
		strXml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		strXml += "<request>";
		strXml += "<space-name>"+cf.getStorageName()+"</space-name>";
		strXml += "<table-name>"+cf.getTableNameSubInfo()+"</table-name>";
		strXml += "<page>";
		strXml += "<no>1</no>";
		strXml += "<size>" + iqueryNum + "</size>";
		strXml += "</page>";
		strXml += "<element>";
		strXml += "<condition>";
		strXml += "<key>SubscribeID</key>";
		strXml += "<oper>2</oper>";
		strXml += "<value>" + strSubId + "</value>";
		strXml += "</condition>";
		strXml += "</element>";
		strXml += "</request>";
				
		Map<String, String> createMap = new HashMap<String, String>();
		log.debug("querySubInfo XML: " + strXml);
		createMap.put("xml", strXml);
		String httpOrgCreateTestRtn = httpClientUtil.MNT_POST(httpOrgCreateTest, createMap, cf.getVCMCharset(), cookie);
		log.debug("querySubInfo Response: " + httpOrgCreateTestRtn);
		return httpOrgCreateTestRtn;
	}

}
