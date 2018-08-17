package com.huawei.checkpoint.data;
 
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter; 
import java.net.InetSocketAddress; 
import org.apache.log4j.Logger;
 

/**
 * Handles a server-side channel.
 */
public class ProxyServerHandler extends ChannelInboundHandlerAdapter { 
	
	private static Logger log = Logger.getLogger(ProxyServerHandler.class);
	
	private String strReceiveData;
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { 
		
    	//1.每次Server Socket 接收到数据。需要把数据传到对应的SgControllerParter中
    	if(msg != null){
    		strReceiveData = (String) msg; 

            //传输内容 
//    		byte[] req =strReceiveData.getBytes(); 
			log.debug("ProxyServer-Socket Read data :"+strReceiveData);
			log.info("ProxyServer-Socket Read data success!");
			DataManager.getIns().setSgData(MessageUtils.getMessageToJson(strReceiveData)); 
    	}
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                .remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        ProxyChannelMng.getInstance().rmProxyChannel(clientIP);
        ctx.close();
    }

    //ChannelHandlerContext通道处理上下文
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws InterruptedException { 

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                .remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        ProxyChannelMng.getInstance().addProxyChannel(clientIP,ctx);
    }

}