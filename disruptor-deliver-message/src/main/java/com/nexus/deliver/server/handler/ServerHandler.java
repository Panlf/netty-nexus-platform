package com.nexus.deliver.server.handler;

import com.nexus.deliver.common.entity.TranslatorData;
import com.nexus.deliver.common.service.MessageProducer;
import com.nexus.deliver.common.service.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		TranslatorData request = (TranslatorData)msg;
		
		//自己的应用服务应该有一个ID生成规则
		String producerId = "sessionId:001";
		MessageProducer messageProducer = RingBufferWorkerPoolFactory
								.getInstance()
								.getMessageProducer(producerId);
		
		messageProducer.onData(request, ctx);
	}
}
