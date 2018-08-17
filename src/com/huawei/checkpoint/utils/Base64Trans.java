package com.huawei.checkpoint.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.apache.log4j.Logger;

public class Base64Trans {

	private static Logger log = Logger.getLogger(Base64Trans.class);

    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     * @return
     */
    public static String getImageStr(String Imagepath) {
    	Path path = Paths.get(Imagepath);

        byte[] imageContents = null;
            try {
				imageContents = Files.readAllBytes(path);
			} catch (IOException e) {
	            //System.out.println("读取文件出错");
				//e.printStackTrace();
				log.warn("getImageStr exception:"+Imagepath, e);
			}

            String encodingString= Base64.getEncoder().encodeToString(imageContents);

            //System.out.println("图片文件Base64码：" + encodingString);
    	return encodingString;
    }

    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     * @return
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     */
	public static byte[] generateImage(String encodingString) {
		

		byte[] imageContents = Base64.getDecoder().decode(encodingString);

		log.debug("base64编码字符串转换为byte[]：" + encodingString);

		SimpleDateFormat sdftime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String pho = sdftime.format(new Date());
		String Imagepath = "E:/photo-" + pho + ".jpg";

		Path path = Paths.get(Imagepath);
		try {
			Files.write(path, imageContents, StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.out.println("写入文件出错");
		}
		return imageContents;
	}
	

	/**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     * @return
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     */
	public static byte[] generateImagebyByte(byte[] encodingByte) {


		log.debug("base64编码字符串转换为byte[]：" + encodingByte);

		SimpleDateFormat sdftime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String pho = sdftime.format(new Date());
//		String Imagepath = "E:/yspic/photo-" + pho + ".jpg";
		String Imagepath = "/root/xialf/photo/"+ pho + ".jpg";

		Path path = Paths.get(Imagepath);
		try {
			Files.write(path, encodingByte, StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.out.println("写入文件出错");
		}
		return encodingByte;
	}
}
