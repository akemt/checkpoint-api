package com.huawei.checkpoint.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.VCMAccess;

/**
 * 宇视实体值域转换共通类
 * 
 * @author Xialf
 *
 */
public class YsConstant {

	/** 默认值 - 0 */
	private static final String zero = "0";
	/** 默认值 - 1 */
	private static final String one = "1";

	/** 默认值 未识别- 99 */
	private static final String isNull = "99";

	/** 默认值 - 99 */
	private static final String SIX_ZERO = "000000";

	/**
	 * 洛阳实体值域转换成共通的值域
	 * 
	 * @param vcmMotor
	 * @return
	 */
	public static VCMMotor ysToCommon(VCMMotor vcmMotor) {

		vcmMotor.setXTBH("-1");

		vcmMotor.setAppType(vcmMotor.getAppType().equals("") ? isNull : CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getAppType()));
		vcmMotor.setSourceType(String.valueOf(CheckPointStaticUtils.YU_SHI));

		// 过车时间
		vcmMotor.setWatchTimeMill(VCMAccess.setYsTimeMill(vcmMotor.getWatchTime()));
		vcmMotor.setWatchTime(VCMAccess.setYsTime(vcmMotor.getWatchTime()));
		// 上传时间
		vcmMotor.setUploadTime(getNewData());

		// 车辆外形
		// vcmMotor.setVehicleBody(zero);
		// 车辆类型
		vcmMotor.setCarType(carType.get(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getCarType())));
		
		//车外廓长
		vcmMotor.setVehicleLength(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getVehicleLength()));
		// 车身颜色
		vcmMotor.setVehicleColor(vehicleColor.get(vcmMotor.getVehicleColor()));
		
		//车身颜色深浅
		vcmMotor.setVehicleColorDept(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getVehicleColorDept()));
		// 车速
		vcmMotor.setCarSpeed("".equals(vcmMotor.getCarSpeed()) ? zero
				: CheckPointStaticUtils
						.strSubString(CheckPointStaticUtils.transformRange(vcmMotor.getCarSpeed(), zero)));
		// 限速
		vcmMotor.setLimitedSpeed("".equals(vcmMotor.getLimitedSpeed()) ? zero
				: CheckPointStaticUtils
						.strSubString(CheckPointStaticUtils.transformRange(vcmMotor.getLimitedSpeed(), zero)));
		// 号牌
		vcmMotor.setCarNo(CheckPointStaticUtils.NoRecognize(vcmMotor.getCarNo(), SIX_ZERO));

		// 置信度-特殊字符？ 转换成 0
		String strConfidence = CheckPointStaticUtils.transformRange(vcmMotor.getConfidence(), zero);
		// 置信度-宇视需要除以100
		vcmMotor.setConfidence(CheckPointStaticUtils.calculation(strConfidence));
		// 号牌颜色
		vcmMotor.setPlateColor("".equals(vcmMotor.getPlateColor()) ? zero : plateColor.get(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getPlateColor())));
		// 车辆品牌
//		vcmMotor.setCarLogo(isNull);
		// 行驶状态
		// vcmMotor.setDriveStatus(zero);
		// 处理标记
		 vcmMotor.setDealTag(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getDealTag()));
		 
		 vcmMotor.setCarRoad(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getCarRoad()));
		// 方向编号
		vcmMotor.setDirection(direction.get(CheckPointStaticUtils.dropStrBeforeZero(vcmMotor.getDirection())));

		return vcmMotor;

	}

	/**
	 * 车辆类型 :宇视协议：0-未知，1-小型车，2-中型车，3-大型车，4-其他 共通类型：0：未识别，1：大车，2：小车，3：中型车，4：其他
	 */
	public final static Map<String, String> carType = new HashMap<String, String>() {
		{
			put("0", "0");
			put("1", "2");
			put("2", "3");
			put("3", "1");
			put("4", "4");
		}
	};

	/**
	 * 车身颜色
	 * :宇视协议：A：白，B：灰，C：黄，D：粉，E：红，F：紫，G：绿，H：蓝，I：棕，J：黑，K：橙，L：青，M：银，N：银白，Y:未知，Z：其他
	 * 共通类型：1 白色 ,2 银色 ,3 黑色,4 红色,5 紫色,6 蓝色 ,7 黄色,8 绿色,9 褐色 ,10 粉红色 ,11 灰色 ,12
	 * 其它颜色
	 */
	public final static Map<String, String> vehicleColor = new HashMap<String, String>() {
		{
			put("A", "1");
			put("B", "11");
			put("C", "7");
			put("D", "10");
			put("E", "4");
			put("F", "5");
			put("G", "8");
			put("H", "6");
			put("I", "12");
			put("J", "3");
			put("K", "12");
			put("L", "12");
			put("M", "3");
			put("N", "12");
			put("Y", "12");
			put("Z", "12");
		}
	};

	/**
	 * 号牌颜色 :宇视协议：0-白色 1-黄色 2-蓝色 3-黑色 4-其他 5—绿色。
	 * 共通类型：0：未识别、1：蓝色、2：黄色、3：白色、4：黑色、5：绿色、6：红色、7：紫色、8：粉色、9：桔色、10：其他
	 */
	public final static Map<String, String> plateColor = new HashMap<String, String>() {
		{
			put("0", "3");
			put("1", "2");
			put("2", "1");
			put("3", "4");
			put("4", "10");
			put("5", "5");
		}
	};

	/**
	 * 方向编号
	 */
	public final static Map<String, String> direction = new HashMap<String, String>() {
		{
			put("1", "1A");
			put("2", "1B");
			put("3", "2A");
			put("4", "2B");
			put("1A", "1A");
			put("1B", "1B");
			put("2A", "2A");
			put("2B", "2B");
		}
	};

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
