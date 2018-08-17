package com.huawei.checkpoint.data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.utils.ByteConvert;
import com.huawei.checkpoint.utils.RingBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 * 解包
 * 
 * @author Xialf
 *
 */
public class YsUnPack {

	private static Logger log = Logger.getLogger(YsUnPack.class);

	/** 宇视协议消息开始标识 */
	private final static int gszMsgBeginFlag = 0x77aa77aa;
	/** 宇视协议消息结束标识 */
	private final static int gszMsgEndFlag = 0x77ab77ab;
	/** 宇视协议-消息头长度 */
	private final static int START_CODE_LEN = 4;
	/** 宇视协议-消息尾长度 */
	private final static int END_CODE_LEN = 4;
	/** 宇视协议-版本号长度 */
	private final static int VERSION_LEN = 4;
	/** 宇视协议-命令码长度 */
	private final static int CMD_LEN = 4;
	/** 宇视协议-消息体长度 */
	private final static int BODYLEN_LEN = 4;
	/** 宇视协议-整包长度（不包括Data长度） */
	private final static int PACKET_META_LEN = START_CODE_LEN + BODYLEN_LEN + VERSION_LEN + CMD_LEN + END_CODE_LEN;

	/** 宇视协议-DATA中XML长度 */
	private final static int XML_LEN = 4;

	/** 宇视协议-DATA中PICNUM长度 */
	private final static int PICNUM_LEN = 4;

	/** 宇视协议-DATA中PIC长度 */
	private final static int PIC_LEN = 4;

	/** 联机信号保活命令码 */
	private final static int TMS_MSG_CMDID_KEEPALIVE = 101;
	
	/** 联机信号保活返回命令码 */
	private final static int TMS_MSG_CMDID_KEEPALIVE_RETURN = 501;
	
	/**  宇视协议-保活包长度 */
	private final static int KEEPALIVE_PAC_LEN = 52;

	/** 实时图像命令码 */
	private final static int TMS_MSG_CMDID_REALTIME = 111;

	/** 历史图像命令码 */
	private final static int TMS_MSG_CMDID_HISTORY = 112;

	/** 实时图像命令码 */
	private final static int TMS_MSG_CMDID_REALTIME_V2 = 115;

	/** 历史图像命令码 */
	private final static int TMS_MSG_CMDID_HISTORY_V2 = 116;

	/** 实时图像命令码V3 */
	private final static int TMS_MSG_CMDID_REALTIME_V3 = 118;

	/** 历史图像命令码V3 */
	private final static int TMS_MSG_CMDID_HISTORY_V3 = 119;

	/** 跨域实时图像命令码 */
	private final static int TMS_MSG_CMDID_INTER_REALTIME = 211;

	/** 跨域历史图像命令码 */
	private final static int TMS_MSG_CMDID_INTER_HISTORY = 212;

	/** 新实时/历史图像响应 */
	private final static int TMS_MSG_CMDID_RESP_RH_V2 = 512;

	/** 通用错误码 */
	/** < 执行成功 */
	private final static int ERR_COMMON_SUCCEED = 0;
	/** < 执行失败 */
	private final static int ERR_COMMON_FAIL = 1;

	/**
	 * 完整的解包和回执信息公共方法
	 * 
	 * @return
	 */
	public static int msgInfoProc(RingBuffer ringBuffer, ChannelHandlerContext ctx) {

		unPacket(ringBuffer, ctx);

		return ERR_COMMON_SUCCEED;
	}

	/**
	 * 接收宇视数据包
	 * 
	 * @param bDataRecvd
	 * @param resInfo
	 * @return
	 */
	public synchronized static void unPacket(RingBuffer ringBuffer, ChannelHandlerContext ctx) {
		log.debug("读取完整包开始-ringBuffer.start：" + ringBuffer.start);
		log.debug("读取完整包开始-ringBuffer.end：" + ringBuffer.end);
		/** 1.把 接收的数据bDataRecvd放到RingBuffer */

		/** 2.当start <= end时，循环遍历。当start > end时，退出。继续等待解包。 */
		/** 首先循环4个字节，判断消息头；如果没有在循环4个字节 ，直到符合消息头-gszMsgBeginFlag; */

		ResponseInfo resInfo = new ResponseInfo();
		log.debug("读取解析包-开始");
		int startLen = START_CODE_LEN;
		while (START_CODE_LEN <= ringBuffer.dataLen) {
			// while (ringBuffer.start + gulUVMsgFlagLen <= ringBuffer.end) {

			int iHeadData = tryGetByteData(ringBuffer, START_CODE_LEN);
			if (iHeadData != gszMsgBeginFlag) {
				ringBuffer.shift(START_CODE_LEN);
				startLen = ringBuffer.start;
				continue;
			} else {
				log.debug("消息头-0x77aa77aa-2007660458：" + iHeadData);
				break;
			}
		}

		boolean realData = false;

		int iLenData = 0;

		int iVerData = 0;
		/** 3.找到消息头之后，剩下的长度是否大于12个字节（总长度+版本号+命令码） */
		if (BODYLEN_LEN + VERSION_LEN + CMD_LEN > ringBuffer.dataLen) {
			return;
		} else {
			// while (ringBuffer.start + 3*gulUVMsgFlagLen <= ringBuffer.end)
			// {// 如果大于12个字节，接下来可以读取（总长度+版本号+命令码）

			byte[] tmp = ringBuffer.tryGet(startLen + BODYLEN_LEN + VERSION_LEN + CMD_LEN);
			/** 4.读取前4个字节（总长度） */
			byte[] bLenData = new byte[BODYLEN_LEN];
			System.arraycopy(tmp, startLen, bLenData, 0, BODYLEN_LEN);
			iLenData = ByteConvert.bytesToInt(bLenData);
			// int iLenData = getByteData(ringBuffer, gulUVMsgFlagLen);
			log.debug("读取前4个字节（总长度）：" + iLenData);
			/** 5.读取版本号 */
			byte[] bVerData = new byte[VERSION_LEN];
			System.arraycopy(tmp, startLen + 4, bVerData, 0, VERSION_LEN);
			iVerData = ByteConvert.bytesToInt(bVerData);
			// int iVerData = getByteData(ringBuffer, gulUVMsgFlagLen);
			log.debug("读取版本号：" + iVerData);
			/**
			 * 操作
			 * 
			 */

			/** 5.读取命令码 */
			byte[] bCmdData = new byte[CMD_LEN];
			System.arraycopy(tmp, startLen + 8, bCmdData, 0, CMD_LEN);
			int iCmdID = ByteConvert.bytesToInt(bCmdData);
			log.debug("读取命令码：" + iCmdID);
			// 实时记录信息（有应答） 115 当车辆违章时，上传1条记录（违章）
			// 历史记录信息（有应答） 116 当车辆违章时，上传1条记录（违章）
			// 实时记录信息（有应答） 118 当车辆违章时，上传2条记录（卡口+违章）
			// 历史记录信息（有应答） 119 当车辆违章时，上传2条记录（卡口+违章）
			switch (iCmdID) {
			case TMS_MSG_CMDID_KEEPALIVE: {// 保活
				log.debug("读取命令码：" + iCmdID + "-保活状态!");
				int packLen = iLenData + START_CODE_LEN + BODYLEN_LEN + END_CODE_LEN;
				if (packLen > ringBuffer.dataLen) {

					log.debug("不是完整的包！");
					return;
				}else{
					byte[] heartData = getByteDataPic(ringBuffer, KEEPALIVE_PAC_LEN);
					
					log.debug("KEEP ALIVE Req: "+ByteConvert.bytesToInt(heartData,12));
					
					ByteConvert.intToBytes(TMS_MSG_CMDID_KEEPALIVE_RETURN,heartData, 12);
					
					log.debug("KEEP ALIVE response: "+ByteConvert.bytesToInt(heartData,12));
					//hearData[12] = ''
//					ctx.writeAndFlush(heartData);
					ByteBuf resp = Unpooled.copiedBuffer(heartData);
					ctx.writeAndFlush(resp);
					log.debug("heart beat 读取解析包-结束");
					/** -------递归调用---------- */
					unPacket(ringBuffer, ctx);
				}
				break;
			}
			case 111:
			case TMS_MSG_CMDID_REALTIME_V2:
			case TMS_MSG_CMDID_HISTORY_V2:
			case TMS_MSG_CMDID_REALTIME_V3:
			case TMS_MSG_CMDID_HISTORY_V3:
			case TMS_MSG_CMDID_INTER_HISTORY: {

				int ulTempCmdID = iCmdID;
				if ((TMS_MSG_CMDID_REALTIME_V2 == ulTempCmdID) || (TMS_MSG_CMDID_REALTIME_V3 == ulTempCmdID)
						|| (TMS_MSG_CMDID_INTER_REALTIME == ulTempCmdID)) {
					ulTempCmdID = TMS_MSG_CMDID_REALTIME;
				} else if ((TMS_MSG_CMDID_HISTORY_V2 == ulTempCmdID) || (TMS_MSG_CMDID_HISTORY_V3 == ulTempCmdID)) {
					ulTempCmdID = TMS_MSG_CMDID_HISTORY;
				} else {
					/* 历史/实时图像都是回复511响应 */
				}
				realData = true;
			}
			default:
				// ringBuffer.start = 0;
				// ringBuffer.shift(len);
				break;

			}
		}
		int packLen = iLenData + START_CODE_LEN + BODYLEN_LEN + END_CODE_LEN;
		int dataLen = iLenData - CMD_LEN - VERSION_LEN;
		 if (!realData) {
//		if (realData) {
			// int dataLen = iLenData - gulUVMsgFlagLen - gulUVMsgFlagLen;
			if (packLen <= ringBuffer.dataLen) {
				ringBuffer.shift(packLen); 
			} else {
				log.debug("没有符合的CmdID！");
				return;
			}
		} else {
			/** 6.读取DATA-xml */
			if (packLen > ringBuffer.dataLen) {

				log.debug("不是完整的包！");
				return;
			}
			// 把ringBuffer.start 指到 DATA 开头
			ringBuffer.shift(START_CODE_LEN + BODYLEN_LEN + VERSION_LEN + CMD_LEN);
			log.debug("读取DATA-xml:" + ringBuffer.start);
			log.debug("读取DATA-xml:" + ringBuffer.end);
			/** 7.读取车辆信息长度:VehicleLength */
			int vehicleLength = getByteData(ringBuffer, XML_LEN);
			log.debug("读取车辆信息长度-VehicleLength：" + vehicleLength);

			/** 8.读取车辆信息XML CHAR:VehicleInfo */
			String vehicleInfo = getByteDataStr(ringBuffer, vehicleLength);

			String strVehicleInfo = vehicleInfo == null ? "" : vehicleInfo.trim();

			String strTollgateID = "";
			String strCamID = "";

			if (iVerData == 2) {// ----------以XML形式接收数据
				log.debug("读取车辆信息XML-VehicleInfo：" + strVehicleInfo);
				Document document = null;
				try {
					document = DocumentHelper.parseText(strVehicleInfo);
//					document.setXMLEncoding("UTF-8");
					strCamID = document.getRootElement().element("CamID").getTextTrim();
					// 卡口编号
					strTollgateID = document.getRootElement().element("TollgateID").getTextTrim();
					resInfo.setSzDevCode(strTollgateID);
					// 记录 ID 号
					String strRecordID = document.getRootElement().element("RecordID").getTextTrim();
					resInfo.setSzRecID(strRecordID);
				} catch (DocumentException e1) {
					log.warn(" Exception,src data:"+strVehicleInfo , e1);
					//e1.printStackTrace();
				}catch (Exception e) {
					log.warn(" Exception,src data:"+strVehicleInfo , e);
					//e.printStackTrace();
				}

				/** 9.读取总共N张图片-XML:PicNum */
				int picNum = getByteData(ringBuffer, PICNUM_LEN);
				log.debug("读取总共N张图片-PicNum：" + picNum);
				int picAllSize = 0;
				log.debug("保存图片-开始");
				for (int i = 0; i < picNum; i++) {
					List<?> imageList = document.getRootElement().elements("Image");
					/** 10.读取图片1大小:PicSize */
					int picSize = getByteData(ringBuffer, PIC_LEN);
					log.debug("读取图片1大小:Pic1Size：" + picSize);
					if(picSize > 0){

						picAllSize += picSize;
						/** 11.读图片数据:PicData */
						byte[] picData = getByteDataPic(ringBuffer, picSize);

						/** -------往XML新增ImgData----start------- */
						Element image = (Element) imageList.get(i);
						if(image.element("ImageData") == null || "".equals(image.element("ImageData"))){
							image.addElement("ImageData").setText(Base64.getEncoder().encodeToString(picData));
						}else{
							image.element("ImageData").setText(Base64.getEncoder().encodeToString(picData));
						}
						/** -------往XML新增ImgData----end------- */
						// log.debug("读图片1数据-Pic1Data："+pic1Data);

						/** ---------保存图片到本地--------------- */
//						Base64Trans.generateImagebyByte(picData);
					}
				}
				log.debug("保存图片-结束");
				// 实际真实的DATA长度
				int realDataLen = XML_LEN + vehicleLength + PICNUM_LEN + picNum * PIC_LEN + picAllSize;
				/** 12.读取车牌图片大小:vehiclePicSize --当dataLen - realDataLen */
				if (dataLen > realDataLen) {
					int vehiclePicSize = getByteData(ringBuffer, PIC_LEN);
					log.debug("读取车牌图片大小-vehiclePicSize：" + vehiclePicSize);

					if(vehiclePicSize > 0){

						/** 13.读取车牌小图数据:vehiclePicData */
						byte[] vehiclePicData = getByteDataPic(ringBuffer, vehiclePicSize);
						log.debug("读取车牌小图数据-vehiclePicData：" + vehiclePicData);
						/** ---------保存图片到本地------start--------- */
//						Base64Trans.generateImagebyByte(vehiclePicData);
					}
					/** ---------保存图片到本地------end--------- */
				}
				/** ---------保存XML到本地-----start---------- */
				SimpleDateFormat sdftime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				Date now = new Date();
				String xmlName="";
				if(strCamID.equals("")){
					xmlName = "CamID-" + sdftime.format(now);
				}else{
					xmlName = strCamID + "-" + sdftime.format(now);
				}

//				FileWriter fw;
				String vehicleInfoXml = document.asXML();
//				log.debug("车辆信息XML加UTF－8-VehicleInfo：" + vehicleInfoXml);
				try {
					log.debug("保存XML-开始");
					String[] ysList = SystemManager.getIns().getYsPathList();
					for (String tmp : ysList) {
						if (tmp.length() > 0) {
//							fw = new FileWriter("e:\\ysxml\\" + strTollgateID+"-"+xmlName + ".xml");
//							fw = new FileWriter(tmp +"/"+ xmlName + ".xml");
//							fw.write(vehicleInfoXml);
//							fw.flush();
//							fw.close();
							 
							 OutputStreamWriter outPutStr = new OutputStreamWriter(new FileOutputStream(tmp +"/"+ xmlName + ".xml"), "UTF-8");
							 Writer wt = new BufferedWriter(outPutStr);
							 wt.write(vehicleInfoXml);
							 wt.flush();
							 wt.close();
							 break;
						}
					}
					log.debug("保存XML-结束:" + xmlName + ".xml");
				} catch (IOException e) {
					log.warn(" Exception,xml name:"+xmlName , e);
					//e.printStackTrace();
				}
				/** ---------保存XML到本地-----end---------- */
			} else if (iVerData == 3) {// ----------以JSON形式接收数据
				JSONObject jsonObject = JSONObject.fromObject(strVehicleInfo).getJSONObject("Vehicle");
				strCamID = jsonObject.getString("CamID");
				
				strTollgateID = jsonObject.getString("TollgateID");
				resInfo.setSzDevCode(strTollgateID);
				// 记录 ID 号
				String strRecordID = jsonObject.getString("RecordID");
				resInfo.setSzRecID(strRecordID);

				/** 9.读取总共N张图片-XML:PicNum */
				int picNum = getByteData(ringBuffer, PICNUM_LEN);
				log.debug("读取总共N张图片-PicNum：" + picNum);
				int picAllSize = 0;
				log.debug("保存图片-开始");
				List<?> imageList = jsonObject.getJSONArray("Image");
				for (int i = 0; i < picNum; i++) {
					/** 10.读取图片1大小:PicSize */
					int picSize = getByteData(ringBuffer, PIC_LEN);
					log.debug("读取图片1大小:Pic1Size：" + picSize);
					picAllSize += picSize;
					/** 11.读图片数据:PicData */
					byte[] picData = getByteDataPic(ringBuffer, picSize);

					/** -------往JSON新增ImgData----start------- */
					JSONObject image = (JSONObject) imageList.get(i);
					image.put("ImageData", Base64.getEncoder().encodeToString(picData));
					log.warn(jsonObject.toString());
					/** -------往XML新增ImgData----end------- */
					// log.debug("读图片1数据-Pic1Data："+pic1Data);

					/** ---------保存图片到本地--------------- */
//					Base64Trans.generateImagebyByte(picData);
				}
				log.debug("保存图片-结束");
				// 实际真实的DATA长度
				int realDataLen = XML_LEN + vehicleLength + PICNUM_LEN + picNum * PIC_LEN + picAllSize;
				/** 12.读取车牌图片大小:vehiclePicSize --当dataLen - realDataLen */
				if (dataLen > realDataLen) {
					int vehiclePicSize = getByteData(ringBuffer, PIC_LEN);
					log.debug("读取车牌图片大小-vehiclePicSize：" + vehiclePicSize);

					/** 13.读取车牌小图数据:vehiclePicData */
					byte[] vehiclePicData = getByteDataPic(ringBuffer, vehiclePicSize);
					log.debug("读取车牌小图数据-vehiclePicData：" + vehiclePicData);
					/** ---------保存图片到本地------start--------- */
//					Base64Trans.generateImagebyByte(vehiclePicData);
					/** ---------保存图片到本地------end--------- */
				}
				/** ---------保存JSON到本地-----start---------- */
				SimpleDateFormat sdftime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				Date now = new Date();
				String xmlName = strCamID + "-" + sdftime.format(now);

				FileWriter fw;
				String vehicleInfoJson = jsonObject.toString();
				try {
					log.debug("保存XML-开始");
					String[] ysList = SystemManager.getIns().getYsPathList();
					for (String tmp : ysList) {
						if (tmp.length() > 0) {
							fw = new FileWriter(tmp + "/"+ xmlName + ".xml");
							fw.write(vehicleInfoJson);
							fw.flush();
							fw.close();
						}
					}
					//log.debug("保存XML-结束");
					log.debug("保存XML-结束:" + xmlName + ".xml");
				} catch (IOException e) {
					//e.printStackTrace();
					log.warn("保存XML-exception:" + xmlName + ".xml", e);
				}

				/** ---------保存JSON到本地-----end---------- */

				log.debug("读取车辆信息JSON-VehicleInfo：" + vehicleInfo);
			} else {
				log.warn("没有符合的版本号信息：" + iVerData);
			}

			// 得到的数据如何处理图片和XML
			/** 14.读取列尾:gszMsgEndFlag */
			int iTailData = getByteData(ringBuffer, END_CODE_LEN);
			if (iTailData != gszMsgEndFlag) {
				log.debug("不是标准的列尾-0x77ab77ab-2007725995：" + iTailData);
			}
			log.debug("读取列尾-0x77ab77ab-2007725995：" + iTailData);
			log.debug("读取完整包结束-ringBuffer.start：" + ringBuffer.start);
			log.debug("读取完整包结束-ringBuffer.end：" + ringBuffer.end);

			// 需要传入resInfo中记录ID号-RecordID 和卡口编号-TollgateID
			resInfo.setUlResponseCmdID(TMS_MSG_CMDID_RESP_RH_V2);
			// 发送回执消息
			responseProc(resInfo, iVerData, ctx);
			log.debug("读取解析包-结束");
			/** -------递归调用---------- */
			unPacket(ringBuffer, ctx);
		}
	}

	/**
	 * 获取字节数据大小int
	 * 
	 * @param pcMsgBuff
	 * @param start
	 * @param end
	 * @return
	 */
	private static int tryGetByteData(RingBuffer ringBuffer, int len) {
		byte[] msgData = new byte[len];
		msgData = ringBuffer.tryGet(msgData.length);
		int i = -1;
		if (msgData != null) {
			i = ByteConvert.bytesToInt(msgData);
		} else {
			System.out.println("内容不够，无法提取！");
		}
		return i;

	}

	/**
	 * 
	 * @param ringBuffer
	 * @param len
	 * @return
	 */
	private static int getByteData(RingBuffer ringBuffer, int len) {
		byte[] msgData = new byte[len];
		msgData = ringBuffer.get(msgData.length);
		int i = -1;
		if (msgData != null) {
			i = ByteConvert.bytesToInt(msgData);
		} else {
			System.out.println("内容不够，无法提取！");
		}
		return i;

	}

	/**
	 * 获取字节数据char内容
	 * 
	 * @param pcMsgBuff
	 * @param start
	 * @param end
	 * @return
	 */
	private static String getByteDataStr(RingBuffer ringBuffer, int len) {
		byte[] msgData = new byte[len];
		msgData = ringBuffer.get(msgData.length);
		String str = null;
		if (msgData != null) {
			try {
				str = new String(msgData,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.warn("exception:",e);
				//e.printStackTrace();
			}
		} else {
			log.debug("内容不够，无法提取！");
		}
		return str;

	}

	/**
	 * 获取字节数据Byte[]内容
	 * 
	 * @param pcMsgBuff
	 * @param start
	 * @param end
	 * @return
	 */
	private static byte[] getByteDataPic(RingBuffer ringBuffer, int len) {
		byte[] msgData = new byte[len];
		msgData = ringBuffer.get(msgData.length);
		if (msgData == null) {
			System.out.println("内容不够，无法提取！");
		}
		return msgData;

	}

	/**
	 * TMS给卡口服务器回复响应的处理函数 \n
	 * 
	 * @param[IN] TMS_UV_RESPONSE_INFO_S pstResponseInfo 回复响应的结构
	 * @param[OUT] TUCL_REG_INFO_S pstRegInfo TUCL的连接信息结构
	 * @return ULONG, - 成功：ERR_COMMON_SUCCEED - 失败：见错误码文件
	 * @return iVerData 版本号
	 * @note 无
	 */
	public static void responseProc(ResponseInfo info, int iVerData, ChannelHandlerContext ctx) {
		int ulRet = ERR_COMMON_SUCCEED;

		if (info == null) {
			return;
		}

		SendInfo sendInfo = new SendInfo();

		switch (info.getUlResponseCmdID()) {
		/* 命令行调用外域消息，才回复外域响应 */
		case TMS_MSG_CMDID_RESP_RH_V2: {
			String oXmlBuff = "";
			if (iVerData == 2) {// 以XML回执信息---2
				oXmlBuff += "<?xml version=\"1.0\" ?>\r\n";
			} else if (iVerData == 3) {
				oXmlBuff += "<root>";
			}
			oXmlBuff += "<Response>";
			oXmlBuff += "<CamID>";// 卡口相机编号
			oXmlBuff += info.getSzDevCode();
			oXmlBuff += "</CamID>";

			oXmlBuff += "<RecordID>";// 记录ID编号
			oXmlBuff += info.getSzRecID();
			oXmlBuff += "</RecordID>";

			oXmlBuff += "<Result>";// 错误码
			oXmlBuff += "0";
			oXmlBuff += "</Result>";

			// CHAR szReqCmdID[32] = {0};
			// IMOS_snprintf(szReqCmdID, 32, "%lu",
			// pstResponseInfo->ulResponseCmdID);
			oXmlBuff += "<ReqCmdID>";// 请求命令码
			oXmlBuff += info.getUlResponseCmdID();
			oXmlBuff += "</ReqCmdID>";

			oXmlBuff += "<DBRecordID>";// 内部使用， 内部使用， 内部使用， 数据库记录 数据库记录
			oXmlBuff += "0";
			oXmlBuff += "</DBRecordID>";

			oXmlBuff += "</Response>";
			if (iVerData == 3) {
				oXmlBuff += "</root>";
			}
			// XML长度
			int ulXmlLen = oXmlBuff.length();

			// 整个包的长度
			int ulPacketLen = 5 * 4 + 4 + ulXmlLen;

			byte[] pcPacketBuff = new byte[ulPacketLen];
			if (pcPacketBuff == null) {
				return;
			}
			int pcTempStart = 0;
			/** 消息头 ULong byte */
			byte[] beginMsg = ByteConvert.intToBytes(gszMsgBeginFlag);
			System.arraycopy(beginMsg, 0, pcPacketBuff, pcTempStart, START_CODE_LEN);
			pcTempStart += 4;

			/** 赋值消息长度 */
			byte[] ulTempValue = ByteConvert.intToBytes(ulPacketLen - 3 * 4);
			System.arraycopy(ulTempValue, 0, pcPacketBuff, pcTempStart, BODYLEN_LEN);
			pcTempStart += 4;

			/** 赋值协议版本号 */
			ulTempValue = ByteConvert.intToBytes(2);
			System.arraycopy(ulTempValue, 0, pcPacketBuff, pcTempStart, VERSION_LEN);
			pcTempStart += 4;

			/** 赋值消息命令码 */
			ulTempValue = ByteConvert.intToBytes(info.getUlResponseCmdID());
			System.arraycopy(ulTempValue, 0, pcPacketBuff, pcTempStart, CMD_LEN);
			pcTempStart += 4;

			/** 赋值XML长度 */
			ulTempValue = ByteConvert.intToBytes(ulXmlLen);
			System.arraycopy(ulTempValue, 0, pcPacketBuff, pcTempStart, XML_LEN);
			pcTempStart += 4;

			/** 赋值XML */
			try {
				if (iVerData == 2) {// 以XML回执信息---2
					System.arraycopy(oXmlBuff.getBytes("utf-8"), 0, pcPacketBuff, pcTempStart, ulXmlLen);
				} else if (iVerData == 3) {// 以JSON回执信息---3
					XMLSerializer xmlSerializer = new XMLSerializer();
					JSON json = xmlSerializer.read(oXmlBuff);
					log.debug("response-json:" + json.toString());

					System.arraycopy(json.toString(1).getBytes("utf-8"), 0, pcPacketBuff, pcTempStart, json.size());
				}
			} catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
				log.warn("exception:",e);
			}
			pcTempStart += ulXmlLen;

			/** 列尾 */
			byte[] endMsg = ByteConvert.intToBytes(gszMsgEndFlag);
			System.arraycopy(endMsg, 0, pcPacketBuff, pcTempStart, END_CODE_LEN);

			sendInfo.setPcDataBuf(pcPacketBuff);
			sendInfo.setlDataLength(ulPacketLen);

			/** ------------Response---------- */
//			ctx.channel().writeAndFlush(sendInfo);
			
			ByteBuf resp = Unpooled.copiedBuffer(pcPacketBuff);
			ctx.writeAndFlush(resp);
			log.info("回复响应=." + oXmlBuff);
			if (ERR_COMMON_SUCCEED != ulRet) {
				/* 此处失败仅记录日志 */
				log.warn("回复响应失败，错误码=." + ulRet);
			}

			break;
		}
		default: {
			/* 故障会走到这个分支，不需要回响应，所以不记录日志 */
			break;
		}
		}

		return;
	} 
}
