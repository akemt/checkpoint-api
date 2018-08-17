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

/**
 * 车道List
 * @author Xialf
 *
 */
public class LaneListConfig {

	private Logger log = Logger.getLogger(LaneListConfig.class);

	private JSONArray jsonAll = new JSONArray();
	
	private LaneListConfig() {
		initFlag = false;
	}

	private static volatile LaneListConfig instance;

	private boolean initFlag;

	public static LaneListConfig getIns() {
		if (instance == null)
			synchronized (LaneListConfig.class) {
				if (instance == null)
					instance = new LaneListConfig();
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
			 configFile = System.getProperty("com.huawei.checkpoint.utils.lanelist");
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
			log.error("getlanelist config exception:"+configFile, e);
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
	public String getLCGAList() {
		String str = configs.get("lcga-list");
		return str;
	}
	
	/**
	 * 栾川 List
	 * 
	 * @return
	 */
	public String getLCJJList() {
		String str = configs.get("lcjj-list");
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
		if(yyJson.size()>0){
			this.jsonFormate(yyJson,jsonAll);
		}
		
		JSONArray ycJson = JSONArray.fromObject(getYCList());
		if(ycJson.size()>0){
			this.jsonFormate(ycJson,jsonAll);
		}
		 
		JSONArray ybJson = JSONArray.fromObject(getYBList());
		if(ybJson.size()>0){
			this.jsonFormate(ybJson,jsonAll);
		}

		JSONArray xaJson = JSONArray.fromObject(getXAList());
		if(xaJson.size()>0){
			this.jsonFormate(xaJson,jsonAll);
		}

		JSONArray sxJson = JSONArray.fromObject(getSXList());
		if(sxJson.size()>0){
			this.jsonFormate(sxJson,jsonAll);
		}

		JSONArray ryJson = JSONArray.fromObject(getRYList());
		if(ryJson.size()>0){
			this.jsonFormate(ryJson,jsonAll);
		}

		JSONArray mjJson = JSONArray.fromObject(getMJList());
		if(mjJson.size()>0){
			this.jsonFormate(mjJson,jsonAll);
		}

		JSONArray lysJson = JSONArray.fromObject(getLYSList());
		if(lysJson.size()>0){
			this.jsonFormate(lysJson,jsonAll);
		}

		JSONArray lnJson = JSONArray.fromObject(getLNList());
		if(lnJson.size()>0){
			this.jsonFormate(lnJson,jsonAll);
		}

		JSONArray lcgaJson = JSONArray.fromObject(getLCGAList());
		if(lcgaJson.size()>0){
			this.jsonFormate(lcgaJson,jsonAll);
		}
		
		JSONArray lcjjJson = JSONArray.fromObject(getLCJJList());
		if(lcjjJson.size()>0){
			this.jsonFormate(lcjjJson,jsonAll);
		}

		JSONArray jlJson = JSONArray.fromObject(getJLList());
		if(jlJson.size()>0){
			this.jsonFormate(jlJson,jsonAll);
		}

		JSONArray jjzdJson = JSONArray.fromObject(getJJZDList());
		if(jjzdJson.size()>0){
			this.jsonFormate(jjzdJson,jsonAll);
		}

		JSONArray ysjJson = JSONArray.fromObject(getYSList());
		if(ysjJson.size()>0){
			this.jsonFormate(ysjJson,jsonAll);
		}
		
		JSONArray gsjjJson = JSONArray.fromObject(getGSJJList());
		if(gsjjJson.size()>0){
			this.jsonFormate(gsjjJson,jsonAll);
		}
		 
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
//				kk2TollGate.put(jsn.get("kkid").toString(), jsn.get("Tollgate").toString());
				jsnNew.put("TollgateID", jsn.get("Tollgate"));
				jsnNew.put("LaneId", jsn.get("LaneId").toString());//车道ID
				jsnNew.put("LaneNo", jsn.get("LaneNo").toString());//车道编号
				jsnNew.put("Name", jsn.get("Name"));
				jsnNew.put("Direction", jsn.get("Direction"));//车道方向:01 东向西，02 西向东，03 南向北，04 北向南，05 东北到西南，06 西南到东北，07 东南到西北，08 西北到东南，00 其他
				jsnNew.put("CityPass", jsn.get("CityPass").toString());//车道出入城:01 进城、02出城、03非进出城
				jsnNew.put("DeviceID", jsn.get("kkid").toString());//设备ID
				jTollgateList.add(jsnNew);
			}
		}
		
		return jTollgateList;
	} 
	
}
