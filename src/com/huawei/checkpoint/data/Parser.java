package com.huawei.checkpoint.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
//import org.dom4j.io.XMLWriter;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.utils.CheckPointStaticUtils;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.ResolveJson;
import com.huawei.checkpoint.utils.VCMAccess;
import com.huawei.checkpoint.utils.XmlParser;

public class Parser {
	private SAXReader reader = new SAXReader();;
	private Document document;
	// private XMLWriter writer;
	private volatile static int cnt = 0;
	private ArrayList<String> filePath;
	private String fileName;
	private String filePathUrl;
	private int platType;
	private String sysType;

	private static Logger log = Logger.getLogger(Parser.class);

	public Parser() {
		cnt++;
//		log.warn("parser cnt>>>>>>:" + cnt);
	}
	public void init(ArrayList<String> path, int type) {
			filePath = path;
			platType = type;
//			filePathUrl = new File(path).getParent();
//			fileName = new File(path).getName();
			Config cf = Config.getIns();
			cf.getProperties();
			sysType = cf.getSysType();
	}
	public Parser(ArrayList<String> path, int type) {
		filePath = path;
		platType = type;
//		filePathUrl = new File(path).getParent();
//		fileName = new File(path).getName();
		Config cf = Config.getIns();
		cf.getProperties();
		sysType = cf.getSysType();

	}

	public Document getDocument() {
		return document;
	}

	public ArrayList<String> getPath() {
		return filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFilePathUrl() {
		return filePathUrl;
	}

	public int getType() {
		return platType;
	}

	public void close() {
		//
		if (sysType.compareTo("0") == 0) {
			// rm xml file here?

		} else {

		}
	}

	/**
	 * 公共方法： XML转换成实体Bean，实体Bean转换成XML
	 * 
	 * sysType = 0 sysType != 0
	 * 
	 * @param clazz
	 * @return
	 * @throws DocumentException
	 */
	public List<LuoyangMotor> parse(Class<?> clazz) {
//		log.warn("parse-------start------" + fileName);// 300ms
		List<LuoyangMotor> motList = new ArrayList<LuoyangMotor>();
		LuoyangMotor obj = null;
//		String pa = "src/resource/JGJ00001_00_1_20170421112347503Z.xml,src/resource/JGJ00001_00_1_20170421112347503Z12.xml";
//		filePath = pa.split(",");
		
		Hashtable<String, String> xtbhHT = SystemManager.getIns().getXTBHHT();
		int con = filePath.size();
		for (int i = 0; i < con; i++) {
			try {
				log.debug("parse-xml------start------" + filePath.get(i));
				document = reader.read(new File(filePath.get(i)));
				obj = (LuoyangMotor) XmlParser.xmlStrToBean(document, clazz);
				obj = XmlParser.xmlStrToList(document, obj);
				// 监控路径中存在配置文件中的值时，则修改其XTBH值
				if (xtbhHT.containsKey(new File(filePath.get(i)).getParent())) {
					obj.setXTBH(xtbhHT.get(new File(filePath.get(i)).getParent()));
				}
				motList.add(obj);
				log.debug("parse-xml------end------" + filePath.get(i));
			} catch (DocumentException e) {
				log.warn("文件已损坏", e);
			} catch (Exception e) {
				log.warn("parse file exception", e);
				// e.printStackTrace();
			}
		}
		
//		log.warn("parse------end-------" + fileName);
		return motList;
	}
	
	
	/**
	 * 公共方法： XML转换成实体Bean，实体Bean转换成XML
	 * 
	 * sysType = 0 sysType != 0
	 * 
	 * @param clazz
	 * @return
	 * @throws DocumentException
	 */
	public List<YuShiMotor> yuShiParse(Class<?> clazz) {
		List<YuShiMotor> motList = new ArrayList<YuShiMotor>();
		YuShiMotor yuShiMotor = null; 
		
			int con = filePath.size();
			for (int i = 0; i < con; i++) {
				try {
				log.debug("yuShiParse--XML-----start------" + filePath.get(i));
				if (CheckPointStaticUtils.isMatchesStr(filePath.get(i), CheckPointStaticUtils.STR_XML)) {
					document = reader.read(new File(filePath.get(i)));
					yuShiMotor = XmlParser.xmlStrToYSBean(document, YuShiMotor.class);
					motList.add(yuShiMotor);
				} else if (CheckPointStaticUtils.isMatchesStr(filePath.get(i), CheckPointStaticUtils.STR_JSON)) {
					ResolveJson.setJsonConfigF();
					yuShiMotor = ResolveJson.readFileToBean(filePath.get(i));
					motList.add(yuShiMotor);
				}
				log.debug("yuShiParse--XML-----end------" + filePath.get(i));
				} catch (DocumentException e) {
					log.warn("文件已损坏",e);
				} catch (Exception e) {
					log.warn("parse file exception",e);
					//e.printStackTrace();
				}
			}
		return motList;
	}
}
