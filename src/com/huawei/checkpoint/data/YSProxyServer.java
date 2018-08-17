package com.huawei.checkpoint.data;


import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

/**
 * Discards any incoming data.
 */
public class YSProxyServer {
	 
	private static Logger log = Logger.getLogger(YSProxyServer.class); 
    private int port; 
    
    private volatile ChannelFuture f;

	public YSProxyServer() {
    }
    
    public YSProxyServer(int port) {
        this.port = port;
    }
    //TODO:线程池按照默认配置的，根据具体的情况�?要进行调�?
    public void run() throws Exception {
    	EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    	EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {  
                            ch.pipeline().addLast(new YSProxyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            // 绑定端口，同步等待成�?
            f = b.bind(port).sync(); // (7)
            log.debug("YuShiProxyServer-start listen at " + port);
			log.info("[start YuShi ProxyServer Socket] success!");
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            // 等待服务端监听端口关�?
            f.channel().closeFuture().sync();
            log.debug("YuShiProxyServer-stop listen at " + port);
        } finally {
        	log.info("YuShiProxyServer-finally-shutdown!");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    } 
    /**
     * 停止监听ProxyServer
     */
    public void closeProxyServer(){
    	log.info("YuShiProxyServer closeProxyServer!");
    	f.channel().disconnect();
    }
    public static void main(String[] args) {
    	YSProxyServer ss = new YSProxyServer(8888);
    	
    	try {
			ss.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}