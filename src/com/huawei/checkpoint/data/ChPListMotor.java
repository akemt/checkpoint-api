package com.huawei.checkpoint.data;

/**
 * 卡口LIST实体
 * 
 * @author Xialf
 *
 */
public class ChPListMotor {

	/** 主键ID */
	private String AutoID;
	/** 卡口编号 */
	private String ChPointID;
	/** 系统编号 */
	private String XTBH;
	/** 设备编码 */
	private String DeviceID;
	/** 设备名称 */
	private String DeviceName;
	/** 地点名称 */
	private String PlaceName;
	/** IP地址 */
	private String IP;
	/** 端口 */
	private String Port;
	/** 经度 */
	private String Longitude;
	/** 纬度 */
	private String Latitude;
	/** 信息来源 0：洛阳 1：宇视 3：其他 */
	private String SourceType;
	/** 保留字段 */
	private String Reserve1;
	/** 保留字段 */
	private String Reserve2;
	/** 保留字段 */
	private String Reserve3;

	/** CamID */
	private String CamID;
	/** ClusterCode */
	private String ClusterCode;

	public String getCamID() {
		return isNotNull(CamID);
	}

	public void setCamID(String camID) {
		CamID = camID;
	}

	public String getClusterCode() {
		return isNotNull(ClusterCode);
	}

	public void setClusterCode(String clusterCode) {
		ClusterCode = clusterCode;
	}

	public String getDeviceName() {
		return isNotNull(DeviceName);
	}

	public String getLongitude() {
		return isNotNullDouble(Longitude);
	}

	public String getLatitude() {
		return isNotNullDouble(Latitude);
	}

	public String getPort() {
		return isNotNullZero(Port);
	}

	public void setDeviceName(String deviceName) {
		DeviceName = deviceName;
	}

	public void setLongitude(String Longitude) {
		this.Longitude = Longitude;
	}

	public void setLatitude(String Latitude) {
		this.Latitude = Latitude;
	}

	public void setPort(String Port) {
		this.Port = Port;
	}

	public String getAutoID() {
		return isNotNull(AutoID);
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
	}

	public String getChPointID() {
		return isNotNull(ChPointID);
	}

	public void setChPointID(String chPointID) {
		ChPointID = chPointID;
	}

	public String getXTBH() {
		return isNotNull(XTBH);
	}

	public void setXTBH(String xTBH) {
		XTBH = xTBH;
	}

	public String getDeviceID() {
		return isNotNull(DeviceID);
	}

	public void setDeviceID(String deviceID) {
		DeviceID = deviceID;
	}

	public String getPlaceName() {
		return isNotNull(PlaceName);
	}

	public void setPlaceName(String placeName) {
		PlaceName = placeName;
	}

	public String getIP() {
		return isNotNull(IP);
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getSourceType() {
		return isNotNull(SourceType);
	}

	public void setSourceType(String sourceType) {
		SourceType = sourceType;
	}

	public String getReserve1() {
		return isNotNullZero(Reserve1);
	}

	public void setReserve1(String reserve1) {
		Reserve1 = reserve1;
	}

	public String getReserve2() {
		return isNotNullZero(Reserve2);
	}

	public void setReserve2(String reserve2) {
		Reserve2 = reserve2;
	}

	public String getReserve3() {
		return isNotNull(Reserve3);
	}

	public void setReserve3(String reserve3) {
		Reserve3 = reserve3;
	}

	public String isNotNull(String data) {
		return data == null ? "" : data;
	}

	public String isNotNullZero(String data) {
		return data == null || "".equals(data) ? "0" : data;
	}

	public String isNotNullDouble(String data) {
		return data == null || "".equals(data) ? "0.00" : data;
	}
}
