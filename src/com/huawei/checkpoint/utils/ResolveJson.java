package com.huawei.checkpoint.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.data.YuShiImagesMotor;
import com.huawei.checkpoint.data.YuShiMotor;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JavaIdentifierTransformer;

/**
 * 解析JSON
 * 
 * @author Xialf
 *
 */
public class ResolveJson {

	private static Logger logger = Logger.getLogger(ResolveJson.class);

	private static JsonConfig jsonConfig = new JsonConfig();

	public static void main(String[] args) {
		setJsonConfigF();
		logger.warn("<<<<<<<<---------readFileToBean start------------");
		long begin = System.currentTimeMillis();
		// for (int i = 0; i < 1; i++) {
		YuShiMotor yuShiMotor = readFileToBean("src/json/JSON.json");
		// }
		yuShiMotor.getApplicationType();
		long end = System.currentTimeMillis() - begin;
		logger.warn("<<<<<<<<---------readFileToBean end------------");

	}

	/**
	 * json配置文件
	 */
	public static void setJsonConfigF() {
		jsonConfig.setJavaIdentifierTransformer(new JavaIdentifierTransformer() {
			@Override
			public String transformToJavaIdentifier(String str) {
				char[] chars = str.toCharArray();
				chars[0] = Character.toLowerCase(chars[0]);
				return new String(chars);
			}
		});
		jsonConfig.setRootClass(YuShiMotor.class);
		Map<String, Object> classMap = new HashMap<String, Object>();
		classMap.put("Image", YuShiImagesMotor.class);
		// classMap.put("VehicleFace", YuShiVehicleFaceMotor.class);
		jsonConfig.setClassMap(classMap);
	}

	/**
	 * 读取文件 转成实体bean
	 * 
	 * @param fileName
	 * @return
	 */
	public static YuShiMotor readFileToBean(String fileName) {
		try {
			String data = ReadFile(fileName);
			JSONObject jsonObject = JSONObject.fromObject(data).getJSONObject("Vehicle");
			YuShiMotor yuShiMotor = (YuShiMotor) JSONObject.toBean(jsonObject, jsonConfig);
//			return setMotor(yuShiMotor);
			return yuShiMotor;
		} catch (Exception e) {
			return new YuShiMotor();
		}
	}

	/**
	 * 读取指定路径文件
	 * @param Path
	 * @return
	 */
	public static String ReadFile(String Path) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(Path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			//e.printStackTrace();
			logger.warn("read excpetion: "+ Path,e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//e.printStackTrace();
					logger.warn("read close excpetion: "+ Path,e);
				}
			}
		}
		return laststr;
	}

	/**
	 * 对宇视时间信息进行format
	 * 
	 * @param yuShiMotor
	 * @return
	 */
	public static YuShiMotor setMotor(YuShiMotor yuShiMotor) {
		yuShiMotor.setPassTimeMill(setTimeMill(yuShiMotor.getPassTime()));
		yuShiMotor.setPassTime(setTime(yuShiMotor.getPassTime()));
		yuShiMotor.setRedLightStartTimeMill(setTimeMill(yuShiMotor.getRedLightStartTime()));
		yuShiMotor.setRedLightStartTime(setTime(yuShiMotor.getRedLightStartTime()));
		yuShiMotor.setRedLightEndTimeMill(setTimeMill(yuShiMotor.getRedLightEndTime()));
		yuShiMotor.setRedLightEndTime(setTime(yuShiMotor.getRedLightEndTime()));
		List<YuShiImagesMotor> image = yuShiMotor.getImage();
		for (int i = 0; i < image.size(); i++) {
			YuShiImagesMotor mo = image.get(i);
			mo.setPassTimeMill(setTimeMill(mo.getPassTime()));
			mo.setPassTime(setTime(mo.getPassTime()));
		}
		yuShiMotor.setImage(image);
		return yuShiMotor;
	}

	public static String setTime(String timeStr) {
		String timeReturn = "1111-11-11 11:11:11";
		SimpleDateFormat oritime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = oritime.parse(timeStr);
			timeReturn = time.format(date);
			return timeReturn;
		} catch (ParseException e) {
			return timeReturn;
		}
	}

	public static String setTimeMill(String timeStr) {

		String timeReturn = "-1";

		SimpleDateFormat oritime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat millTime = new SimpleDateFormat("SSS");
		try {
			Date date = oritime.parse(timeStr);
			timeReturn = millTime.format(date);
			return timeReturn;
		} catch (ParseException e) {
			return timeReturn;
		}
	}

}
