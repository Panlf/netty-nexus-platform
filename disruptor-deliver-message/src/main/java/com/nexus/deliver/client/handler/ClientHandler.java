package com.nexus.deliver.client.handler;


import com.nexus.deliver.common.entity.TranslatorData;
import com.nexus.deliver.common.service.MessageProducer;
import com.nexus.deliver.common.service.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		TranslatorData response = (TranslatorData)msg;
		String producerId = "code:sessionId:001";
		MessageProducer messageProducer = RingBufferWorkerPoolFactory
						.getInstance()
						.getMessageProducer(producerId);
		
		messageProducer.onData(response, ctx);
	}
}