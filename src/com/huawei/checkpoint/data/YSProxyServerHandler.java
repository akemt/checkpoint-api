package com.huawei.checkpoint.data;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.utils.RingBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a server-side channel.
 */
public class YSProxyServerHandler extends ChannelInboundHandlerAdapter {

	private static Logger log = Logger.getLogger(YSProxyServerHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		if (msg != null) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String clientIP = insocket.getAddress().getHostAddress();

			log.debug("VCMServer-Socket channelReadIP :" + clientIP + ":" + insocket.getPort());
			String ipAndPort = clientIP + ":" + insocket.getPort();
			ManagerUtils manUtils = ProxyChannelMng.getInstance().getProxyManagerByIP(ipAndPort);

			ByteBuf bb = (ByteBuf) msg;
			int len = bb.readableBytes();
			byte[] bytes = new byte[len];
			bb.getBytes(0, bytes);
			/** 1.把 接收的数据bDataRecvd放到RingBuffer */
			RingBuffer rb = manUtils.getRb();
			rb.put(bytes);
			log.debug("VCMServer-Socket Read byte :" + rb.end);
			YsUnPack.msgInfoProc(rb, ctx);
			
			bb.release();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		log.info("VCMServer-Socket exceptionCaught rmProxyChannelManager!");
		cause.printStackTrace();
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();
		String ipAndPort = clientIP + ":" + insocket.getPort();
		log.debug("VCMServer-Socket exceptionCaught rmProxyChannelManager IP And Port :"+ipAndPort);
		ProxyChannelMng.getInstance().rmProxyChannelManager(ipAndPort);
		ctx.close();
		log.info("VCMServer-Socket exceptionCaught rmProxyChannelManager2!");
	}

	// ChannelHandlerContext通道处理上下�?
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws InterruptedException {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();

		log.debug("VCMServer-Socket channelActive :" + clientIP + ":" + insocket.getPort());
		boolean flag = ManagerUtils.isVCMDataByIP(clientIP);
		if (flag) {// 此IP 在VCM 卡口LIST中存在
			String ipAndPort = clientIP + ":" + insocket.getPort();
			log.info("VCMServer-Socket channelActive connect success!");
			ManagerUtils man = new ManagerUtils();
			man.setCtx(ctx);
			ProxyChannelMng.getInstance().addProxyManager(ipAndPort, man);
		} else {// 不存在，关闭客户端连接
			log.info("VCMServer-Socket channelActive connect false-checkpointinfo no ip!");
			ctx.close();
		}
	}
	
	public void channelInactive(ChannelHandlerContext ctx)
            throws java.lang.Exception{
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String clientIP = insocket.getAddress().getHostAddress();
			String ipAndPort = clientIP + ":" + insocket.getPort();
			log.debug("VCMServer-Socket channelInactive channelInactive IP And Port :"+ipAndPort);
			ProxyChannelMng.getInstance().rmProxyChannelManager(ipAndPort);
			ctx.close();
	}
	 
}