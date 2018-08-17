package com.huawei.checkpoint.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.huawei.checkpoint.function.AgController;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.VCMAccess;

/**
 * 卡口LIST共通类
 * 
 * @author Xialf
 *
 */
public class ChPListConstant {

	private static Logger log = Logger.getLogger(ChPListConstant.class);

	/**
	 * 保存卡口list 洛阳数据
	 * 
	 * @param strCookie
	 * @param lyMotorList
	 * @param cf
	 */
	//TODO kakoulist start
	public static void saveLYChPList(String strCookie, List<LuoyangMotor> lyMotorList, Config cf) {

		if (lyMotorList == null || lyMotorList.isEmpty()) {
			return;
		}
		
		String reStr = VCMAccess.queryAllChPointList(strCookie, CheckPointStaticUtils.LUO_YANG, cf);		
		ArrayList<String> ChPointList = new ArrayList<String>();
		
      try {
          Document document = DocumentHelper.parseText(reStr);
          Element rootElement = document.getRootElement();
                
          Element result = rootElement.element("result");
          String strCode = result.element("code").getTextTrim();
          log.info("VCMAccess.queryAllChPointList strCode:" + strCode);
          if (strCode.equals("0")) {
              Element metadata = result.element("meta-data");
              List<Element> elements = metadata.elements();
              if (0 < elements.size()) {
                  log.info("VCMAccess.queryAllChPointList success." );
                  for (int i = 0; i < elements.size(); i++) {
                      String tmp =  elements.get(i).element("content").getTextTrim();
                      ChPointList.add(tmp);
                  		}
                   }
             }
      } catch (DocumentException e){
          log.warn("DocumentException: ", e);
      } catch (Exception e) {
          log.warn("Exception: ", e);
        }
	
		List<Object> motorlist = new ArrayList<Object>();
		LuoyangMotor lyMotor = null;
		ChPListMotor chPListMotor = null;
			
		for (int i = 0; i < lyMotorList.size(); i++) {
			lyMotor = lyMotorList.get(i);
			String strDeviceID = lyMotor.getDeviceNo();
			if(strDeviceID == null || "".equals(strDeviceID)){
				log.warn("卡口LIST：洛阳过车数据中不存在设备ID记录!");
			}else{
				// 验证卡口编号，在卡口LIST中是否存在
				boolean flag = false;
				
				for (int j = 0; j < ChPointList.size(); j++) {
					if(ChPointList.get(j).contains(strDeviceID)) {
						flag = true;
						break;
					}
				}
					
				if (!flag) {// 不存在，则新增一条记录
					chPListMotor = new ChPListMotor();
					String strID = CheckPointStaticUtils.getUUID();
					chPListMotor.setAutoID(strID);
					chPListMotor.setXTBH(lyMotor.getXTBH());
					chPListMotor.setDeviceID(strDeviceID);
					chPListMotor.setDeviceName(strDeviceID);
					chPListMotor.setPlaceName(lyMotor.getDeviceDesc());
					chPListMotor.setSourceType(String.valueOf(CheckPointStaticUtils.LUO_YANG));

					motorlist.add(chPListMotor);
				} else {
					log.warn("卡口LIST中存在记录：" + strDeviceID);
				}
			}
		}
			
		// 生成VCM需要的XML文件：创建元数据索引XML(VA_Create_Metadata_Index)
		if (motorlist != null && motorlist.size() > 0) {
			String strXml = VCMAccess.getMetadataIndexStr(cf.getStorageName(), cf.getTableNameChplist(), motorlist);
			VCMAccess.vcmVACreateMetadataIndex(strCookie, strXml);
		}
	}
	//TODO kakoulist end

	
	
	
	/**
	 * 保存卡口list 洛阳数据
	 * 
	 * @param strCookie
	 * @param lyMotorList
	 * @param cf
	 */
	/*
	public static void saveLYChPList(String strCookie, List<LuoyangMotor> lyMotorList, Config cf) {

		if (lyMotorList != null && !lyMotorList.isEmpty()) {
			List<Object> motorlist = new ArrayList<Object>();
			int cnt = lyMotorList.size();
			LuoyangMotor lyMotor = null;
			ChPListMotor chPListMotor = null;
			for (int i = 0; i < cnt; i++) {

				lyMotor = lyMotorList.get(i);
//				String strDeviceID = LyConstant.addZeroForNum(lyMotor.getDeviceNo(), 32);
				String strDeviceID = lyMotor.getDeviceNo();
				if(strDeviceID == null || "".equals(strDeviceID)){
					log.warn("卡口LIST：洛阳过车数据中不存在设备ID记录!");
				}else{
					// 验证卡口编号，在卡口LIST中是否存在
					boolean flag = VCMAccess.queryChPointListByDeviceID(strCookie, strDeviceID,
							CheckPointStaticUtils.LUO_YANG, cf);
					if (!flag) {// 不存在，则新增一条记录
						chPListMotor = new ChPListMotor();
						String strID = CheckPointStaticUtils.getUUID();
						chPListMotor.setAutoID(strID);
						chPListMotor.setXTBH(lyMotor.getXTBH());
						chPListMotor.setDeviceID(strDeviceID);
						chPListMotor.setDeviceName(strDeviceID);
						chPListMotor.setPlaceName(lyMotor.getDeviceDesc());
						chPListMotor.setSourceType(String.valueOf(CheckPointStaticUtils.LUO_YANG));

						motorlist.add(chPListMotor);
					} else {
						log.warn("卡口LIST中存在记录：" + strDeviceID);
					}
				}
			}
			// 生成VCM需要的XML文件：创建元数据索引XML(VA_Create_Metadata_Index)
			if (motorlist != null && motorlist.size() > 0) {
				String strXml = VCMAccess.getMetadataIndexStr(cf.getStorageName(), cf.getTableNameChplist(), motorlist);
				VCMAccess.vcmVACreateMetadataIndex(strCookie, strXml);
			}
		}
	}
	*/

	/**
	 * 保存卡口list 宇视数据
	 * 
	 * @param strCookie
	 * @param lyMotorList
	 * @param cf
	 */
	public static void saveYSChPList(String strCookie, List<YuShiMotor> ysMotorList, Config cf) {

		if (ysMotorList != null && !ysMotorList.isEmpty()) {
			List<Object> motorlist = new ArrayList<Object>();
			int cnt = ysMotorList.size();
			YuShiMotor ysMotor = null;
			ChPListMotor chPListMotor = null;
			for (int i = 0; i < cnt; i++) {

				ysMotor = ysMotorList.get(i);
				String strDeviceID = ysMotor.getCamID();
				if(strDeviceID == null || "".equals(strDeviceID)){
					log.warn("卡口LIST：宇视过车数据中不存在设备ID(CamID)记录!");
				}else{
					// 验证卡口编号，在卡口LIST中是否存在
					boolean flag = VCMAccess.queryChPointListByDeviceID(strCookie, strDeviceID,
							CheckPointStaticUtils.YU_SHI, cf);
					if (!flag) {// 不存在，则新增一条记录
						chPListMotor = new ChPListMotor();
						String strID = CheckPointStaticUtils.getUUID();
						chPListMotor.setAutoID(strID);
						chPListMotor.setXTBH("-1");
						chPListMotor.setDeviceID(strDeviceID);
						// chPListMotor.setDeviceName();
						chPListMotor.setPlaceName(ysMotor.getPlaceName());
						chPListMotor.setSourceType(String.valueOf(CheckPointStaticUtils.YU_SHI));

						motorlist.add(chPListMotor);
					} else {
						log.warn("卡口LIST中存在记录：" + strDeviceID);
					}
				} 
			}
			// 生成VCM需要的XML文件：创建元数据索引XML(VA_Create_Metadata_Index)
			if (motorlist != null && motorlist.size() > 0) {
				String strXml = VCMAccess.getMetadataIndexStr(cf.getStorageName(), cf.getTableNameChplist(), motorlist);
				VCMAccess.vcmVACreateMetadataIndex(strCookie, strXml);
			}
		}

	}

}
