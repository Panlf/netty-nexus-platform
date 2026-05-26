package com.nexus.deliver.common.service;

import com.lmax.disruptor.RingBuffer;

import com.nexus.deliver.common.entity.TranslatorData;
import com.nexus.deliver.common.entity.TranslatorDataWrapper;
import io.netty.channel.ChannelHandlerContext;

public class MessageProducer {
	private String producerId;
	
	private final RingBuffer<TranslatorDataWrapper> ringBuffer;
	
	public MessageProducer(String producerId,RingBuffer<TranslatorDataWrapper> ringBuffer) {
		this.setProducerId(producerId);
		this.ringBuffer = ringBuffer;
	}
	
	public void onData(TranslatorData data, ChannelHandlerContext ctx){
		long sequence = ringBuffer.next();
		try {
            TranslatorDataWrapper wrapper = ringBuffer.get(sequence);
            wrapper.setData(data);
            wrapper.setCtx(ctx);
		} finally {
			ringBuffer.publish(sequence);
		}
	}

	public String getProducerId() {
		return producerId;
	}

	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}

}
