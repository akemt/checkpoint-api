package com.huawei.checkpoint.data;

public class YuShiImagesMotor {

	/** 外键ID */
	private String AutoID;
	/** 图片ID */
	private String PicID;
	/** 图像索引 -- 从1开始。 */
	private String ImageIndex;
	/** 图像名称 -- */
	private String ImageURL;//
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
	private String PassTime;

	private String PassTimeMill;

	public String getPicID() {
		return PicID;
	}

	public void setPicID(String picID) {
		PicID = picID;
	}

	/** 图像的base64编码 */
	private String ImageData;

	public String getAutoID() {
		return AutoID;
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
	}

	public String getPassTimeMill() {
		return isNotNull(PassTimeMill);
	}

	public void setPassTimeMill(String passTimeMill) {
		PassTimeMill = passTimeMill;
	}

	public String getImageData() {
		return isNotNull(ImageData);
	}

	public void setImageData(String imageData) {
		ImageData = imageData;
	}

	public String getImageIndex() {
		return isNotNull(ImageIndex);
	}

	public void setImageIndex(String imageIndex) {
		ImageIndex = imageIndex;
	}

	public String getImageURL() {
		return isNotNull(ImageURL);
	}

	public void setImageURL(String imageURL) {
		ImageURL = imageURL;
	}

	public String getImageType() {
		return isNotNullZero(ImageType);
	}

	public void setImageType(String imageType) {
		ImageType = imageType;
	}

	public String getImageWidth() {
		return isNotNullZero(ImageWidth);
	}

	public void setImageWidth(String imageWidth) {
		ImageWidth = imageWidth;
	}

	public String getImageHeight() {
		return isNotNullZero(ImageHeight);
	}

	public void setImageHeight(String imageHeight) {
		ImageHeight = imageHeight;
	}

	public String getPassTime() {
		return isNotNull(PassTime);
	}

	public void setPassTime(String passTime) {
		PassTime = passTime;
	}

	public String isNotNull(String data) {
		return data == null ? "" : data;
	}
	
	public String isNotNullZero(String data) {
		return data == null || "".equals(data) ? "0" : data;
	}
}
