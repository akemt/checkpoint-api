package com.huawei.checkpoint.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FtpClientUtil {
	private static Logger logger = Logger.getLogger(FtpClientUtil.class);
	
	private static ThreadLocal<FTPClient> ftpClientThreadLocal = new ThreadLocal<FTPClient>(); 

	public static synchronized FTPClient getFTPClient(String ftpHost, String ftpPassword, String ftpUserName, int ftpPort) {
		FTPClient ftpClient = ftpClientThreadLocal.get();
		if (ftpClient != null && ftpClient.isConnected() && validateObject(ftpClient)) {  
			logger.info("[FtpClientUtil] [getFTPClient - ftpClientThreadLocal] [success]");
            return ftpClientThreadLocal.get();  
        } else {
			try {
				ftpClient = new FTPClient();
				ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
				ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
				if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
					logger.warn("未连接到FTP，用户名或密码错误。");
					ftpClient.disconnect();
				} else {
					logger.info("[FtpClientUtil] [getFTPClient] [connect success]");
				}
				
				
			} catch (SocketException e) {
				
				//e.printStackTrace();
				logger.warn("FTP的IP地址可能错误，请正确配置。",e);
				ftpClient = null;
			} catch (IOException e) {
				//e.printStackTrace();
				logger.warn("FTP的端口错误,请正确配置。",e);
				ftpClient = null;
			}finally {
				ftpClientThreadLocal.set(ftpClient);
			}
        }
		return ftpClient;
	}
	
	public static boolean validateObject(FTPClient ftpClient) {
		try {
			return ftpClient.sendNoOp();
		} catch (IOException e) {
//			logger.warn("Failed to validate client");
			return false;
//			throw new RuntimeException("Failed to validate client: " + e, e);
		} 
	}
	
	private static ThreadLocal<FTPClient> targetftpClientThreadLocal = new ThreadLocal<FTPClient>(); 

	public static FTPClient getTargetFTPClient(String ftpHost, String ftpPassword, String ftpUserName, int ftpPort) {
		FTPClient ftpClient = null;
		if (targetftpClientThreadLocal.get() != null && targetftpClientThreadLocal.get().isConnected()) {  
			logger.info("[FtpClientUtil] [getFTPClient - targetftpClientThreadLocal] [success]");
            return targetftpClientThreadLocal.get();  
        } else {
			try {
				ftpClient = new FTPClient();
				ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
				ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
				if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
					logger.warn("未连接到目标FTP，用户名或密码错误。");
					ftpClient.disconnect();
				} else {
					logger.info("[FtpClientUtil] [getTargetFTPClient] [connect success]");
				}
				
				targetftpClientThreadLocal.set(ftpClient);
			} catch (SocketException e) {
				//e.printStackTrace();
				logger.warn("目标FTP的IP地址可能错误，请正确配置。",e);
			} catch (IOException e) {
				//e.printStackTrace();
				logger.warn("目标FTP的端口错误,请正确配置。",e);
			}
        }
		return ftpClient;
	}
	
	
	/**
	 * 遍历文件直接上传FTP
	 * 
	 * @param ftpClient
	 * @param fileFullNames
	 */
	public static void uploadFiles(FTPClient ftpClient,ArrayList<String> fileFullNames) {
		for(String str : fileFullNames){
			String strFile = str;
			uploadFtpByFile(ftpClient, strFile);
		}
//		try {
//			ftpClient.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 单个文件直接上传FTP
	 * 
	 * @param ftpClient
	 * @param fileFullNames
	 */
	public static void uploadFiles(FTPClient ftpClient,String fileFullNames) {
		try {
			uploadFtpByFile(ftpClient, fileFullNames);
		} catch (Exception e) {
			logger.warn("uploadfiles exception ,files :"+fileFullNames + " dest host:" +ftpClient.getRemoteAddress().getHostAddress(),e);
			//e.printStackTrace();
			
		}
//		try {
//			ftpClient.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 文件直接上传到目标FTP
	 * 
	 * @param ftpClient
	 * @param fileFullName
	 */
	public static void uploadFtpByFile(FTPClient ftpClient, String fileFullName) {
		// FTPClient ftpClient = null;
		File f = new File(fileFullName);
		if(ftpClient != null){
			logger.info("[FtpClientUtil] [uploadFtpByFile] [开始上传文件到FTP...]");
			InputStream in = null;
			// ftpClient = getFTPClient(ftpHost, ftpPassword, ftpUserName, ftpPort);
			try {
				// 设置PassiveMode传输
				ftpClient.enterLocalPassiveMode();
				// 设置以二进制流的方式传输
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	
				// 对远程目录的处理
				String remoteFileName = new File(fileFullName).getName();
				//一路FTP
//				String path = new File(fileFullName).getParent();
//				 
//				if (path.indexOf("/")!= -1){//have
//					int i = path.lastIndexOf("/");
//					String newPath = path.substring(i+1, path.length()); 
//					ftpClient.changeWorkingDirectory(newPath);
//				} 
	
				// if (remoteFileName.contains(CheckPointStaticUtils.FORWARD_SLASH)) {
				// remoteFileName =
				// remoteFileName.substring(remoteFileName.lastIndexOf(CheckPointStaticUtils.FORWARD_SLASH)
				// + 1);
				// }
				// FTPFile[] files = ftpClient.listFiles(new
				// String(remoteFileName));
				// 先把文件写在本地。在上传到FTP上最后在删除
				// InputStream in = new ByteArrayInputStream(fileContent.getBytes());
				// boolean writeResult = write(remoteFileName, fileContent,
				// writeTempFielPath);
				// if (writeResult) {
				in = new FileInputStream(f);
				boolean flag = FTPClientWraper.storeFile(ftpClient,remoteFileName, in);
	
				if (flag) {
					logger.info("[FtpClientUtil] [uploadFtpByFile] [上传文件到FTP成功!]");
					logger.debug("上传文件:" + fileFullName +"to:"+ftpClient.getRemoteAddress().getHostAddress() +":" +remoteFileName + "成功!");
				} else {
					logger.info("[FtpClientUtil] [uploadFtpByFile] [上传文件到FTP失败!]");
					logger.warn("上传文件:" + fileFullName +"to:"+ftpClient.getRemoteAddress().getHostAddress() +":" +remoteFileName + "失败!");
				}
			} catch (IOException e) { 
				//f.delete();
				//e.printStackTrace();
				logger.warn("上传文件:" + fileFullName +"to:"+ftpClient.getRemoteAddress().getHostAddress()  + "excption!", e);
			}finally{
				//try {
					if(in != null) {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							logger.warn("上传文件:" + fileFullName +"to:"+ftpClient.getRemoteAddress().getHostAddress()  + "close excption!", e);
						}
					}
					if(f != null) {
						f.delete();
					}
					//ftpClient.changeWorkingDirectory("..");
				//} catch (IOException e) {
				//	e.printStackTrace();
				//}
			}
			
		}else{
			logger.warn("上传文件-FTPClient is null,file " + fileFullName); 
			if(f != null) {
				f.delete();
			}
		}
	}

	
	/**
	 * 遍历XML上传FTP
	 * 
	 * @param ftpClient
	 * @param fileFullNames
	 */
	public static void uploadFtpByXmlLists(FTPClient ftpClient, ArrayList<Map<String,String>> fileList) {
		Map<String,String> map = null;
		for (int i = 0; i < fileList.size(); i++) {
			map = (Map<String,String>) fileList.get(i);
			try {
				uploadFtpByXml(ftpClient, map.get("strFile").toString(), map.get("strXml").toString());
			} catch (Exception e) {
				logger.warn("上传文件:" + map.get("strFile").toString() + " " +map.get("strXml").toString() +"to:"+ftpClient.getRemoteAddress().getHostAddress()  + "uploadFtpByXml excption!", e);
				//e.printStackTrace();
			}
		}
		// try {
		// ftpClient.disconnect();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}
	
	/**
	 * 
	 * 读取XML上传到目标FTP
	 * 
	 * @param ftpClient
	 * @param fileFullName
	 * @param fileContent
	 * @throws IOException 
	 */
	public static void uploadFtpByXml(FTPClient ftpClient, String fileFullName, String fileContent) throws IOException {
		logger.info("开始上传文件到FTP.");
		// 设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();
		// 设置以二进制流的方式传输
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		// 对远程目录的处理
		String remoteFileName = new File(fileFullName).getName(); 
		// 先把文件写在本地。在上传到FTP上最后在删除
		InputStream in = new ByteArrayInputStream(fileContent.getBytes()); 
		boolean flag = ftpClient.storeFile(remoteFileName, in);
		if (flag) {
			in.close();
			logger.info("上传文件" + remoteFileName + "到FTP成功!");
		} else {
			logger.info("上传文件" + remoteFileName + "到FTP失败!");
		} 
	}
	
	public static boolean write(String fileName, String fileContext, String writeTempFielPath) {
		try {
			logger.info("开始写配置文件");
			File f = new File(writeTempFielPath + CheckPointStaticUtils.FORWARD_SLASH + fileName);
			if (!f.exists()) {
				if (!f.createNewFile()) {
					logger.warn("文件不存在，创建失败! " +fileName);
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(fileContext.replaceAll("\n", "\r\n"));
			bw.flush();
			bw.close();
			return true;
		} catch (Exception e) {
			logger.error("写文件没有成功:" +fileName ,e);
			//e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 遍历读取文件转成XML
	 * 
	 * @param ftpClient
	 * @param fileFullNames
	 */
	public static ArrayList<Map<String,String>> readXmlFiles(FTPClient ftpClient,ArrayList<String> fileFullNames) {
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		for(String str : fileFullNames){
			try {
			String strFile = str;
			String strXml = readConfigFileForFTP(ftpClient, strFile);
			Map<String,String> map = new HashMap<String,String>();
			map.put("strFile", strFile);
			map.put("strXml", strXml);
			list.add(map);
			} catch (Exception e) {
				logger.error("readConfigFileForFTP:" +str ,e);
			}
		}
//		try {
//			ftpClient.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return list;
	}

	/**
	 ** 去 服务器的FTP路径下上读取文件 *
	 * 
	 * @param ftpUserName
	 * @param ftpPassword
	 * @param ftpPath
	 * @param FTPServer
	 * @return
	 */
	public static String readConfigFileForFTP(FTPClient ftpClient,String filePathUrl) {
		StringBuffer resultBuffer = new StringBuffer();
		InputStream in = null; 
		String strPath = getFirstSubStr(filePathUrl, 1);
		String fileName = getSubStr(filePathUrl, 2);
		
		logger.info("开始读取绝对路径" + filePathUrl + "文件!");
		try { 
			ftpClient.setControlEncoding("gbk"); // 中文支持
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.changeWorkingDirectory(strPath);
			in = ftpClient.retrieveFileStream(fileName);
		} catch (FileNotFoundException e) {
			logger.error("没有找到" + filePathUrl + "文件",e);
			//e.printStackTrace();
			return "下载配置文件失败，请联系管理员.";
		} catch (SocketException e) {
			logger.error("连接FTP失败.",e);
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error("文件读取错误。",e);
			//e.printStackTrace();
			return "配置文件读取失败，请联系管理员.";
		}
		if (in != null) {
			String data = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader((in), "gbk"));
				while ((data = br.readLine()) != null) {
					resultBuffer.append(data + "\n");
				}
				in.close(); 
			} catch (IOException e) {
				logger.error("文件读取错误。",e);
				//e.printStackTrace();
				return "配置文件读取失败，请联系管理员.";
			} 
			finally {
				try {
					ftpClient.completePendingCommand();//more
					ftpClient.deleteFile(fileName);
				} catch (IOException e) {
					//e.printStackTrace();
					logger.error("deleteFile",e);
				}
			}
		} else {
			logger.error("in为空，不能读取。");
			return "配置文件读取失败，请联系管理员.";
		}
		return resultBuffer.toString();
	}
	
	/**
	 * 截取第num个 包含"/"的字符串
	 * 
	 * @param str
	 * @param num
	 * @return
	 */
	private static String getFirstSubStr(String str, int num) {
		String result = "";
		int i = 0;
		while (i < num) {
			int lastFirst = str.lastIndexOf('/');
			str = str.substring(0, lastFirst);
			result = str.substring(0,str.lastIndexOf('/')) ;
			i++;
		}
		return result.substring(1);
	}
	
	/**
	 * 截取倒数第num个 包含"/"的字符串
	 * 
	 * @param str
	 * @param num
	 * @return
	 */
	private static String getSubStr(String str, int num) {
		String result = "";
		int i = 0;
		while (i < num) {
			int lastFirst = str.lastIndexOf('/');
			result = str.substring(lastFirst) + result;
			str = str.substring(0, lastFirst);
			i++;
		}
		return result.substring(1);
	}

//	public static void main(String[] args) {
//		FTPClient lyftpClient = FtpClientUtil.getFTPClient("10.1.26.80","xialianfu", "xialf",  21);
//		readConfigFileForFTP(lyftpClient,"/home/xialf/test2/7.xml");
		 
//		String str = "/home/xialf/test1/vcm_20170510001.xml";
//		System.out.println(getFirstSubStr(str, 1));
//	}
}
