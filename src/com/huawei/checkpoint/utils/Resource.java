package com.huawei.checkpoint.utils;

import java.io.*;

/**
 * 存放配置文件缓存
 * 
 * @author Xialf
 *
 */
public class Resource {
	public InputStream getResource(String path) throws IOException {
		// 返回读取指定资源的输入流
		InputStream is = this.getClass().getResourceAsStream(path);
		return is;
	}

}