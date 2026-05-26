package com.nexus.deliver.server.handler;


import com.nexus.deliver.common.entity.TranslatorData;
import com.nexus.deliver.common.entity.TranslatorDataWrapper;
import com.nexus.deliver.common.service.MessageConsumer;
import io.netty.channel.ChannelHandlerContext;

public class MessageConsumerImpl4Server extends MessageConsumer {
	

	public MessageConsumerImpl4Server(String consumerId) {
		super(consumerId);
	}

	@Override
    public void onEvent(TranslatorDataWrapper event) throws Exception {
		TranslatorData request = event.getData();
		ChannelHandlerContext ctx = event.getCtx();
		
		//业务逻辑处理
        System.out.printf("Server端:id=%s,name=%s,message=%s\n",request.getId(),request.getName(),request.getMessage());
		
		//回应相应信息
		TranslatorData response = new TranslatorData();
		response.setId("response:"+request.getId());
		response.setName("response:"+request.getName());
		response.setMessage("response:"+request.getMessage());
		ctx.writeAndFlush(response);
	}

}
