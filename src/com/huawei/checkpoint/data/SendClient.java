package com.huawei.checkpoint.data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.huawei.checkpoint.utils.Config;

//import io.netty.channel.socket.SocketChannel;

public class SendClient {
	
	private static Logger log = Logger.getLogger(SendClient.class);
	
	private static SocketChannel socketChannel;
	private static Selector selector;  
	public static void initClient() throws IOException, ClosedChannelException {  
		
		Config cf = Config.getIns();
		
		//获取共享网关端口-Server端
		int iSgPort = cf.getSGPort();
		//获取共享网关：IP-Server端
		String strSGAddr = cf.getSGAddr();
		
        InetSocketAddress addr = new InetSocketAddress(strSGAddr,iSgPort); 
        // 获得一个Socket通道
        socketChannel = SocketChannel.open();  
        // 获得一个通道管理器
        selector = Selector.open();  
        // 设置通道为非阻塞  
        socketChannel.configureBlocking(false); 
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
        socketChannel.register(selector, SelectionKey.OP_READ);  
  
        // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调用channel.finishConnect();才能完成连接
        socketChannel.connect(addr);  
  
        while (!socketChannel.finishConnect()) {  
        	log.warn("check finish connection");
        }  
    }  
	public static void send(String data) {
		try {   
			
            ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(data).getBytes("UTF-8"));  
            while (buffer.hasRemaining()) {  
            	log.warn("buffer.hasRemaining() is true.");
                socketChannel.write(buffer);  
            }  
        } catch (IOException e) {  
            if (socketChannel.isOpen()) {  
                try {
					socketChannel.close();
				} catch (IOException e1) {
					log.warn("close exception",e1);
					//e1.printStackTrace();
				}  
            }  
            log.warn("send client exception,data:"+data,e);
            //e.printStackTrace();  
        }  
	}
	
	public static void send(int sgReDataType,ArrayList<String> strFilePathUrl) {
		try {   
			String data = MessageUtils.setMessageToJson(sgReDataType,strFilePathUrl);

			ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());  
			while (buffer.hasRemaining()) {  
				log.warn("buffer.hasRemaining() is true.");
				socketChannel.write(buffer);  
			}  
		} catch (IOException e) {  
			if (socketChannel.isOpen()) {  
				try {
					socketChannel.close();
				} catch (IOException e1) {
					log.warn("close exception",e1);
					//e1.printStackTrace();
				}  
			} 
			log.warn("send client exception,files:"+strFilePathUrl,e);
			//e.printStackTrace();  
		}  
	}
	/**
	 * 断开SendClient连接
	 */
	public static void stopSendClient(){
		if (socketChannel.isOpen()) {  
            try {
				socketChannel.close();
				log.info("SendClient close");
			} catch (IOException e1) {
				e1.printStackTrace();
			}  
        }   
	}

}
