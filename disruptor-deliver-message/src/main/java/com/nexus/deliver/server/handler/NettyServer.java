package com.nexus.deliver.server.handler;

import com.nexus.deliver.common.codec.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
	public NettyServer(){
		//1、创建两个工程线程组：一个用于接收网络请求的线程组，另一个用于实际处理业务的线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			//.option(ChannelOption.SO_SNDBUF, 16*1024)
			//.option(ChannelOption.SO_RCVBUF, 16*1024)
			//缓冲区动态调配
			.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
			//缓存区池化
			.option(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT)		
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					ch.pipeline().addLast(new ServerHandler());
					ch.pipeline().addLast(new ServerHandler());
				}
			});
		
		//绑定端口，同步等等请求连接
		try {
			ChannelFuture cf = serverBootstrap.bind(8765).sync();
            System.out.println("Server startup...");
			cf.channel().closeFuture().sync();
			
		} catch (Exception e) {
            System.out.printf("出现异常，异常信息:%s\n",e.getMessage());
		}finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
            System.out.println("Server ShutDown...");
		}
	}
}
