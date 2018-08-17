package com.huawei.checkpoint.data;
/**
 * @author IA-dong_b
 *
 */

public class SubscribesInfo {
	private String subscribeID;

	private String title;	

	private String ReceiveAddr;
	
	private String subCategory;
	
	public String getSubscribeID() {
		return subscribeID;
	}

	public void setSubscribeID(String subscribeID) {
		this.subscribeID = subscribeID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReceiveAddr() {
		return ReceiveAddr;
	}

	public void setReceiveAddr(String receiveAddr) {
		ReceiveAddr = receiveAddr;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

}
