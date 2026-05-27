package com.nexus.rpc.client;

import java.lang.reflect.Proxy;
import java.util.concurrent.FutureTask;

import com.nexus.rpc.common.Params;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyRpcClient {
	
	public static NettyRpcClientHandler initClient(){
		NettyRpcClientHandler nettyRpcClientHandler = new NettyRpcClientHandler();
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipline = socketChannel.pipeline();
						// 编码器
						pipline.addLast(new ObjectEncoder());
						// 解码器
						pipline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

						pipline.addLast(nettyRpcClientHandler);
					}
			});
		
			try {
				bootstrap.connect("localhost",8765).sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			return nettyRpcClientHandler;
	}
	
	
	@SuppressWarnings("unchecked")
	public static<T> T proxy(Class<T> target){
		return (T) Proxy.newProxyInstance(target.getClassLoader(),
				new Class<?>[]{target},
				(proxy, method,args) -> {
					//构建消息体	
					Params params = new Params();
					params.setClassName(target.getName());
					params.setMethodName(method.getName());
					params.setParameterTypes(method.getParameterTypes());
					params.setParameterValues(args);
					
					
					//通过远程调用代理方法
					NettyRpcClientHandler nettyRpcClientHandler = initClient();
					
					//设置参数
					nettyRpcClientHandler.setBody(params);
					
					//设定异步回调
					FutureTask<Object> task = new FutureTask<>(nettyRpcClientHandler);
					new Thread(task).start();
					return task.get();
				});
	}
}
