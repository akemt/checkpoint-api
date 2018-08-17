package com.huawei.checkpoint.data;

/**
 * VCM共通实体类
 * 
 */
public class VCMMotor {
	/**
	 * 主键ID.UUID自动生成
	 */
	private String AutoID;
	/**
	 * 应用类型 0：车辆卡口 1：电警 2：人员卡口 3：泛卡口
	 */
	private String AppType;
	/**
	 * 信息来源 0：洛阳 1：宇视 3：其他
	 */
	private String SourceType;
	/**
	 * 系统编号，中心统一分配;洛阳：自带；宇视：网关填充v
	 * 
	 * A：市局卡口系统 B：交警支队电警、卡口系统 C：交警高速大队电警、卡口系统 D：偃师市电警、卡口系统 E：孟津县电警、卡口系统
	 * F：新安县电警、卡口系统 G：伊川县电警、卡口系统 H：汝阳县电警、卡口系统 I：嵩 县电警、卡口系统 J：栾川县电警、卡口系统
	 * K：宜阳县电警、卡口系统 L：洛宁县电警、卡口系统 L：吉利区电警、卡口系统
	 */
	private String XTBH;
	/**
	 * 设备编号 网关提供、用户不可改 ; SourceType=0-洛阳：设备编码DeviceNo 不满位补零 ;
	 * SourceType=1-宇视：CamID。
	 */
	private String DeviceID;
	/**
	 * 经过时间 YYYYMMDDHHMMSSMMM,时间按24小时制。
	 */
	private String WatchTime;
	/**
	 * 经过时间毫秒
	 */
	private String WatchTimeMill;
	/**
	 * 上传时间 YYYYMMDDHHMMSS,时间按24小时制。
	 */
	private String UploadTime;
	/**
	 * 设备监测地点
	 */
	private String DeviceDesc;
	/**
	 * 域编码
	 */
	private String DomCode;
	/**
	 * NVR编码
	 */
	private String NVRCode;

	/**
	 * 车辆外形
	 */
	private String VehicleBody; // 置信度，车牌识别的准确度，范围在0—1之间，如果无计算则为“?”
	/**
	 * 车辆类型 0：未识别 1：大车 2：小车 3：中型车 4：其他
	 */
	private String CarType; // 行驶路段方向编码，如果不能识别填写‘00’
	/**
	 * 车外廓长
	 */
	private String VehicleLength; // 车道标识符
	/**
	 * 车身颜色 宇视协议：
	 * A：白，B：灰，C：黄，D：粉，E：红，F：紫，G：绿，H：蓝，I：棕，J：黑，K：橙，L：青，M：银，N：银白，Y:未知，Z：其他
	 * （洛阳协议不存在该字段 默认Y）
	 */
	private String VehicleColor;
	/**
	 * 车身颜色深浅 宇视协议：0-未知，1-浅，2-深 （洛阳协议不存在该字段 默认0）
	 */
	private String VehicleColorDept;
	/**
	 * 车辆速度
	 */
	private String CarSpeed;
	/**
	 * 限速
	 */
	private String LimitedSpeed;
	/**
	 * 号牌号码 未识别的用‘000000’填写
	 */
	private String CarNo;
	/**
	 * 号牌置信度 取值范围0-1
	 */
	private String Confidence;
	/**
	 * 号牌种类
	 */
	private String PlateType;
	/**
	 * 号牌颜色 0：未识别 1：蓝色 2：黄色 3：白色 4：黑色 5：绿色 6：红色 7：紫色 8：粉色 9：桔色 10：其他
	 */
	private String PlateColor;
	/**
	 * 车辆品牌
	 */
	private String CarLogo;
	/**
	 * 行驶状态 宇视协议： 0—正常，1—嫌疑， 按GA408.1编码，
	 * 1301-逆行，1302-不按交通信号灯通行，4602—在高速公路上逆行的，1603—机动车行驶超过规定时速50%的，等等。
	 * （洛阳协议不存在该字段 默认0）
	 */
	private String DriveStatus;
	/**
	 * 处理标记 宇视协议： 0—未处理，1—已处理。 0—初始状态未校对，1—已校对和保存，2—无效信息，3—已处理和保存。
	 * （洛阳协议不存在该字段默认1）
	 */
	private String DealTag;
	/**
	 * 车道编号 从1开始。车辆行驶方向最左车道为1，由左向右顺序编号。
	 */
	private String CarRoad;
	/**
	 * 方向编号 01：由东向西 02：由西向东 03：由南向北 04：由北向南 05：由东南到西北 06：由西北到东南 07：由东北到西南
	 * 08：由西南到东北
	 */
	private String Direction;
	/**
	 * 图片数量
	 */
	private String PicNum;

	public String getAutoID() {
		return isNotNull(AutoID);
	}

	public String getAppType() {
		return isNotNull(AppType);
	}

	public String getSourceType() {
		return isNotNull(SourceType);
	}

	public String getXTBH() {
		return isNotNull(XTBH);
	}

	public String getDeviceID() {
		return isNotNull(DeviceID);
	}

	public String getWatchTime() {
		return isNotNull(WatchTime);
	}

	public String getWatchTimeMill() {
		return isNotNull(WatchTimeMill);
	}

	public String getUploadTime() {
		return isNotNull(UploadTime);
	}

	public String getDeviceDesc() {
		return isNotNull(DeviceDesc);
	}

	public String getDomCode() {
		return isNotNull(DomCode);
	}

	public String getNVRCode() {
		return isNotNull(NVRCode);
	}

	public String getVehicleBody() {
		return isNotNull(VehicleBody);
	}

	public String getCarType() {
		return isNotNull(CarType);
	}

	public String getVehicleLength() {
		return isNotNullZero(VehicleLength);
	}

	public String getVehicleColor() {
		return isNotNull(VehicleColor);
	}

	public String getVehicleColorDept() {
		return isNotNull(VehicleColorDept);
	}

	public String getCarSpeed() {
		return isNotNull(CarSpeed);
	}

	public String getLimitedSpeed() {
		return isNotNull(LimitedSpeed);
	}

	public String getCarNo() {
		return isNotNull(CarNo);
	}

	public String getConfidence() {
		return isNotNull(Confidence);
	}

	public String getPlateType() {
		return isNotNull(PlateType);
	}

	public String getPlateColor() {
		return isNotNull(PlateColor);
	}

	public String getCarLogo() {
		return isNotNull(CarLogo);
	}

	public String getDriveStatus() {
		return isNotNull(DriveStatus);
	}

	public String getDealTag() {
		return isNotNull(DealTag);
	}

	public String getCarRoad() {
		return isNotNull(CarRoad);
	}

	public String getDirection() {
		return isNotNull(Direction);
	}

	public String getPicNum() {
		return isNotNullZero(PicNum);
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
	}

	public void setAppType(String appType) {
		AppType = appType;
	}

	public void setSourceType(String sourceType) {
		SourceType = sourceType;
	}

	public void setXTBH(String xTBH) {
		XTBH = xTBH;
	}

	public void setDeviceID(String devID) {
		DeviceID = devID;
	}

	public void setWatchTime(String watchTime) {
		WatchTime = watchTime;
	}

	public void setWatchTimeMill(String watchTimeMill) {
		WatchTimeMill = watchTimeMill;
	}

	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}

	public void setDeviceDesc(String deviceDesc) {
		DeviceDesc = deviceDesc;
	}

	public void setDomCode(String domCode) {
		DomCode = domCode;
	}

	public void setNVRCode(String nVRCode) {
		NVRCode = nVRCode;
	}

	public void setVehicleBody(String vehicleBody) {
		VehicleBody = vehicleBody;
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public void setVehicleLength(String vehicleLength) {
		VehicleLength = vehicleLength;
	}

	public void setVehicleColor(String vehicleColor) {
		VehicleColor = vehicleColor;
	}

	public void setVehicleColorDept(String vehicleColorDept) {
		VehicleColorDept = vehicleColorDept;
	}

	public void setCarSpeed(String carSpeed) {
		CarSpeed = carSpeed;
	}

	public void setLimitedSpeed(String limitedSpeed) {
		LimitedSpeed = limitedSpeed;
	}

	public void setCarNo(String carNo) {
		CarNo = carNo;
	}

	public void setConfidence(String confidence) {
		Confidence = confidence;
	}

	public void setPlateType(String plateType) {
		PlateType = plateType;
	}

	public void setPlateColor(String plateColor) {
		PlateColor = plateColor;
	}

	public void setCarLogo(String carLogo) {
		CarLogo = carLogo;
	}

	public void setDriveStatus(String driveStatus) {
		DriveStatus = driveStatus;
	}

	public void setDealTag(String dealTag) {
		DealTag = dealTag;
	}

	public void setCarRoad(String carRoad) {
		CarRoad = carRoad;
	}

	public void setDirection(String direction) {
		Direction = direction;
	}

	public void setPicNum(String picNum) {
		PicNum = picNum;
	}

	public String isNotNull(String data) {
		return data == null ? "" : data;
	}

	public String isNotNullZero(String data) {
		return data == null || "".equals(data) ? "0" : data;
	}
}
