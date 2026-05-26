package com.nexus.deliver.client.start;


import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.nexus.deliver.client.handler.MessageConsumerImpl4Client;
import com.nexus.deliver.client.handler.NettyClient;
import com.nexus.deliver.common.service.MessageConsumer;
import com.nexus.deliver.common.service.RingBufferWorkerPoolFactory;

/**
 *
 * @author panlf
 * @date 2026/5/26
 */
public class ClientStart {
    public static void main(String[] args) {
        MessageConsumer[] consumers = new MessageConsumer[4];
        for(int i=0;i<consumers.length;i++){
            MessageConsumer messageConsumer = new MessageConsumerImpl4Client("code:clientId:"+i);
            consumers[i]=messageConsumer;
        }

        RingBufferWorkerPoolFactory.getInstance().initAndStart(
                ProducerType.MULTI,
                1024*1024,
                new BlockingWaitStrategy(),
                consumers);

        new NettyClient().sendData();
    }
}
