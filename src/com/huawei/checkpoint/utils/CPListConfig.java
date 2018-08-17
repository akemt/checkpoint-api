package com.huawei.checkpoint.utils; 
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.huawei.checkpoint.data.FTPClientConfigure;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CPListConfig {

	private Logger log = Logger.getLogger(CPListConfig.class);

	private JSONArray jsonAll = new JSONArray();
	
	private CPListConfig() {
		initFlag = false;
	}

	private static volatile CPListConfig instance;

	private boolean initFlag;

	public static CPListConfig getIns() {
		if (instance == null)
			synchronized (CPListConfig.class) {
				if (instance == null)
					instance = new CPListConfig();
			}
		return instance;
	}

	private Map<String, String> configs;
	
	private Map<String, String> kk2TollGate =  new HashMap<String, String>();;

	@SuppressWarnings("rawtypes")
	public void getProperties() {

		if (initFlag)
			return;

		initFlag = true;

		Properties props = new Properties();
		configs = new HashMap<String, String>();
		String configFile = null;
		try {
			// log.info("config.properties ：" +
			// getClass().getResource("/config.properties"));
//			InputStream in = getClass().getResourceAsStream("/st-config.properties");
			 configFile = System.getProperty("com.huawei.checkpoint.utils.chplist");
			 File dir = new File(configFile);
			 
			 File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		        if (files != null) {
		            for (int i = 0; i < files.length; i++) {
		                String fileName = files[i].getName();
		                if (files[i].isDirectory()) { // 判断是文件还是文件夹
//		                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
		                } else if (fileName.endsWith("properties")) { // 判断文件名是否以.avi结尾
		                    String strFileName = files[i].getAbsolutePath();
//		                    filelist.add(files[i]);
		       			 	InputStream in = new FileInputStream(strFileName);
		       			 	props.load(new InputStreamReader(in, "UTF-8"));
		                } else {
		                    continue;
		                }
		            }

		        }
			 
			Enumeration en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String property = props.getProperty(key);
				if (property != null && !property.equals("")) {
					configs.put(key, property.trim());
				} else {
					configs.put(key, property);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			log.error("getchplist config exception:"+configFile, e);
		}

	}


	/**
	 * 高速交警 List
	 * 
	 * @return
	 */
	public String getGSJJList() {
		String str = configs.get("gsjj-list");
		return str;
	}
	/**
	 * 交警支队 List
	 * 
	 * @return
	 */
	public String getJJZDList() {
		String str = configs.get("jjzd-list");
		return str;
	}
	/**
	 * 吉利 List
	 * 
	 * @return
	 */
	public String getJLList() {
		String str = configs.get("jl-list");
		return str;
	}
	/**
	 * 栾川 List
	 * 
	 * @return
	 */
	public String getLCList() {
		String str = configs.get("lc-list");
		return str;
	}
	/**
	 * 洛宁 List
	 * 
	 * @return
	 */
	public String getLNList() {
		String str = configs.get("ln-list");
		return str;
	}
	/**
	 * 洛阳市 List
	 * 
	 * @return
	 */
	public String getLYSList() {
		String str = configs.get("lys-list");
		return str;
	}
	/**
	 * 孟津 List
	 * 
	 * @return
	 */
	public String getMJList() {
		String str = configs.get("mj-list");
		return str;
	}
	/**
	 * 汝阳 List
	 * 
	 * @return
	 */
	public String getRYList() {
		String str = configs.get("ry-list");
		return str;
	}
	/**
	 * 嵩县 List
	 * 
	 * @return
	 */
	public String getSXList() {
		String str = configs.get("sx-list");
		return str;
	}
	/**
	 * 新安 List
	 * 
	 * @return
	 */
	public String getXAList() {
		String str = configs.get("xa-list");
		return str;
	}
	/**
	 * 伊滨 List
	 * 
	 * @return
	 */
	public String getYBList() {
		String str = configs.get("yb-list");
		return str;
	}
	/**
	 * 伊川 List
	 * 
	 * @return
	 */
	public String getYCList() {
		String str = configs.get("yc-list");
		return str;
	}
	/**
	 * 偃师 List
	 * 
	 * @return
	 */
	public String getYSList() {
		String str = configs.get("ys-list");
		return str;
	} 

	/**
	 * 宜阳 List
	 * 
	 * @return
	 */
	public String getYYList() {
		String str = configs.get("yy-list");
		return str;
	}
	 
	public JSONArray getALLList() {
		if (!initFlag){
			intAllList();
		}

		
		return jsonAll;
	}
	
	public String getTollGateID(String kkID){
		if(kkID.length() > 0) {
			if(kk2TollGate.containsKey(kkID)) {
				return kk2TollGate.get(kkID);
			}else{
				log.warn("TollGateID failed:"+kkID);
				return kkID;
			}
		}
		else{
			log.warn("TollGateID failed for lenth = 0");
			return kkID;
		}
	}
	/**
	 * 所有市县LIst
	 * @return
	 */
	public void intAllList() {
		
		getProperties();
		
		JSONArray yyJson = JSONArray.fromObject(getYYList());
		this.jsonFormate(yyJson,jsonAll);
		
		JSONArray ycJson = JSONArray.fromObject(getYCList());
		this.jsonFormate(ycJson,jsonAll);

		JSONArray ybJson = JSONArray.fromObject(getYBList());
		this.jsonFormate(ybJson,jsonAll);

		JSONArray xaJson = JSONArray.fromObject(getXAList());
		this.jsonFormate(xaJson,jsonAll);

		JSONArray sxJson = JSONArray.fromObject(getSXList());
		this.jsonFormate(sxJson,jsonAll);

		JSONArray ryJson = JSONArray.fromObject(getRYList());
		this.jsonFormate(ryJson,jsonAll);

		JSONArray mjJson = JSONArray.fromObject(getMJList());
		this.jsonFormate(mjJson,jsonAll);

		JSONArray lysJson = JSONArray.fromObject(getLYSList());
		this.jsonFormate(lysJson,jsonAll);

		JSONArray lnJson = JSONArray.fromObject(getLNList());
		this.jsonFormate(lnJson,jsonAll);

		JSONArray lcJson = JSONArray.fromObject(getLCList());
		this.jsonFormate(lcJson,jsonAll);

		JSONArray jlJson = JSONArray.fromObject(getJLList());
		this.jsonFormate(jlJson,jsonAll);

		JSONArray jjzdJson = JSONArray.fromObject(getJJZDList());
		this.jsonFormate(jjzdJson,jsonAll);

		JSONArray ysjJson = JSONArray.fromObject(getYSList());
		this.jsonFormate(ysjJson,jsonAll);
		
		JSONArray gsjjJson = JSONArray.fromObject(getGSJJList());
		this.jsonFormate(gsjjJson,jsonAll);
		 
		//JSONObject jobj = new JSONObject();
		//jobj.put("TollgateList", jsonAll);
		return ;
	}
	
	
	public JSONArray jsonFormate(JSONArray json,JSONArray jTollgateList) {
		int jNum = json.size();
		//String zeroKKID = "000000000000000000000000";
		if (jNum > 0) { 
			for (int i = 0; i < jNum; i++) {
				JSONObject jsn = json.getJSONObject(i); 
				JSONObject jsnNew = new JSONObject();
				
				//kk2TollGate.put(zeroKKID+jsn.get("kkid").toString(), jsn.get("Tollgate").toString());
				kk2TollGate.put(jsn.get("kkid").toString(), jsn.get("Tollgate").toString());
				jsnNew.put("TollgateID", jsn.get("Tollgate"));
				jsnNew.put("Name", jsn.get("placename"));
				jsnNew.put("Longtidude", "");
				jsnNew.put("Latitude", "");
				jsnNew.put("PlaceCode", jsn.get("placecode"));
				jsnNew.put("Place", "");
				jsnNew.put("Status", "1");
				jsnNew.put("TollgateType", "81");
				jsnNew.put("LaneNum", jsn.get("lanenum"));
				jsnNew.put("OrgCode", "410300000000");
				jsnNew.put("ActiveTime", "");
				jTollgateList.add(jsnNew);
			}
		}
		
		return jTollgateList;
	} 
	
}
