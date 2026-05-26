package com.nexus.deliver.client.handler;

import com.nexus.deliver.common.entity.TranslatorData;
import com.nexus.deliver.common.entity.TranslatorDataWrapper;
import com.nexus.deliver.common.service.MessageConsumer;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerImpl4Client extends MessageConsumer {
	

	public MessageConsumerImpl4Client(String consumerId) {
		super(consumerId);
	}

	@Override
	public void onEvent(TranslatorDataWrapper event) throws Exception {
		TranslatorData response = event.getData();
		//ChannelHandlerContext ctx = event.getCtx();
		try {
			//业务逻辑处理
            System.out.printf("Client端:id=%s,name=%s,message=%s\n",
                    response.getId(),response.getName(),response.getMessage());
		} finally {
			ReferenceCountUtil.release(response);
		}
	}

}
