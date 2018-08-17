package com.huawei.checkpoint.data;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.function.SgSubscribeNotificationsController;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.VCMAccess;
import com.huawei.checkpoint.utils.CPListConfig;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.sun.jmx.snmp.Timestamp;

public class SubscribeNotificationsConstant {
	private static Logger log = Logger.getLogger(SubscribeNotificationsConstant.class);	
	// 默认值 - 0
	private static final String unknown = "0";
	// 默认值 - 未知:9
	private static final String other_9 = "9";
	// 默认值 - 未知:99
	private static final String other_99 = "99";

	private static final String protocol = "http://";
	private static final String httpDownloaduri = "/VIID/download/";

	private static final String pattern19 = "yyyy-MM-dd HH:mm:ss";
	private static final String pattern14 = "yyyyMMddHHmmss";
	//视频图像信息基本对象统一标识1-41
	//设备编码/应用平台编码  GB/T28181-2016附录D中D.1规定的编码规则 1-20
	private static final String centerCode = "41030000";
	private static final String industryCode = "05";
	private static final String typeCode = "209";
	private static final String networkIdentification = "0";
	private static final String serialNumber = "000000";

	//子类型编码 03-文件 21-22 
	private static final String subtypeCode = "03";

	//时间编码 YYYYMMDDhhmmss 23-36
	private static String curPassTime14 = "00000000000000";
	//视频图像信息基本对象序号37-41
	private static int picInfoSerialNumber = 0;

	//表示视频图像信息语义属性对象的类型42-43 02-机动车
	private static final String picInfoSubtypeCode = "02";

	//视频图像信息语义属性对象序号44-48
	private static final String picInfoAttrSerialNumber = "00000";

	public static JSONObject vcmToNotitications(List<Element> elements){
		JSONObject uploadInfo = new JSONObject();
		JSONObject motorVehicle = new JSONObject();
		JSONArray motorVehicleList = new JSONArray();
		log.debug("vcmToNotitications start");
		DecimalFormat df = new DecimalFormat("00000");
		for (int i = 0; i < elements.size(); i++) {
			String watchTime =  elements.get(i).element("WatchTime").getTextTrim();
			//	    	log.info("vcmToNotitications step1 start");
			String tempPassTime14 = getWantDate(watchTime, pattern14);
			if (curPassTime14.equals(tempPassTime14)) {
				picInfoSerialNumber++;
			} else {
				curPassTime14 = tempPassTime14;
				picInfoSerialNumber = 0;
			}
			//			log.info("vcmToNotitications step2 start");
			String motorVehicleId = centerCode + industryCode + typeCode + networkIdentification + serialNumber + 
					subtypeCode + curPassTime14 +
					df.format(picInfoSerialNumber) + picInfoSubtypeCode + picInfoAttrSerialNumber;
			motorVehicle.put("MotorVehicleID",  motorVehicleId);
			//motorVehicle.put("SourceID",  elements.get(i).element("xxxx"));
			
			String deviceKKid =  elements.get(i).element("DeviceID").getTextTrim();
			String deviceId = CPListConfig.getIns().getTollGateID(deviceKKid);
			motorVehicle.put("TransportID",  deviceId);
			motorVehicle.put("DeviceID",  deviceId);
			//			log.info("vcmToNotitications step3 start");
			String autoID = elements.get(i).element("AutoID").getTextTrim();
			String picId = getPicID(autoID);
			motorVehicle.put("StorageUrl1", picId);
			motorVehicle.put("LaneNo",  elements.get(i).element("CarRoad").getTextTrim());
			//motorVehicle.put("HasPlate",  elements.get(i).element("xxxx"));
			//motorVehicle.put("PlateClass",  elements.get(i).element("xxxx"));//洛阳协议不存在该字段，默认是空
			//			log.info("vcmToNotitications step4 start");
			String strPlateColor = plateColor.get(elements.get(i).element("PlateColor").getTextTrim());
			if (null == strPlateColor) {
				strPlateColor = other_99;
			}
			motorVehicle.put("PlateColor", strPlateColor);
			//			log.info("vcmToNotitications step5 start");
			motorVehicle.put("PlateNo",  elements.get(i).element("CarNo").getTextTrim());
			motorVehicle.put("Speed",  elements.get(i).element("CarSpeed").getTextTrim());

			String strDirection = direction.get(elements.get(i).element("Direction").getTextTrim());
			if (null == strDirection) {
				strDirection = other_9;
			}
			motorVehicle.put("Direction", strDirection);

			//洛阳协议不存在该字段 默认0
			motorVehicle.put("DrivingStatusCode",  elements.get(i).element("DriveStatus").getTextTrim());
			//			log.info("vcmToNotitications step6 start");
			String strVehicleClass = vehicleClass.get(elements.get(i).element("CarType").getTextTrim());
			if (null == strVehicleClass) {
				strVehicleClass = unknown;
			}		
			motorVehicle.put("VehicleClass", strVehicleClass);

			//赋值方式待定
			motorVehicle.put("VehicleBrand", carLogo.get(CheckPointStaticUtils.dropStrBeforeZero(elements.get(i).element("CarLogo").getTextTrim())));
			motorVehicle.put("VehicleLength", elements.get(i).element("VehicleLength").getTextTrim());

			//洛阳协议不存在该字段 默认Y
			motorVehicle.put("VehicleColor", elements.get(i).element("VehicleColor").getTextTrim());
			//			log.info("vcmToNotitications step7 start");
			//洛阳协议不存在该字段 默认0 上传时0：深、1：浅
			motorVehicle.put("VehicleColorDepth", elements.get(i).element("VehicleColorDept").getTextTrim());

			String passTime19 = getWantDate(watchTime, pattern19);
			motorVehicle.put("PassTime", passTime19);
			//			log.info("vcmToNotitications step8 start");
			motorVehicleList.add(i, motorVehicle);
		}
		uploadInfo.put("MotorVehicleObjectList", motorVehicleList);
		return uploadInfo;
	}

	/**
	 * 车牌颜色
	 * 洛阳协议：
	 *  0	未识别
	 *  1	蓝
	 *  2	黄
	 *  3	白
	 *  4	黑
	 *  5	绿
	 *  6	红
	 *  7	紫
	 *  8	粉
	 *  9	桔
	 *  10	其他
	 *  
	 * 省厅协议:
	 *  1	黑
	 *  2	白
	 *  5	蓝
	 *  6	黄
	 *  9	绿
	 *  99	其他 
	 */
	public final static Map<String, String> plateColor = new HashMap<String, String>() {
		{
			put("0", "99");
			put("1", "5");
			put("2", "6");
			put("3", "2");
			put("4", "1");
			put("5", "9");
			put("6", "99");
			put("7", "99");
			put("8", "99");
			put("9", "99");
			put("10", "99");
		}
	};

	/**
	 * 方向编号
	 */
	public final static Map<String, String> direction = new HashMap<String, String>() {
		{
			put("00", "9");
			put("1A", "1");
			put("1B", "2");
			put("2A", "3");
			put("2B", "4");
		}
	};

	/**
	 * 车辆类型
	 */
	public final static Map<String, String> vehicleClass = new HashMap<String, String>() {
		{
			put("0", "0");
			put("1", "3");
			put("2", "1");
			put("3", "2");
			put("4", "4");
		}
	};

	//	/*
	//	 * 获得想要的时间格式
	//	 * 
	//	 */
	//	public static String getWantDate(String dateStr, String wantFormat) {
	//		if (!"".equals(dateStr) && dateStr != null) {
	//			SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
	//			dateStr = sdf.format(new Date(dateStr));
	//		}
	//		return dateStr;
	//	}

	/*
	 * 获得想要的时间格式
	 * 
	 */
	public static String getWantDate(String dateStr, String wantFormat) {
		String strDate = "";
		SimpleDateFormat sdfe = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
		if (!"".equals(dateStr) && dateStr != null) {
			try {
				Date date1 = sdfe.parse(dateStr);
				SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
				strDate = sdf.format(date1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.warn("getWantDate exception,src data:"+dateStr, e);
				//e.printStackTrace();
			}
		}
		return strDate;
	}
	public static String getPicID(String autoID) {
		
		Config cf = Config.getIns();
		String ret = "";
		ret = protocol + cf.getHttpServerAddr() + ":" + cf.getHttpServerPort() + httpDownloaduri + autoID;
		
		
//		String strCookie = SystemManager.getIns().getCookie();
//		String reStr = VCMAccess.queryVPicInfoByAutoId(strCookie, 1, autoID);
//		//String ret = "";
//		try {
//			Document document = DocumentHelper.parseText(reStr);
//			String code = document.getRootElement().element("result").element("code").getTextTrim();
//			if(code.equals("0")){
//				List<Element> elements = document.getRootElement().element("result").element("meta-data").elements();
//				for (int i = 0; i < elements.size(); i++) {
//					Element PicNum = elements.get(i).element("PicID");
//					ret = protocol + cf.getHttpServerAddr() + ":" + cf.getHttpServerPort() + httpDownloaduri + PicNum.getText();
//				}
//				
////				List<Element> elements = document.getRootElement().element("result").element("meta-data").elements();
////				for (int i = 0; i < elements.size(); i++) {
////					Element PicNum = elements.get(i).element("PicID");
////					ret += protocol + cf.getHttpServerAddr() + ":" + cf.getHttpServerPort() + httpDownloaduri + PicNum.getText() + ",";
////				}
//			}
//		}
//		catch (DocumentException e){
//			log.info("getPicID DocumentException: " + e.getMessage());
//		}catch (Exception e) {
//			log.warn("getPicID Exception " + e.getMessage());
//			e.printStackTrace();
//		}
		return ret;
	}
	
	public final static Map<String, String> carLogo = new HashMap<String, String>() {
        {
           put("1", "宝马");
           put("2", "现代");
           put("3", "奔驰");
           put("4", "本田");
           put("5", "大众");
           put("6", "马自达");
           put("7", "丰田");
           put("8", "别克");
           put("9", "雪弗兰");
           put("10", "雪铁龙");
           put("11", "标志");
           put("12", "福特");
           put("13", "凌志"); 
           put("14", "尼桑");
           put("15", "奇瑞");
           put("16", "比亚迪");
           put("17", "起亚");
           put("18", "荣威");
           put("19", "三菱");
           put("20", "斯柯达");
           put("21", "铃木");
           put("22", "昌河");
           put("23", "菲亚特");
           put("24", "沃尔沃");
           put("25", "吉普");
           put("26", "路虎");
           put("27", "通用");
           put("28", "红旗");
           put("29", "悍马");
           put("30", "金杯");
           put("31", "江淮");
           put("32", "江铃");
           put("33", "吉利");
           put("34", "陆风");
           put("35", "力帆");
           put("36", "名爵");
           put("37", "讴歌");
           put("38", "英菲尼迪");
           put("39", "中华");
           put("40", "众泰");
           put("41", "威麟");
           put("42", "斯巴鲁");
           put("43", "世爵");
           put("44", "双环");
           put("45", "萨博");
           put("46", "威兹曼");
           put("47", "全球鹰");
           put("48", "吉奥");
           put("49", "华泰");
           put("50", "华普");
           put("51", "哈飞");
           put("52", "东南");
           put("53", "帝豪");
           put("54", "长安");
           put("55", "长丰");
           put("56", "长城");
           put("57", "大宇");
           put("58", "五十铃");
           put("59", "大发");
           put("60", "捷豹");
           put("61", "欧宝");
           put("62", "克莱斯勒");
           put("63", "阿尔法-罗密欧");
           put("64", "林肯");
           put("65", "劳斯莱斯");
           put("66", "法拉利");
           put("67", "保时捷");
           put("68", "莲花");
           put("69", "阿斯顿-马丁");
           put("70", "皇冠");
           put("71", "奔腾");
           put("72", "东风");
           put("73", "中顺");
           put("74", "北汽福田");
           put("75", "五菱");
           put("76", "凯迪拉克");
           put("77", "玛莎拉蒂");
           put("78", "夏利");
           put("79", "雷诺");
           put("80", "依维柯");
           put("81", "宾利");
           put("82", "迷你");
           put("83", "双龙");
           put("84", "汇众");
           put("85", "一汽");
           put("86", "开瑞");
           put("87", "南汽");
           put("88", "跃进");
           put("89", "解放");
           put("90", "金龙");
           put("91", "罗福");
           put("92", "中兴");
           put("93", "瑞麟");
           put("94", "曙光");
           put("95", "北汽");
           put("1000", "其他");
           put("0", "奥迪");
           put("?", "未知");
           put("?", "未知");
        }
     };  
}
