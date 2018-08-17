package com.huawei.checkpoint.utils;
/**  
* 实现对Java配置文件Properties的读取、写入与更新操作  
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
  

/**
 * @author
 * @version
 */
public class SetSystemProperty {
	
	// 属性文件的路径
	static String profilepath = "com.huawei.checkpoint.utils.config";
	 
	/**
	 * 采用静态方法
	 */
	private static Properties props = new Properties();
	static {
		try {
//			Resource res = new Resource();
//			InputStream in = res.getResource(profilepath);
			String configFile = System.getProperty(profilepath);
			File f = new File(configFile);
			InputStream in = new FileInputStream(f);
			props.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			System.exit(-1);
		}
	}

	/**
	 * 读取属性文件中相应键的值
	 * 
	 * @param key
	 *            主键
	 * @return String
	 */
	public static String getKeyValue(String key) {
		return props.getProperty(key);
	}

	/**
	 * 根据主键key读取主键的值value
	 * 
	 * @param filePath
	 *            属性文件路径
	 * @param key
	 *            键名
	 */
	public static String readValue(String key,String strYORN) {
		Properties props = new Properties();
		try {
//			Resource res = new Resource();
//			InputStream in = res.getResource(profilepath);
			String configFile = System.getProperty(profilepath);
			File f = new File(configFile);
			InputStream in = new FileInputStream(f);
			props.load(in);
			String value = props.getProperty(key);
			if(key.equals("vcn-pass")||key.equals("vcm-pass")||key.equals("pp-pass")||key.equals("lyftp-pass")){
				if(strYORN.equals("Y")){
					System.out.println(getValueByKey(key) + " ：*****************");
					System.out.println("Please Write Y/N!");
				}else{
					System.out.println(getValueByKey(key) + " ：请输入新密码！");
				}
			}else{
				System.out.println(getValueByKey(key) + " ：" + value);
				System.out.println("Please Write Y/N!");
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新（或插入）一对properties信息(主键及其键值) 如果该主键已经存在，更新该主键的值； 如果该主键不存在，则插件一对键值。
	 * 
	 * @param keyname
	 *            键名
	 * @param keyvalue
	 *            键值
	 */
	public static void writeProperties(String keyname, String keyvalue) { 
		
		try {
			// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
//			String strPath = Thread.currentThread().getContextClassLoader().getResource("config.properties").getPath();
			String configFile = System.getProperty(profilepath);
			File f = new File(configFile); 
			
			OutputStream fos = new FileOutputStream(f);
			if(keyname.equals("vcn-pass")||keyname.equals("vcm-pass")||keyname.equals("pp-pass")||keyname.equals("lyftp-pass")){
				try {
					DesUtils des = new DesUtils();

					String reValue = des.encrypt(keyvalue);
					reValue = reValue.trim().intern();
					props.setProperty(keyname, reValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				props.setProperty(keyname, keyvalue);
			}
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			props.store(fos, "Update '" + keyname + "' value");
		} catch (IOException e) {
			System.err.println("属性文件更新错误");
		} 
	}

	public static String getValueByKey(String strKey) {
		Map<String, String> map = new HashMap<String, String>();

		map.put("vcn-usr", "VCN用户名");
		map.put("vcn-pass", "VCN密码");
		map.put("vcm-usr", "VCM用户名");
		map.put("vcm-pass", "VCM密码");
		map.put("pp-usr", "公安用户名");
		map.put("pp-pass", "公安密码");
		map.put("lyftp-usr", "FTP用户名");
		map.put("lyftp-pass", "FTP密码");

		return map.get(strKey).toString();
	}

	/**
	 * 控制台输入配置文件信息
	 */
	public static void printMessage(){

		String strUsr = "vcn-usr,vcn-pass,vcm-usr,vcm-pass,pp-usr,pp-pass,lyftp-usr,lyftp-pass,";
		String[] dataUsr = strUsr.split(",");
		
		String strPass = "vcn-usr,vcn-pass,vcm-usr,vcm-pass,pp-usr,pp-pass,lyftp-usr,lyftp-pass,";
		String[] dataPass = strPass.split(",");
		int num = dataUsr.length + 1;
		System.out.println("请输入卡口配置信息：");
		String strYORN = "";
		for (int i = 1; i < num; i++) {
			String iUsr = String.valueOf(dataUsr[i - 1]);
			readValue(iUsr,strYORN);
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			if (line.equals("y") || line.equals("Y")) { 
				strYORN = "Y";
				continue;
			} else if (line.equals("n") || line.equals("N")) {
				strYORN = "N"; 
				System.out.println("请输入"+getValueByKey(iUsr));
				String strData = scanner.nextLine();
				writeProperties(iUsr, strData);
				if(i < 8){
					String iPass = String.valueOf(dataPass[i]);
					if(iPass.equals("vcn-pass")||iPass.equals("vcm-pass")||iPass.equals("pp-pass")||iPass.equals("lyftp-pass")){
						readValue(iPass,strYORN);
						String passData = scanner.nextLine();
						writeProperties(iPass, passData);
						i++;
					}
				}
			} else {  
				i = i - 1;
			} 
		}

	}
	
	// 测试代码
	public static void main(String[] args) {

		String strUsr = "vcn-usr,vcn-pass,vcm-usr,vcm-pass,pp-usr,pp-pass,lyftp-usr,lyftp-pass,";
		String[] dataUsr = strUsr.split(",");
		
		String strPass = "vcn-usr,vcn-pass,vcm-usr,vcm-pass,pp-usr,pp-pass,lyftp-usr,lyftp-pass,";
		String[] dataPass = strPass.split(",");
		int num = dataUsr.length + 1;
		System.out.println("请输入卡口配置信息：");
		String strYORN = "";
		for (int i = 1; i < num; i++) {
			String iUsr = String.valueOf(dataUsr[i - 1]);
			readValue(iUsr,strYORN);
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			if (line.equals("y") || line.equals("Y")) { 
				strYORN = "Y";
				continue;
			} else if (line.equals("n") || line.equals("N")) {
				strYORN = "N"; 
				System.out.println("请输入"+getValueByKey(iUsr));
				String strData = scanner.nextLine();
				writeProperties(iUsr, strData);
				if(i < 8){
					String iPass = String.valueOf(dataPass[i]);
					if(iPass.equals("vcn-pass")||iPass.equals("vcm-pass")||iPass.equals("pp-pass")||iPass.equals("lyftp-pass")){
						readValue(iPass,strYORN);
						String passData = scanner.nextLine();
						writeProperties(iPass, passData);
						i++;
					}
				}
			} else {  
				i = i - 1;
			} 
		}

	}
}