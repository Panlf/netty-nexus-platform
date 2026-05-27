package com.nexus.websocket.server;

import com.nexus.websocket.handler.WebChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WebChatServer {

	private final int port;
	
	public WebChatServer(int port){
		this.port = port;
	}
	
	public void start() {
		//定义两个线程组
		//处理新客户端的接入
		EventLoopGroup boss= new NioEventLoopGroup();
		//处理客户端的网络请求
		EventLoopGroup worker = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			
			bootstrap.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.childHandler(new WebChatServerInitializer())
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
		
			ChannelFuture future = bootstrap.bind(port).sync(); 
		
			System.out.println("[系统消息]:服务器已经启动");
			
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
		
	}
	
	public static void main(String[] args) {
		new WebChatServer(8080).start();
	}

}
