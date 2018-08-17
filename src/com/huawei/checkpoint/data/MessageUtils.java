package com.huawei.checkpoint.data;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.function.AgController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Socket通信：组装数据公共类 byte []
 * 
 * @author Xialf
 *
 */
public class MessageUtils {

	private static String strFH = "$_";
	private static Logger log = Logger.getLogger(MessageUtils.class);
	private int sgReDataType;

	private static int xmlFileNameLength;
	private String xmlFileName;
	private String strFilePathUrl;
	
	private ArrayList<String> filePathUrl;


	public ArrayList<String> getFilePathUrl() {
		return filePathUrl;
	}

	public void setFilePathUrl(ArrayList<String> filePathUrl) {
		this.filePathUrl = filePathUrl;
	}

	public String getStrFilePathUrl() {
		return strFilePathUrl;
	}

	public void setStrFilePathUrl(String strFilePathUrl) {
		this.strFilePathUrl = strFilePathUrl;
	}

	public int getSgReDataType() {
		return sgReDataType;
	}

	public void setSgReDataType(int sgReDataType) {
		this.sgReDataType = sgReDataType;
	}

	public static String getStrFH() {
		return strFH;
	}

	public static void setStrFH(String strFH) {
		MessageUtils.strFH = strFH;
	}

	public static int getXmlFileNameLength() {
		return xmlFileNameLength;
	}

	public static void setXmlFileNameLength(int xmlFileNameLength) {
		MessageUtils.xmlFileNameLength = xmlFileNameLength;
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}

	MessageUtils() {

	}

	/**
	 * 把Int String 转换成byte[] 类型
	 * 
	 * @param iSgReDataType
	 * @param sXmlData
	 * @param sXmlName
	 * @return
	 */
	public static byte[] setMessageByte(int iSgReDataType, String sXmlFileName) {

		byte[] bType = toByteArray(iSgReDataType, 4);

		byte[] bXmlFileName = sXmlFileName.getBytes();
		xmlFileNameLength = bXmlFileName.length;
		byte[] bXmlFileNameLength = toByteArray(xmlFileNameLength, 4);

		byte[] bFH = strFH.getBytes();

		byte[] bytes = new byte[bType.length + bXmlFileNameLength.length + bXmlFileName.length];

		System.arraycopy(bType, 0, bytes, 0, bType.length);

		System.arraycopy(bXmlFileNameLength, 0, bytes, bType.length, bXmlFileNameLength.length);

		System.arraycopy(bXmlFileName, 0, bytes, bType.length + bXmlFileNameLength.length, bXmlFileName.length);

		System.arraycopy(bFH, 0, bytes, bType.length + bXmlFileNameLength.length + bXmlFileName.length, bFH.length);

		return bytes;
	}

	/**
	 * 通过byte[] msg，组装信息文件
	 * 
	 * @param msg
	 * @return
	 */
	public static MessageUtils getMessageByte(byte[] msg) {

		int iType = 0;
		int iXmlFileLength = 0;
		byte bLoop;
		for (int i = 0; i < msg.length; i++) {

			bLoop = msg[i];
			if (i < 4) {
				iType += (bLoop & 0xFF) << (8 * i);
			} else if (i < 8 && i >= 4) {
				iXmlFileLength += (bLoop & 0xFF) << (8 * i);
			} else {
				break;
			}

		}

		byte[] bXmlName = new byte[iXmlFileLength];
		System.arraycopy(msg, 4 + 4, bXmlName, 0, iXmlFileLength);
		String strXmlFileName = null;
		try {
			strXmlFileName = new String(bXmlName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			log.warn("getMessageByte excption" , e);
		}

		MessageUtils temp = new MessageUtils();
		temp.setSgReDataType(iType);
		temp.setXmlFileName(strXmlFileName);
		return temp;
	}

	/**
	 * 代码转自：java int 与 byte转换
	 * 
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/**
	 * 组装数据到Map 中
	 * 
	 * @param iSgReDataType
	 * @param sXmlFileName
	 * @return
	 */
	public static String setMessageToJson(int iSgReDataType,ArrayList<String> strFilePathUrl) {

		JSONObject json = new JSONObject();
		
		json.put("iSgReDataType", iSgReDataType);
		json.element("filePathUrl", strFilePathUrl);
		String strXmlMsg = json.toString() + strFH;

		return strXmlMsg;

	}
	
	/**
	 * 取值
	 * 
	 * @param msg
	 * @return
	 */
	public static MessageUtils getMessageToJson(String msg) {

		JSONObject jsonObject = JSONObject.fromObject(msg);
		
		MessageUtils temp = new MessageUtils();
		temp.setSgReDataType(Integer.parseInt(jsonObject.get("iSgReDataType").toString()));
		
		@SuppressWarnings("unchecked")
		ArrayList<String> filelist = (ArrayList<String>) JSONArray.toCollection(jsonObject.getJSONArray("filePathUrl"));
		temp.setFilePathUrl(filelist);
		return temp;

	}
	 
	
}
