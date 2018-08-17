package com.huawei.checkpoint.data;

/**
 * VCM共通实体类
 * 
 */
public class VCMImageMotor {

	private String AutoID;

	private String PicID;
	/** 图像类型 -- 取值：1-车辆大图 2-车牌彩色小图 3-车牌二值化图 4-驾驶员面部特征图 5-副驾驶面部特征图 6-车标 */
	private String ImageType;
	/** 图像长 -- 图像的长度 */
	private String ImageWidth;
	/** 图像高 -- 图像的高度 */
	private String ImageHeight;
	/**
	 * 图像抓拍时刻 --
	 * YYYYMMDDHHMMSSMMM，时间按24小时制。第一组MM表示月，第二组MM表示分，第三组MMM表示毫秒。车辆大图才需要该字段。
	 */
	private String WatchTime;

	private String WatchTimeMill;

	public String getAutoID() {
		return isNotNull(AutoID);
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
	}

	public String getPicID() {
		return isNotNull(PicID);
	}

	public void setPicID(String picID) {
		PicID = picID;
	}

	public String getImageType() {
		return isNotNull(ImageType);
	}

	public void setImageType(String imageType) {
		ImageType = imageType;
	}

	public String getImageWidth() {
		return isNotNull(ImageWidth);
	}

	public void setImageWidth(String imageWidth) {
		ImageWidth = imageWidth;
	}

	public String getImageHeight() {
		return isNotNull(ImageHeight);
	}

	public void setImageHeight(String imageHeight) {
		ImageHeight = imageHeight;
	}

	public String isNotNull(String data) {
		return data == null ? "" : data;
	}

	public String getWatchTime() {
		return isNotNull(WatchTime);
	}

	public void setWatchTime(String watchTime) {
		WatchTime = watchTime;
	}

	public String getWatchTimeMill() {
		return isNotNull(WatchTimeMill);
	}

	public void setWatchTimeMill(String watchTimeMill) {
		WatchTimeMill = watchTimeMill;
	}

}
