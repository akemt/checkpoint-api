package com.huawei.checkpoint.data;

/**
 * 回执消息
 * 
 * @author Xialf
 *
 */
public class ResponseInfo {
	/** 回复响应的消息码,见宏定义 */
	private int ulResponseCmdID;
	/** 卡口设备编码 */
	private String szDevCode;

	/** 记录ID号，16位最大 */
	private String szRecID;

	/** 记录ID,只在回复实时/历史图像的消息时有用 */
	private int ulRecID;

	/** 消息返回的错误码，见TMS_UNIVIEW_ERRCODE_E */
	private int ulRespErrCode;

	/** 请求消息的命令码 */
	private int ulReqCmdID;

	public int getUlResponseCmdID() {
		return ulResponseCmdID;
	}

	public void setUlResponseCmdID(int ulResponseCmdID) {
		this.ulResponseCmdID = ulResponseCmdID;
	}

	public String getSzDevCode() {
		return szDevCode;
	}

	public void setSzDevCode(String szDevCode) {
		this.szDevCode = szDevCode;
	}

	public String getSzRecID() {
		return szRecID;
	}

	public void setSzRecID(String szRecID) {
		this.szRecID = szRecID;
	}

	public int getUlRecID() {
		return ulRecID;
	}

	public void setUlRecID(int ulRecID) {
		this.ulRecID = ulRecID;
	}

	public int getUlRespErrCode() {
		return ulRespErrCode;
	}

	public void setUlRespErrCode(int ulRespErrCode) {
		this.ulRespErrCode = ulRespErrCode;
	}

	public int getUlReqCmdID() {
		return ulReqCmdID;
	}

	public void setUlReqCmdID(int ulReqCmdID) {
		this.ulReqCmdID = ulReqCmdID;
	}

}