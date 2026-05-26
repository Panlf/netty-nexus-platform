package com.nexus.deliver.server.start;


import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.nexus.deliver.common.service.MessageConsumer;
import com.nexus.deliver.common.service.RingBufferWorkerPoolFactory;
import com.nexus.deliver.server.handler.MessageConsumerImpl4Server;
import com.nexus.deliver.server.handler.NettyServer;

/**
 *
 * @author panlf
 * @date 2026/5/26
 */
public class ServerStart {
    public static void main(String[] args) {
        MessageConsumer[] consumers = new MessageConsumer[4];
        for(int i=0;i<consumers.length;i++){
            MessageConsumer messageConsumer = new MessageConsumerImpl4Server("code:serverId:"+i);
            consumers[i]=messageConsumer;
        }

        RingBufferWorkerPoolFactory.getInstance().initAndStart(
                ProducerType.MULTI,
                1024*1024,
                new BlockingWaitStrategy(),
                consumers);

        new NettyServer();
    }
}
