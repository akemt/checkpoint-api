package com.huawei.checkpoint.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.huawei.checkpoint.data.LuoyangMotor;
import com.huawei.checkpoint.data.YuShiImagesMotor;
import com.huawei.checkpoint.data.YuShiMotor;
import com.huawei.checkpoint.data.YuShiVideoMotor;

/**
 * xml转换成实体Bean,实体Bean转换成Xml
 * 
 * @author Xialf
 *
 */
public class XmlParser {

	private static Logger logger = Logger.getLogger(XmlParser.class);

	/**
	 * xml转换成实体Bean: 测试方法
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 创建SAXReader对象
		// SAXReader reader = new SAXReader();
		// // 读取文件 转换成Document
		// Document document = reader.read(new
		// File("src/xml/00100001_01_1_20050321101234023Z.xml"));

		// String docXmlText = document.asXML();
		// Object obj = xmlStrToBean(docXmlText, LuoyangMotor.class);
	}

	/**
	 * xml字符串转换成bean对象
	 * 
	 * @param xmlStr
	 *            xml字符串
	 * @param clazz
	 *            待转换的class
	 * @return 转换后的对象
	 */
	public static Object xmlStrToBean(Document dom, Class<?> clazz) {
		Object obj = null;
		try {
			// 将xml格式的数据转换成Map对象
			Map<String, Object> map = xmlStrToMap(dom);
			// 将map对象的数据转换成Bean对象
			obj = mapToBean(map, clazz);

		} catch (Exception e) {
			//e.printStackTrace();
			logger.warn(e);
		}
		return obj;
	}

	/**
	 * 将xml格式的字符串转换成Map对象
	 * 
	 * @param xmlStr
	 *            xml格式的字符串
	 * @return Map对象
	 * @throws Exception
	 *             异常
	 */
	public static Map<String, Object> xmlStrToMap(Document doc) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取data节点
		List<?> dataList = doc.getRootElement().element("Data").elements();
		// 循环所有子元素
		Element child = null;
		for (int j = 0; j < dataList.size(); j++) {
			child = (Element) dataList.get(j);
			if(child.elements().size()<1){
				map.put(child.getName(), child.getTextTrim());
			}
		}
		return map;
	}

	/**
	 * 将Map对象通过反射机制转换成Bean对象
	 * 
	 * @param map
	 *            存放数据的map对象
	 * @param clazz
	 *            待转换的class
	 * @return 转换后的Bean对象
	 * @throws Exception
	 *             异常
	 */
	public static Object mapToBean(Map<String, Object> map, Class<?> clazz) throws Exception {
		Object obj = clazz.newInstance();
		if (map != null && map.size() > 0) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String propertyName = entry.getKey();
				Object value = entry.getValue();
				boolean exists = existsField(clazz, propertyName);
				if (exists) {
					String setMethodName = "set" + propertyName;
					Field field = getClassField(clazz, propertyName);
					Class<?> fieldTypeClass = field.getType();
					value = convertValType(value, fieldTypeClass);
					clazz.getMethod(setMethodName, field.getType()).invoke(obj, value);
				}
			}
		}
		return obj;
	}

	/**
	 * 判断属性是否存在
	 * @param clz
	 * @param fieldName
	 * @return
	 */
	public static boolean existsField(Class<?> clz, String fieldName) {
		try {
			return clz.getDeclaredField(fieldName) != null;
		} catch (Exception e) {
		}
		if (clz != Object.class) {
			return existsField(clz.getSuperclass(), fieldName);
		}
		return false;
	}

	/**
	 * 将Object类型的值，转换成bean对象属性里对应的类型值
	 * 
	 * @param value
	 *            Object对象值
	 * @param fieldTypeClass
	 *            属性的类型
	 * @return 转换后的值
	 */
	private static Object convertValType(Object value, Class<?> fieldTypeClass) {
		Object retVal = null;
		retVal = value;
		// if (Long.class.getName().equals(fieldTypeClass.getName())
		// || long.class.getName().equals(fieldTypeClass.getName())) {
		// retVal = Long.parseLong(value.toString());
		// } else if (Integer.class.getName().equals(fieldTypeClass.getName())
		// || int.class.getName().equals(fieldTypeClass.getName())) {
		// retVal = Integer.parseInt(value.toString());
		// } else if (Float.class.getName().equals(fieldTypeClass.getName())
		// || float.class.getName().equals(fieldTypeClass.getName())) {
		// retVal = Float.parseFloat(value.toString());
		// } else if (Double.class.getName().equals(fieldTypeClass.getName())
		// || double.class.getName().equals(fieldTypeClass.getName())) {
		// retVal = Double.parseDouble(value.toString());
		// } else {
		// retVal = value;
		// }
		return retVal;
	}

	/**
	 * 获取指定字段名称查找在class中的对应的Field对象(包括查找父类)
	 * 
	 * @param clazz
	 *            指定的class
	 * @param fieldName
	 *            字段名称
	 * @return Field对象
	 */
	private static Field getClassField(Class<?> clazz, String fieldName) {
		if (Object.class.getName().equals(clazz.getName())) {
			return null;
		}
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getClassField(superClass, fieldName);
		}
		return null;
	}

	/**
	 * 洛阳处理图片数据
	 * 
	 * @param doc
	 * @param uu
	 * @return
	 * @throws Exception
	 */
	public static LuoyangMotor xmlStrToList(Document doc, LuoyangMotor uu) throws Exception {
		
		int num = Integer.parseInt(uu.getPicNum());
		if(num > 0) {
			Map<String, Object> map = xmlPicToMap(doc);
			ArrayList<byte[]> pic = new ArrayList<byte[]>();
			ArrayList<String> picName = new ArrayList<String>();
			byte[] by = null;
			if (map != null && map.size() > 0) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String propertyName = entry.getKey();
					String value = (String) entry.getValue();
					if (propertyName.contains("PicName")) {
						picName.add(value);
					} 
					//				else if (isBase64(value)) {
					//					by = Base64.getDecoder().decode(value);
					//					pic.add(by);
					//				} 
					else {
						//					by = value.getBytes();
						by = Base64.getDecoder().decode(value);
						pic.add(by);
					}
				}
				uu.setPic(pic);
				uu.setPicName(picName);
			}
		}
		return uu;
	}

	public static boolean isBase64(String base64Str) {
		Pattern tern = Pattern.compile(CheckPointStaticUtils.IS_BASE64_REGEX);
		Matcher mat = tern.matcher(base64Str);
		boolean isBase64 = mat.matches();
		return isBase64;
	}

	public static Map<String, Object> xmlPicToMap(Document doc) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取pic节点
		List<?> picList = doc.getRootElement().element("Data").element("Picture").elements();
		// 获取根节点下的所有元素
		Element child = null;
		// 循环所有子元素
		String value1;
		for (int k = 0; k < picList.size(); k++) {
			child = (Element) picList.get(k);
			//value1 = child.getTextTrim();
			//logger.info("pics value1 trim is :" + value1);
			value1 = child.getText();
			String value2 = value1.replaceAll("=", "0");
			//logger.info("pics value2 notrim is :" + value1);
			
			//for bokang,haikang \r\n
			String value = value2.replaceAll("\r|\n", "");
			//logger.info("pics is :" + value);
			map.put(child.getName(), value);
		}
		return map;
	}
	
	/**
	 * 宇视Xml转实体Bean
	 * 
	 * @param dom
	 * @param clazz
	 * @return
	 */
	public static YuShiMotor xmlStrToYSBean(Document dom, Class<?> clazz) {
		YuShiMotor yuShiMotor = null;
		YuShiImagesMotor imagesMotor = null;
		List<YuShiImagesMotor> imageMotorList = new ArrayList<YuShiImagesMotor>();

		try {
			Map<String, Object> map = xmlStrToYSMap(dom);
			yuShiMotor = (YuShiMotor) mapToBean(map, clazz);
			List<Map<String, Object>> imageList = xmlStrToYSImageMap(dom);
			int imageListSize = imageList.size();
			for (int i = 0; i < imageListSize; i++) {
				//图片信息
				imagesMotor = (YuShiImagesMotor) mapToBean(imageList.get(i), YuShiImagesMotor.class);
				imageMotorList.add(imagesMotor);
			}
			yuShiMotor.setImage(imageMotorList);
			yuShiMotor.setPicNumber(String.valueOf(imageListSize)); 
		} catch (Exception e) {
			//e.printStackTrace();
			logger.warn(e);
		}
		return yuShiMotor;
	}

	/**
	 * 宇视图片信息转List<Map>
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> xmlStrToYSImageMap(Document doc) throws Exception {

		List<Map<String, Object>> imageList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		// 获取data节点
		List<?> dataList = doc.getRootElement().elements("Image");
		// 循环所有子元素
		Element child = null;
		for (int j = 0; j < dataList.size(); j++) {
			map = new HashMap<String, Object>();
			List<?> list = ((Element) dataList.get(j)).elements();
			for (int i = 0; i < list.size(); i++) {
				child = (Element) list.get(i);
				map.put(child.getName(), child.getTextTrim());
			}
			imageList.add(map);
		}
		return imageList;
	}

	/**
	 * 宇视XML转Map
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> xmlStrToYSMap(Document doc) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取data节点
		List<?> dataList = doc.getRootElement().elements();
		// 循环所有子元素
		Element child = null;
		for (int j = 0; j < dataList.size(); j++) {
			child = (Element) dataList.get(j);
			int childSize = child.elements().size();
			if (childSize == 0) {
				map.put(child.getName(), child.getTextTrim());
			}
		}
		return map;
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
