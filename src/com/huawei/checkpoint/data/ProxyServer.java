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
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Discards any incoming data.
 */
public class ProxyServer {
	
	private static Logger log = Logger.getLogger(ProxyServer.class); 
	
    private int port; 
    private int tooLongFrame; 
    
    private volatile ChannelFuture f;

	public ProxyServer() {
    }
    
    public ProxyServer(int port,int iTooLongFrame) {
        this.port = port;
        this.tooLongFrame = iTooLongFrame;
    }
    //TODO:线程池按照默认配置的，根据具体的情况需要进行调整
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
                			// 首先创建分隔符缓冲对象ByteBuf，本例程中使用“$_”作为分隔符。
                			ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                			// 创建DelimiterBasedFrameDecoder对象，将其加入到ChannelPipeline中。
                			// DelimiterBasedFrameDecoder有多个构造方法，这里我们传递两个参数，
                			// 第一个1024表示单条消息的最大长度，当达到该长度后仍然没有查找到分隔符，
                			// 就抛出TooLongFrame Exception异常，防止由于异常码流缺失分隔符导致的内存溢出，
                			// 这是Netty解码器的可靠性保护；第二个参数就是分隔符缓冲对象。
                			ch.pipeline().addLast(new DelimiterBasedFrameDecoder(tooLongFrame, delimiter));
                			//工作原理是依次遍历ByteBuf中的可读字节，判断是否有换行符，如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成了一行。
                			//由于，接收的XML有回车/n，所以当接收XML格式的数据时，不建议使用此方法：LineBasedFrameDecoder。
//                        	ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        	ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ProxyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            // 绑定端口，同步等待成功
            f = b.bind(port).sync(); // (7)
            log.debug("ProxyServer-start listen at " + port);
			log.info("[start ProxyServer Socket] success!");
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
            log.debug("ProxyServer-stop listen at " + port);
        } finally {
        		log.debug("ProxyServer-finally-shutdown!");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    } 
    /**
     * 停止监听ProxyServer
     */
    public void closeProxyServer(){
    	log.info("[closeProxyServer Socket] success!");
    	f.channel().disconnect();
    }
}