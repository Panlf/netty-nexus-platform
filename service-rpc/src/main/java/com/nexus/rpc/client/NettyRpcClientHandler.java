package com.nexus.rpc.client;

import java.util.concurrent.Callable;

import com.nexus.rpc.common.Params;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyRpcClientHandler extends ChannelHandlerAdapter implements Callable<Object>{
private ChannelHandlerContext context;
	
	private Object result;
	
	private Params params;
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		context = ctx;
	}
	@Override
	public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
		result = msg;
		notify();
	}

	@Override
	public synchronized Object call() throws InterruptedException{
		context.writeAndFlush(params);
		wait();
		return result;
	}
	
	public void setBody(Params params){
		this.params = params;
	}
}
