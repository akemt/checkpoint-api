package com.huawei.checkpoint.data;

import java.util.List;

public class YuShiMotor {
	/** 卡口相机编号 -- 卡口相机编码。不可空。由省公安厅统一编码 */
	private String CamID;
	/** 设备编号 -- 设备编码。 可空。 */
	private String DevID;
	/**
	 * 采集类型 -- 可选字段。描述设备的采集类型。
	 * 参考值：01-电警，02-公路卡口，03-固定测速，04-视频监控，05-移动电子警察，06-行车记录仪，09-其他电子监控设备
	 */
	private String EquipmentType;
	/** 全景标志 -- 可选字段。全景标志。 参考值：00- 全景相机，01-特写相机默认01 */
	private String PanoramaFlag;
	/** 记录ID号 -- 由1开始自动增长。 */
	private String RecordID;
	/** 卡口编号 -- 产生该信息的卡口代码。 (需要与宇视平台一致) */
	private String TollgateID;
	/** 卡口名称 -- 可选字段。 */
	private String TollgateName;
	/** 经过时刻 -- YYYYMMDDHHMMSSMMM,时间按24小时制。 */
	private String PassTime;
	private String PassTimeMill;
	/**
	 * 地点编号 -- 移动测速设备在多个地点工作，对应的违章数据地点也会有多个。 移动测速设备默认采用PlaceCode标识设备测速点，
	 * 当PlaceCode没有值， 则使用的卡口信息来标识设备测速点。
	 */
	private String PlaceCode;
	/** 地点名称 -- */
	private String PlaceName;
	/** 车道编号 --从1开始。车辆行驶方向最左车道为1，由左向右顺序编号。(需要与宇视平台一致) */ //
	private String LaneID;
	/** 车道类型 -- 0-机动车道，1-非机动车道 */
	private String LaneType;
	/** 方向编号 -- 1-东向西，2-西向东，3-南向北，4-北向南，5-东南向西北，6-西北向东南，7-东北向西南，8-西南向东北 */
	private String Direction;
	/** 方向名称 -- 可选字段。 */
	private String DirectionName;
	/** 号牌号码 -- 不能自动识别的用“-”表示 */
	private String CarPlate;
	/** 号牌置信度 -- 号牌置信度，数值越大，可信度越高。取值范围0-100。 */
	private String PlateConfidence;
	/** 号牌种类 -- 按GA24.7编码。 */
	private String PlateType;
	/** 号牌颜色 -- 0-白色1-黄色 2-蓝色 3-黑色 4-其他 5—绿色。 */
	private String PlateColor;
	/** 号牌数量 -- */
	private String PlateNumber;
	/**
	 * 号牌一致 --
	 * 0—车头和车尾号牌号码不一致，1—车头和车尾号牌号码完全一致，2—车头号牌号码无法自动识别，3—车尾号牌号码无法自动识别，4—车头和车尾号牌号码均无法自动识别。
	 */
	private String PlateCoincide;
	/**
	 * 尾部号牌号码 --
	 * 被查控车辆车尾号牌号码，允许车辆尾部号牌号码不全。不能自动识别的用“-”表示。扩展字段。在支持前后车牌的情况下使用。在单车牌情况下，该字段不填写，车牌信息填写在前面的字段。
	 */
	private String RearVehiclePlateID;
	/** 尾部号牌置信度 -- 号牌置信度，数值越大，可信度越高。取值范围0-100。 */
	private String RearPlateConfidence;
	/** 尾部号牌颜色 -- 0—白色 1—黄色 2—蓝色 3—黑色 4—其它颜色 5—绿色。扩展字段。 */
	private String RearPlateColor;
	/** 尾部号牌种类 -- 按GA24.7编码。 */
	private String RearPlateType;
	/** 图像数量 -- 采集的图像数量。 */
	private String PicNumber;
	/** 车辆速度 -- 单位km/h，0—无测速功能。 */
	private String VehicleSpeed;
	/** 执法限速 -- 车辆执法限速。单位km/h */
	private String LimitedSpeed;
	/** 标识限速 -- 驾驶员可以看到的限速。推荐使用本限速值， 可减少争议。单位km/h */
	private String MarkedSpeed;
	/**
	 * 行驶状态 --
	 * 0—正常，1—嫌疑，按GA408.1编码，1301-逆行，1302-不按交通信号灯通行，4602—在高速公路上逆行的，1603—机动车行驶超过规定时速50%的，等等。
	 */
	private String DriveStatus;
	/** 车辆品牌 -- 车辆厂牌编码（自行编码）。 */
	private String VehicleBrand;
	/** 车辆外型 -- 车辆外形编码（自行编码）。 */
	private String VehicleBody;
	/** 车辆类型 -- 0-未知，1-小型车，2-中型车，3-大型车，4-其他 */
	private String VehicleType;
	/** 车外廓长 -- 以厘米为单位 */
	private String VehicleLength;
	/** 车身颜色 -- A：白，B：灰，C：黄，D：粉，E：红，F：紫，G：绿，H：蓝，I：棕，J：黑，K：橙，L：青，M：银，N：银白，Z：其他 */
	private String VehicleColor;
	/** 车身颜色深浅 -- 0-未知，1-浅，2-深 */
	private String VehicleColorDept;
	/** 行人衣着颜色 -- 定义见说明 */
	private String DressColor;
	/** 红灯开始时间 -- YYYYMMDDHHMMSSMMM，时间按24小时制。第一组MM表示月，第二组MM表示分，第三组MMM表示毫秒 */
	private String RedLightStartTime;
	private String RedLightStartTimeMill;
	/** 红灯结束时间 -- YYYYMMDDHHMMSSMMM，时间按24小时制。第一组MM表示月，第二组MM表示分，第三组MMM表示毫秒 */
	private String RedLightEndTime;
	private String RedLightEndTimeMill;
	/** 红灯时间 -- 亮红灯的总时间，单位为MS，毫秒。注：有些特殊情况， 可能是半天时间都是红灯。 */
	private String RedLightTime;
	/** 处理标记 -- 0—未处理，1—已处理。 0—初始状态未校对，1—已校对和保存，2—无效信息，3—已处理和保存。 */
	private String DealTag;
	/** 识别状态 -- 0－识别成功 1－不成功 2－不完整 3-未识别 */
	private String IdentifyStatus;
	/** 识别时间 -- 单位毫秒 */
	private String IdentifyTime;
	/** 应用类型 -- 0－车辆卡口 1－ 电警 2－ 人员卡口 3－ 泛卡口 */
	private String ApplicationType;
	/**
	 * 全局合成标志 -- 0－不需要合成 1－自适应（图片数量2张及以上） 2－第1张提取特写（图片数量2～4张），合成后第1张原图丢弃
	 * 3－第1张提取特写（图片数量1～3张），合成后第1张原图不丢弃
	 */
	private String GlobalComposeFlag;
	/**
	 * 关联录像地址 -- 关联录像的完整路径。例如：http://10.10.10.10/video/1.mp4
	 * ftp://user:passwd@10.10.10.10/video/1.mp4 注意：录像的完整路径不支持中文。
	 */
	private String VideoURL;
	/**
	 * 关联录像地址2 -- 关联录像的完整路径。例如：http://10.10.10.10/video/1.mp4
	 * ftp://user:passwd@10.10.10.10/video/1.mp4 注意：录像的完整路径不支持中文。
	 */
	private String VideoURL2;
	/** 图像List */
	private List<YuShiImagesMotor> Image;

	// /**车道行驶方向 */
	// private String LaneDirection;
	// /**车道描述 */
	// private String LaneDescription;
	// /**字符置信度 */
	// private String CharConfidence;
	// /** 尾部字符置信度 */
	// private String RearCharConfidence;
	// /**抓拍触发类型 */
	// private String TriggerType;
	// /** 模拟标志 */
	// private String SimulateFlag;
	// /**车辆重量 */
	// private String VehicleWeight;
	// /**车辆类型 2 */
	// private String VehicleType2;
	// /**抓拍对象类型 */
	// private String TargetType;
	// /**车身颜色 RGB */
	// private String VehicleColorRGB;
	// /**行人衣着颜色 RGB */
	// private String DressColorRGB;
	// /**车辆坐标（左上角 x） */
	// private String VehicleTopX;
	// /** */
	// private YuShiVehicleFaceMotor vehicleFace;

	private String DomCode; //

	private String NVRCode; //

	private String AutoID;// 主键ID

	public String getDomCode() {
		return isNotNull(DomCode);
	}

	public void setDomCode(String domCode) {
		DomCode = domCode;
	}

	public String getNVRCode() {
		return isNotNull(NVRCode);
	}

	public void setNVRCode(String nVRCode) {
		NVRCode = nVRCode;
	}

	public String getAutoID() {
		return isNotNull(AutoID);
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
	}

	public String getCamID() {
		return isNotNull(CamID);
	}

	public String getPassTimeMill() {
		return isNotNull(PassTimeMill);
	}

	public void setPassTimeMill(String passTimeMill) {
		PassTimeMill = passTimeMill;
	}

	public String getRedLightStartTimeMill() {
		return isNotNull(RedLightStartTimeMill);
	}

	public void setRedLightStartTimeMill(String redLightStartTimeMill) {
		RedLightStartTimeMill = redLightStartTimeMill;
	}

	public String getRedLightEndTimeMill() {
		return isNotNull(RedLightEndTimeMill);
	}

	public void setRedLightEndTimeMill(String redLightEndTimeMill) {
		RedLightEndTimeMill = redLightEndTimeMill;
	}

	public void setCamID(String camID) {
		CamID = camID;
	}

	public String getDevID() {
		return isNotNull(DevID);
	}

	public void setDevID(String devID) {
		DevID = devID;
	}

	public String getEquipmentType() {
		return isNotNull(EquipmentType);
	}

	public void setEquipmentType(String equipmentType) {
		EquipmentType = equipmentType;
	}

	public String getPanoramaFlag() {
		return isNotNull(PanoramaFlag);
	}

	public void setPanoramaFlag(String panoramaFlag) {
		PanoramaFlag = panoramaFlag;
	}

	public String getRecordID() {
		return isNotNull(RecordID);
	}

	public void setRecordID(String recordID) {
		RecordID = recordID;
	}

	public String getTollgateID() {
		return isNotNull(TollgateID);
	}

	public void setTollgateID(String tollgateID) {
		TollgateID = tollgateID;
	}

	public String getTollgateName() {
		return isNotNull(TollgateName);
	}

	public void setTollgateName(String tollgateName) {
		TollgateName = tollgateName;
	}

	public String getPassTime() {
		return isNotNull(PassTime);
	}

	public void setPassTime(String passTime) {
		PassTime = passTime;
	}

	public String getPlaceCode() {
		return isNotNull(PlaceCode);
	}

	public void setPlaceCode(String placeCode) {
		PlaceCode = placeCode;
	}

	public String getPlaceName() {
		return isNotNull(PlaceName);
	}

	public void setPlaceName(String placeName) {
		PlaceName = placeName;
	}

	public String getLaneID() {
		return isNotNull(LaneID);
	}

	public void setLaneID(String laneID) {
		LaneID = laneID;
	}

	public String getLaneType() {
		return isNotNull(LaneType);
	}

	public void setLaneType(String laneType) {
		LaneType = laneType;
	}

	public String getDirection() {
		return isNotNull(Direction);
	}

	public void setDirection(String direction) {
		Direction = direction;
	}

	public String getDirectionName() {
		return isNotNull(DirectionName);
	}

	public void setDirectionName(String directionName) {
		DirectionName = directionName;
	}

	public String getCarPlate() {
		return isNotNull(CarPlate);
	}

	public void setCarPlate(String carPlate) {
		CarPlate = carPlate;
	}

	public String getPlateConfidence() {
		return isNotNull(PlateConfidence);
	}

	public void setPlateConfidence(String plateConfidence) {
		PlateConfidence = plateConfidence;
	}

	public String getPlateType() {
		return isNotNull(PlateType);
	}

	public void setPlateType(String plateType) {
		PlateType = plateType;
	}

	public String getPlateColor() {
		return isNotNull(PlateColor);
	}

	public void setPlateColor(String plateColor) {
		PlateColor = plateColor;
	}

	public String getPlateNumber() {
		return isNotNull(PlateNumber);
	}

	public void setPlateNumber(String plateNumber) {
		PlateNumber = plateNumber;
	}

	public String getPlateCoincide() {
		return isNotNull(PlateCoincide);
	}

	public void setPlateCoincide(String plateCoincide) {
		PlateCoincide = plateCoincide;
	}

	public String getRearVehiclePlateID() {
		return isNotNull(RearVehiclePlateID);
	}

	public void setRearVehiclePlateID(String rearVehiclePlateID) {
		RearVehiclePlateID = rearVehiclePlateID;
	}

	public String getRearPlateConfidence() {
		return isNotNull(RearPlateConfidence);
	}

	public void setRearPlateConfidence(String rearPlateConfidence) {
		RearPlateConfidence = rearPlateConfidence;
	}

	public String getRearPlateColor() {
		return isNotNull(RearPlateColor);
	}

	public void setRearPlateColor(String rearPlateColor) {
		RearPlateColor = rearPlateColor;
	}

	public String getRearPlateType() {
		return isNotNull(RearPlateType);
	}

	public void setRearPlateType(String rearPlateType) {
		RearPlateType = rearPlateType;
	}

	public String getPicNumber() {
		return isNotNull(PicNumber);
	}

	public void setPicNumber(String picNumber) {
		PicNumber = picNumber;
	}

	public String getVehicleSpeed() {
		return isNotNull(VehicleSpeed);
	}

	public void setVehicleSpeed(String vehicleSpeed) {
		VehicleSpeed = vehicleSpeed;
	}

	public String getLimitedSpeed() {
		return isNotNull(LimitedSpeed);
	}

	public void setLimitedSpeed(String limitedSpeed) {
		LimitedSpeed = limitedSpeed;
	}

	public String getMarkedSpeed() {
		return isNotNull(MarkedSpeed);
	}

	public void setMarkedSpeed(String markedSpeed) {
		MarkedSpeed = markedSpeed;
	}

	public String getDriveStatus() {
		return isNotNull(DriveStatus);
	}

	public void setDriveStatus(String driveStatus) {
		DriveStatus = driveStatus;
	}

	public String getVehicleBrand() {
		return isNotNull(VehicleBrand);
	}

	public void setVehicleBrand(String vehicleBrand) {
		VehicleBrand = vehicleBrand;
	}

	public String getVehicleBody() {
		return isNotNull(VehicleBody);
	}

	public void setVehicleBody(String vehicleBody) {
		VehicleBody = vehicleBody;
	}

	public String getVehicleType() {
		return isNotNull(VehicleType);
	}

	public void setVehicleType(String vehicleType) {
		VehicleType = vehicleType;
	}

	public String getVehicleLength() {
		return isNotNull(VehicleLength);
	}

	public void setVehicleLength(String vehicleLength) {
		VehicleLength = vehicleLength;
	}

	public String getVehicleColor() {
		return isNotNull(VehicleColor);
	}

	public void setVehicleColor(String vehicleColor) {
		VehicleColor = vehicleColor;
	}

	public String getVehicleColorDept() {
		return isNotNull(VehicleColorDept);
	}

	public void setVehicleColorDept(String vehicleColorDept) {
		VehicleColorDept = vehicleColorDept;
	}

	public String getDressColor() {
		return isNotNull(DressColor);
	}

	public void setDressColor(String dressColor) {
		DressColor = dressColor;
	}

	public String getRedLightStartTime() {
		return isNotNull(RedLightStartTime);
	}

	public void setRedLightStartTime(String redLightStartTime) {
		RedLightStartTime = redLightStartTime;
	}

	public String getRedLightEndTime() {
		return isNotNull(RedLightEndTime);
	}

	public void setRedLightEndTime(String redLightEndTime) {
		RedLightEndTime = redLightEndTime;
	}

	public String getRedLightTime() {
		return isNotNull(RedLightTime);
	}

	public void setRedLightTime(String redLightTime) {
		RedLightTime = redLightTime;
	}

	public String getDealTag() {
		return isNotNull(DealTag);
	}

	public void setDealTag(String dealTag) {
		DealTag = dealTag;
	}

	public String getIdentifyStatus() {
		return isNotNull(IdentifyStatus);
	}

	public void setIdentifyStatus(String identifyStatus) {
		IdentifyStatus = identifyStatus;
	}

	public String getIdentifyTime() {
		return isNotNull(IdentifyTime);
	}

	public void setIdentifyTime(String identifyTime) {
		IdentifyTime = identifyTime;
	}

	public String getApplicationType() {
		return isNotNull(ApplicationType);
	}

	public void setApplicationType(String applicationType) {
		ApplicationType = applicationType;
	}

	public String getGlobalComposeFlag() {
		return isNotNull(GlobalComposeFlag);
	}

	public void setGlobalComposeFlag(String globalComposeFlag) {
		GlobalComposeFlag = globalComposeFlag;
	}

	public String getVideoURL() {
		return isNotNull(VideoURL);
	}

	public void setVideoURL(String videoURL) {
		VideoURL = videoURL;
	}

	public String getVideoURL2() {
		return isNotNull(VideoURL2);
	}

	public void setVideoURL2(String videoURL2) {
		VideoURL2 = videoURL2;
	}

	public List<YuShiImagesMotor> getImage() {
		return Image;
	}

	public void setImage(List<YuShiImagesMotor> image) {
		Image = image;
	}

	public String isNotNull(String data) {
		return data == null ? "" : data;
	}
}
