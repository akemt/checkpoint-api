package com.huawei.checkpoint.data;

import java.util.ArrayList;

/**
 * 洛阳机动车相关监测数据实体类
 * 
 * @author Xialf
 *
 */
public class LuoyangMotor {

	// 数据包头部
	private String Version; // 协议版本信息，默认为1.0

	private String Record; // 文件包含的数据条数，本系统默认为1

	private String Desc; // 备注信息

	// 数据包体格式
	private String XTBH; // 接入系统编号，中心统一分配

	private String WorksNo; // 设备厂商编码，中心统一分配

	private String DeviceNo; // 接入本系统的设备唯一编号，每套设备一个编号中心统一分配

	private String Type; // 数据类型，用于区分本条数据为过车监测数据、行人监测数据还是非机动车监测数据。

	private String CarNo; // 车牌号，规则为牌照信息从上到下，从左右的顺序存储数据，未识别的用‘000000’填写

	private String PlateColor; // 颜色（车辆识别数据中为号牌颜色，行人识别数据中为衣着颜色，非机动车识别数据为车身颜色）

	private String CarType; // 车型，代表机动车类型

	private String CarLogo; // 车辆标志，暂保留

	private String Confidence; // 置信度，车牌识别的准确度，范围在0—1之间，如果无计算则为“?”

	private String Direction; // 行驶路段方向编码，如果不能识别填写‘00’

	private String CarRoad; // 车道标识符

	private String CarSpeed; // 速度，单位km/h 形如’120’样式

	private String LimitedSpeed; // 设备当前限速值，单位km/h 形如’120’样式

	private String UploadType; // 定义传输方式 1—带图片 2---不带图片

	private String WatchTime; // 抓拍时间，精确到毫秒，(时间的格式为 "yyyy-mm-dd HH:MM:SS.zzz"
								// 中间含一空格, 秒和毫秒之间由’.’分隔)

	private String WatchTime1; // 第一张图片的监测时间，精确到毫秒，格式同上

	private String WatchTime2; // 第二张图片的监测时间，精确到毫秒，格式同上

	private String WatchTime3; // 第三张图片的监测时间，精确到毫秒，格式同上

	private String WatchTime4; // 第四张图片的监测时间，精确到毫秒，格式同上

	private String WatchTimeMill; // 抓拍时间，毫秒

	private String WatchTime1Mill; // 第一张图片的监测时间，毫秒

	private String WatchTime2Mill; // 第二张图片的监测时间，毫秒

	private String WatchTime3Mill; // 第三张图片的监测时间，毫秒

	private String WatchTime4Mill; // 第四张图片的监测时间，毫秒

	private String DeviceDesc; // 设备监测地点描述，统一下发应和图片上的描述一置

	private String CheckValue; // 检验位，验证数据的有效性，暂保留

	private String PicNum; // 图片数量，默认为1张图片，可扩展为多张

	// private String PicName1; // 图片1名称
	//
	// private String Pic1; // 图片1内容，图片转为base64编码
	//
	// private String PicName2; // 图片2名称, 没有图片2则填 ?
	//
	// private String Pic2; // 图片2内容，图片转为base64编码，没有图片2则填 ?

	private ArrayList<byte[]> Pic;

	private ArrayList<String> PicName;

	private String DomCode; //

	private String NVRCode; //

	private String ID;// 主键ID

	private String CarColor;//车辆颜色 

	public String getCarColor() {
		return CarColor;
	}

	public void setCarColor(String carColor) {
		CarColor = carColor;
	}

	public ArrayList<byte[]> getPic() {
		return Pic;
	}

	public void setPic(ArrayList<byte[]> pic) {
		Pic = pic;
	}

	public ArrayList<String> getPicName() {
		return PicName;
	}

	public void setPicName(ArrayList<String> picName) {
		PicName = picName;
	}

	public String getWatchTimeMill() {
		return isNotNull(WatchTimeMill);
	}

	public void setWatchTimeMill(String watchTimeMill) {
		WatchTimeMill = watchTimeMill;
	}

	public String getWatchTime1Mill() {
		return isNotNull(WatchTime1Mill);
	}

	public void setWatchTime1Mill(String watchTime1Mill) {
		WatchTime1Mill = watchTime1Mill;
	}

	public String getWatchTime2Mill() {
		return isNotNull(WatchTime2Mill);
	}

	public void setWatchTime2Mill(String watchTime2Mill) {
		WatchTime2Mill = watchTime2Mill;
	}

	public String getWatchTime3Mill() {
		return isNotNull(WatchTime3Mill);
	}

	public void setWatchTime3Mill(String watchTime3Mill) {
		WatchTime3Mill = watchTime3Mill;
	}

	public String getWatchTime4Mill() {
		return isNotNull(WatchTime4Mill);
	}

	public void setWatchTime4Mill(String watchTime4Mill) {
		WatchTime4Mill = watchTime4Mill;
	}

	public String getDomCode() {
		return isNotNull(DomCode);
	}

	public void setDomCode(String DomCode) {
		this.DomCode = DomCode;
	}

	public String getNVRCode() {
		return isNotNull(NVRCode);
	}

	public void setNVRCode(String nVRCode) {
		NVRCode = nVRCode;
	}

	public String getID() {
		return isNotNull(ID);
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getVersion() {
		return isNotNull(Version);
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getDesc() {
		return isNotNull(Desc);
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getXTBH() {
		return isNotNull(XTBH);
	}

	public void setXTBH(String xTBH) {
		XTBH = xTBH;
	}

	public String getWorksNo() {
		return isNotNull(WorksNo);
	}

	public void setWorksNo(String worksNo) {
		WorksNo = worksNo;
	}

	public String getDeviceNo() {
		return isNotNull(DeviceNo);
	}

	public void setDeviceNo(String deviceNo) {
		DeviceNo = deviceNo;
	}

	public String getType() {
		return isNotNull(Type);
	}

	public void setType(String type) {
		Type = type;
	}

	public String getCarNo() {
		return isNotNull(CarNo);
	}

	public void setCarNo(String carNo) {
		CarNo = carNo;
	}

	public String getPlateColor() {
		return isNotNull(PlateColor);
	}

	public void setPlateColor(String plateColor) {
		PlateColor = plateColor;
	}

	public String getCarType() {
		return isNotNull(CarType);
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public String getCarLogo() {
		return isNotNull(CarLogo);
	}

	public void setCarLogo(String carLogo) {
		CarLogo = carLogo;
	}

	public String getDirection() {
		return isNotNull(Direction);
	}

	public void setDirection(String direction) {
		Direction = direction;
	}

	public String getUploadType() {
		return isNotNull(UploadType);
	}

	public void setUploadType(String uploadType) {
		UploadType = uploadType;
	}

	public String getWatchTime() {
		return isNotNull(WatchTime);
	}

	public void setWatchTime(String watchTime) {
		WatchTime = watchTime;
	}

	public String getWatchTime1() {
		return isNotNull(WatchTime1);
	}

	public void setWatchTime1(String watchTime1) {
		WatchTime1 = watchTime1;
	}

	public String getWatchTime2() {
		return isNotNull(WatchTime2);
	}

	public void setWatchTime2(String watchTime2) {
		WatchTime2 = watchTime2;
	}

	public String getWatchTime3() {
		return isNotNull(WatchTime3);
	}

	public void setWatchTime3(String watchTime3) {
		WatchTime3 = watchTime3;
	}

	public String getWatchTime4() {
		return isNotNull(WatchTime4);
	}

	public void setWatchTime4(String watchTime4) {
		WatchTime4 = watchTime4;
	}

	public String getDeviceDesc() {
		return isNotNull(DeviceDesc);
	}

	public void setDeviceDesc(String deviceDesc) {
		DeviceDesc = deviceDesc;
	}

	public String getRecord() {
		return isNotNull(Record);
	}

	public void setRecord(String record) {
		Record = record;
	}

	public String getConfidence() {
		return isNotNull(Confidence);
	}

	public void setConfidence(String confidence) {
		Confidence = confidence;
	}

	public String getCarRoad() {
		return isNotNull(CarRoad);
	}

	public void setCarRoad(String carRoad) {
		CarRoad = carRoad;
	}

	public String getCarSpeed() {
		return isNotNull(CarSpeed);
	}

	public void setCarSpeed(String carSpeed) {
		CarSpeed = carSpeed;
	}

	public String getLimitedSpeed() {
		return isNotNull(LimitedSpeed);
	}

	public void setLimitedSpeed(String limitedSpeed) {
		LimitedSpeed = limitedSpeed;
	}

	public String getCheckValue() {
		return isNotNull(CheckValue);
	}

	public void setCheckValue(String checkValue) {
		CheckValue = checkValue;
	}

	public String getPicNum() {
		return isNotNullZero(PicNum);
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
