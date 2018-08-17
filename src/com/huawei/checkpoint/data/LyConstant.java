package com.huawei.checkpoint.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.VCMAccess;

/**
 * 洛阳实体值域转换共通类
 * 
 * @author Xialf
 *
 */
public class LyConstant {

	/** 默认值 - 0 */
	private static final String zero = "0";
	/** 默认值 - 1 */
	private static final String one = "1";

	/** 默认值 - 99 */
	private static final String isNull = "99";

	/**
	 * 洛阳实体值域转换成共通的值域
	 * 
	 * @param vcmMotor
	 * @return
	 */
	public static VCMMotor lyToCommon(VCMMotor vcmMotor) {
		vcmMotor.setAppType(appType.get(vcmMotor.getAppType()) == null ? isNull : appType.get(vcmMotor.getAppType()));
		vcmMotor.setSourceType(String.valueOf(CheckPointStaticUtils.LUO_YANG));
//		vcmMotor.setDeviceID(addZeroForNum(vcmMotor.getDeviceID(), 32));

		vcmMotor.setUploadTime(getNewData());

		vcmMotor.setVehicleLength(zero);
		vcmMotor.setVehicleColorDept(zero);

		vcmMotor.setDriveStatus(zero);
		vcmMotor.setDealTag(one);
		
		vcmMotor.setVehicleBody(zero);
		
		vcmMotor.setPlateType(zero);

		String strConfidence  = CheckPointStaticUtils.transformRange(vcmMotor.getConfidence(), zero);
		vcmMotor.setConfidence("".equals(strConfidence) ? zero : strConfidence);

		vcmMotor.setWatchTimeMill(VCMAccess.setTimeMill(vcmMotor.getWatchTime()));
		vcmMotor.setWatchTime(VCMAccess.setTime(vcmMotor.getWatchTime()));
		
		vcmMotor.setCarSpeed("".equals(vcmMotor.getCarSpeed()) ? zero : CheckPointStaticUtils.strSubString(CheckPointStaticUtils.transformRange(vcmMotor.getCarSpeed(), zero)));
		
		vcmMotor.setLimitedSpeed("".equals(vcmMotor.getLimitedSpeed()) ? zero : CheckPointStaticUtils.strSubString(CheckPointStaticUtils.transformRange(vcmMotor.getLimitedSpeed(), zero)));
		
		vcmMotor.setPicNum("".equals(vcmMotor.getPicNum()) ? zero : vcmMotor.getPicNum());
		

		vcmMotor.setDirection(direction.get(vcmMotor.getDirection()) == null ? "00" : direction.get(vcmMotor.getDirection()));

		return vcmMotor;

	}

	/**
	 * 应用类型 洛阳协议：X：行人，Y：非机动车，Z：过车监测数据 转换后自定义： 0：车辆卡口 1：电警 2：人员卡口 3：泛卡口 4：非机动车
	 */
	public final static Map<String, String> appType = new HashMap<String, String>() {
		{
			put("X", "2");
			put("Y", "4");
			put("Z", "0");
		}
	};
	
	/**
	 * 方向编号
	 */
	public final static Map<String, String> direction = new HashMap<String, String>() {
		{
			put("01", "1A");
			put("02", "1B");
			put("03", "2A");
			put("04", "2B");
			put("1A", "1A");
			put("1B", "1B");
			put("2A", "2A");
			put("2B", "2B");
		}
	};

	/**
	 * 设备编号 : 设备编码DeviceNo.不满位补零(前)
	 * 
	 * @param str
	 * @param strLength
	 * @return
	 */
	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				// sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	/**
	 * 取当前系统时间
	 * 
	 * @return
	 */
	public static String getNewData() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());
	} 

}
