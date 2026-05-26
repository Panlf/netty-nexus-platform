package com.nexus.deliver.common.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;
import com.nexus.deliver.common.entity.TranslatorDataWrapper;

public class RingBufferWorkerPoolFactory {
	
	private static class SingletonHolder{
		static final RingBufferWorkerPoolFactory instance = new RingBufferWorkerPoolFactory();
	}
	
	private RingBufferWorkerPoolFactory(){
		
	}
	
	public static RingBufferWorkerPoolFactory getInstance(){
		return SingletonHolder.instance;
	}
	
	private static final Map<String,MessageProducer> producers = new ConcurrentHashMap<>();
	
	private static final Map<String,MessageConsumer> consumers = new ConcurrentHashMap<>();

	private RingBuffer<TranslatorDataWrapper> ringBuffer;

    public void initAndStart(ProducerType producerType,int bufferSize,WaitStrategy waitStrategy,MessageConsumer[] messageConsumers){
		//构建ringBuffer对象
		this.ringBuffer = RingBuffer.create(producerType, 
				new EventFactory<TranslatorDataWrapper>() {
					@Override
                    public TranslatorDataWrapper newInstance(){
						return new TranslatorDataWrapper();
					}
				}, 
				bufferSize, 
				waitStrategy);
		//设置序号栅栏
        SequenceBarrier sequenceBarrier = this.ringBuffer.newBarrier();
		//设置工作池
        WorkerPool<TranslatorDataWrapper> workerPool = new WorkerPool<>(this.ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                messageConsumers);
		
		//把所构建的消费者置入池中
		for (MessageConsumer messageConsumer : messageConsumers) {
			consumers.put(messageConsumer.getConsumerId(), messageConsumer);
		}
		
		//添加Sequences
		this.ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
		
		//启动工作池
		workerPool.start(Executors.newCachedThreadPool());
	}
	
	public MessageProducer getMessageProducer(String producerId){
		MessageProducer messageProducer = producers.get(producerId);
		if(null == messageProducer){
			messageProducer = new MessageProducer(producerId,this.ringBuffer);
			producers.put(producerId, messageProducer);
		}
		return messageProducer;
	}
	
	static class EventExceptionHandler implements ExceptionHandler<TranslatorDataWrapper>{

		@Override
		public void handleEventException(Throwable ex, long sequence, TranslatorDataWrapper event) {
			
		}

		@Override
		public void handleOnStartException(Throwable ex) {
			
		}

		@Override
		public void handleOnShutdownException(Throwable ex) {
			
		}
		
	}
}


