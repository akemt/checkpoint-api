package com.huawei.checkpoint.data;

public class YuShiVideoMotor {

	/** 外键ID */
	private String AutoID;

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

	public String getAutoID() {
		return AutoID;
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
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

	public String isNotNull(String data) {
		return data == null ? "" : data;
	}

}
