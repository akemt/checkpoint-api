package com.huawei.checkpoint.data;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.huawei.checkpoint.SystemManager;
import com.huawei.checkpoint.utils.Config;
import com.huawei.checkpoint.utils.RingBuffer;
import com.huawei.checkpoint.utils.VCMAccess;

import io.netty.channel.ChannelHandlerContext;

public class ManagerUtils {
	private static Logger log = Logger.getLogger(ManagerUtils.class);
	private static int iSize = 700000*5;
	
	private ChannelHandlerContext ctx;
	
	private RingBuffer rb = new RingBuffer(iSize);

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public RingBuffer getRb() {
		return rb;
	}

	public void setRb(RingBuffer rb) {
		this.rb = rb;
	}
	
	/**
	 * 通过接收客户端IP，查询此IP是否在VCM，卡口LIST表中存在
	 * 
	 * @param strClientIP
	 * @return 存在： true , 不存在：false
	 */
	public static boolean isVCMDataByIP(String strClientIP) {
		boolean flag = false;
		
		String strCookie = SystemManager.getIns().getCookie();
		// 1. 登录VCM
//		String srtCookit = VCMAccess.vcmApplicationLogin();
		// 2. 根据IP、SourceType=1、XTBH != null。查询 VCM是否存在数据。 
		String reStr = VCMAccess.queryChPointListByIP(strCookie, strClientIP);
		Document document = null;
		try {
			document = DocumentHelper.parseText(reStr);

			String strResponseCode = document.getRootElement().element("result").element("code").getText();
			if (strResponseCode != null && strResponseCode.equals("0")) {// 查询成功
				// count
				String strResponseCount = document.getRootElement().element("result").element("count").getText();

				if (strResponseCount != null && !strResponseCount.equals("0")
						&& Integer.parseInt(strResponseCount) > 0) {// 查询成功-存在IP
					// count
					flag = true;
				}
			}

		} catch (DocumentException e1) {
			//e1.printStackTrace();
			log.warn(" queryChPointListByIP Exception :" + strClientIP , e1);
		}catch (Exception e) {
			log.warn(" queryChPointListByIP Exception :" + strClientIP , e);
			//e.printStackTrace();
		}
		return flag;
	}
 
}
