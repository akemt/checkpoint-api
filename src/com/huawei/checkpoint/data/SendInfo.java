package com.huawei.checkpoint.data;

/**
 * 用于数据发送传递的结构体
 * 
 * @author Xialf
 *
 */
public class SendInfo {

	/** 组ID和控制块ID,由TUCL自动生成，创建成功后返回给调用者，由调用者自己保存 */
	private int ulCBGrpID;
	private int ulCtrlBlockID;
	/** 如果是作为调用者传入的参数， 表示要发送的数据块的长度， 如果是作为发送出错后返回给调用者的数据，表示已经成功发送出去了的数据长度 */
	private int lDataLength;

	/**
	 * 发送目的地址和端口,地址值和端口值必须为网络序;
	 *  如果两者任意一个为0,则TUCL使用控制块中保存的地址和端口发送，
	 *  如果两者都填写为非0值,则使用最新的地址和端口发送并更新tucl内控制块中保存的地址和端口
	 */
	private int ulDstAddr;
	private int usDstPort;

	/** 数据块 */
	private byte[] pcDataBuf;

	public byte[] getPcDataBuf() {
		return pcDataBuf;
	}

	public void setPcDataBuf(byte[] pcDataBuf) {
		this.pcDataBuf = pcDataBuf;
	}

	public int getUlCBGrpID() {
		return ulCBGrpID;
	}

	public void setUlCBGrpID(int ulCBGrpID) {
		this.ulCBGrpID = ulCBGrpID;
	}

	public int getUlCtrlBlockID() {
		return ulCtrlBlockID;
	}

	public void setUlCtrlBlockID(int ulCtrlBlockID) {
		this.ulCtrlBlockID = ulCtrlBlockID;
	}

	public int getlDataLength() {
		return lDataLength;
	}

	public void setlDataLength(int lDataLength) {
		this.lDataLength = lDataLength;
	}

	public int getUlDstAddr() {
		return ulDstAddr;
	}

	public void setUlDstAddr(int ulDstAddr) {
		this.ulDstAddr = ulDstAddr;
	}

	public int getUsDstPort() {
		return usDstPort;
	}

	public void setUsDstPort(int usDstPort) {
		this.usDstPort = usDstPort;
	}

}
