package com.huawei.checkpoint.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
 
import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger; 

/**
 * HTTPClient请求
 * 
 * @author Xialf
 *
 */
public class HttpClientUtil {
	
	private  Logger log = Logger.getLogger(HttpClientUtil.class);
	// 无需证书
	private static CloseableHttpClient mHttpClient = null;
	private final static Object syncLock = new Object();
	private static CloseableHttpClient mShengClient = null;
	private final static Object syncShengLock = new Object();
	
	public CloseableHttpClient createSSLClientDefault() throws Exception {
		return createSSLClient(0);
	}
	public CloseableHttpClient createSSLClient(int type ) throws Exception {
		
		if(type !=0 && type !=1) {
			return null;
		}
		
		if (type == 0 && mHttpClient == null) {
            synchronized (syncLock) {
            	if (mHttpClient == null) {
            		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
            			@Override
            			// 信任所有
            			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            				return true;
            			}
            		}).build();

            		LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            		Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf)
            				.build();

            		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
            		int num = Config.getIns().getVCMConnectPoolMax();
            		log.warn("http client init-pool-num :"+num);
            		log.warn("http client get init-pool-num pre:"+cm.getMaxTotal()+":"+cm.getDefaultMaxPerRoute());
            		cm.setMaxTotal(num);
            		log.warn("http client get init-pool-num mid:"+cm.getMaxTotal()+":"+cm.getDefaultMaxPerRoute());
            		cm.setDefaultMaxPerRoute(num);
            		log.warn("http client get init-pool-num after:"+cm.getMaxTotal()+":"+cm.getDefaultMaxPerRoute());

            		@SuppressWarnings("deprecation")
            		RequestConfig defaultRequestConfig = RequestConfig.custom()
            		.setSocketTimeout(Config.getIns().getVCMSocketTimeout())
            		.setConnectTimeout(Config.getIns().getVCMConnectTimeout())
            		.setConnectionRequestTimeout(5000)
            		.setStaleConnectionCheckEnabled(true)
            		.build();

            		mHttpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(defaultRequestConfig).build();

            	}
            }
        }
		if (type == 1 && mShengClient == null) {
            synchronized (syncShengLock) {
            	if (mShengClient == null) {
            		
            		Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
            				.build();

            		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
            		int num = Config.getIns().getShengConnectPoolMax();
            		log.warn("sheng client init-pool-num :"+num);

            		log.warn("sheng client get init-pool-num pre:"+cm.getMaxTotal()+":"+cm.getDefaultMaxPerRoute());
            		cm.setMaxTotal(num);

            		log.warn("sheng client get init-pool-num mid:"+cm.getMaxTotal()+":"+cm.getDefaultMaxPerRoute());

            		cm.setDefaultMaxPerRoute(num);

            		log.warn("sheng"+cm.getMaxTotal()+":"+cm.getDefaultMaxPerRoute());

            		@SuppressWarnings("deprecation")
            		RequestConfig defaultRequestConfig = RequestConfig.custom()
            		.setSocketTimeout(Config.getIns().getShengSocketTimeout())
            		.setConnectTimeout(Config.getIns().getShengConnectTimeout())
            		.setConnectionRequestTimeout(5000)
            		.setStaleConnectionCheckEnabled(true)
            		.build();

            		mShengClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(defaultRequestConfig).build();

            	}
            }
        }
		
		return type == 0 ? mHttpClient:mShengClient;
	}

	// 指定证书
	public CloseableHttpClient createSSLClientWithKeystore() throws Exception {
		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new File("D:\\keys\\wsriakey"),
				"123456".toCharArray(), new TrustSelfSignedStrategy()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		return httpclient;
	}

	// login
	@SuppressWarnings("rawtypes") 
	public String MNT_Login(String url, Map<String, String> map, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = createSSLClientDefault();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("unchecked")
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.addHeader("Connection", "Keep-Alive");
				// Header header = new
				// BasicHeader(HTTP.CONTENT_TYPE,"application/xml");
				// httpPost.setHeader(arg0);
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPost);
			//----------------------
//			HttpEntity he = response.getEntity();
//			String s0 = EntityUtils.toString(he, charset);
			//-------------------------
			Header[] headers = response.getHeaders("Set-Cookie");
			result = headers[0].toString();
		} catch (Exception ex) {
			log.error("login - http请求连接请求失败！");
		}
		return result;
	}

	// get请求
	public String MNT_GET(String url,  String charset, String cookie) {
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String result = null;
		try {
			httpClient = createSSLClientDefault();
			httpGet = new HttpGet(url);
			httpGet.addHeader("Cookie", cookie);
			// httpGet.addHeader("Connection","Keep-Alive");
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			log.error("get - http请求连接请求失败！");
		}
		return result;
	}

	// post请求
	public String MNT_POST(String url, Map<String, String> map, String charset, String cookie) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
	
			log.warn("MNT_POST----start createSSL");
			try {
				httpClient = createSSLClientDefault();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				log.error("gethttpclient - 失败！"+e1.toString());
			}
			httpPost = new HttpPost(url);
			// 设置参数
			if(map.get("xml") != null){
				StringEntity entity = new StringEntity(map.get("xml"), charset);
				httpPost.setEntity(entity);
			}
						
			httpPost.addHeader("Cookie", cookie);
			// httpPost.addHeader("Connection", "Keep-Alive");
			log.warn("MNT_POST----end createSSL");
			CloseableHttpResponse response = null;
	try {
			log.warn("MNT_POST----start httpClient.execute");
			response = httpClient.execute(httpPost);
			log.warn("MNT_POST----end httpClient.execute");
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
					 EntityUtils.consume(resEntity);
				}
			}
		} catch (Exception ex) {
			log.warn("post - http请求连接请求失败！",ex);
		}finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            	log.warn("response.close - http请求连接请求失败！",e);
            }
            
        }
//		log.info("post - http请求result！"+result);
		return result;
	}
	
	// post请求
	public String SUB_POST(String url, Map<String, String> map, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
	
			log.warn("SUB_POST----start createSSL");
			try {
				httpClient = createSSLClient(1);
			} catch (Exception e1) {
				log.error("gethttpclient - 失败！"+e1.toString());
			}
			httpPost = new HttpPost(url);
			// 设置参数
			if(map.get("json") != null) {
				httpPost.setHeader("Content-Type", "application/json");
				StringEntity entity = new StringEntity(map.get("json"), charset);
				httpPost.setEntity(entity);				
			}
			log.warn("SUB_POST----end createSSL");
			CloseableHttpResponse response = null;
			try {
				log.warn("SUB_POST----start httpClient.execute");
				response = httpClient.execute(httpPost);
				log.warn("SUB_POST----end httpClient.execute");
				if (response != null) {
					HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						result = EntityUtils.toString(resEntity, charset);
						log.debug("SUB_POST result = :");
						log.debug(result);
						EntityUtils.consume(resEntity);
					}
				}
			} catch (Exception ex) {
			log.warn("post - http请求连接请求失败！",ex);
			}finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                //e.printStackTrace();
            	log.warn("response.close - http请求连接请求失败！",e);
            }            
        }
		return result;
	}
	
	public String readTxtFile(String filePath) {
		try {
			String result = "";
			String encoding = "UTF-8";
//			File file = new File(filePath);
//			if (file.isFile() && file.exists()) { // 判断文件是否存在
			if (filePath != null && !filePath.equals("")) {

				Resource res = new Resource();
				InputStream is = res.getResource(filePath);

				InputStreamReader read = new InputStreamReader(is, encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				read.close();
				return result;
			} else {
				log.error("读取实体文件-找不到指定的文件");
				return null;
			}
		} catch (Exception e) {
			log.error("http读取实体文件内容出错");
			return null;
		}
	}
}
