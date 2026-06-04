package com.nexus.protocol.client;

import com.nexus.protocol.*;
import com.nexus.protocol.server.ProtocolMessageDecoder;
import com.nexus.protocol.server.ProtocolMessageEncoder;
import com.nexus.protocol.server.RequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 设备客户端（模拟设备）
 *
 * @author panlf
 * @date 2026/6/4
 */
@Slf4j
public class ProtocolMessageClientStart {
    private final String host;
    private final int port;
    private final String deviceCode;
    private EventLoopGroup group;
    private Channel channel;
    private volatile boolean connected = false;

    public ProtocolMessageClientStart(String host, int port, String deviceCode) {
        this.host = host;
        this.port = port;
        this.deviceCode = deviceCode;
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast("encoder", new ProtocolMessageEncoder());
                        ch.pipeline().addLast("decoder", new ProtocolMessageDecoder());
                        ch.pipeline().addLast("clientHandler", new ClientMessageHandler(deviceCode));
                    }
                });

        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
        connected = true;
        log.info("客户端连接成功: {}:{}", host, port);

        // 启动心跳线程（每30秒发送一次）
        startHeartBeat();
    }

    private void startHeartBeat() {
        Thread heartThread = new Thread(() -> {
            while (connected && channel != null && channel.isActive()) {
                try {
                    TimeUnit.SECONDS.sleep(30);
                    if (channel.isActive()) {
                        RequestMessage heartBeat = new RequestMessage();
                        heartBeat.setRequestType("HEART_BEAT");
                        heartBeat.setDeviceCode(deviceCode);
                        heartBeat.setContent("ping");
                        channel.writeAndFlush(heartBeat);
                        log.debug("发送心跳: deviceCode={}", deviceCode);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        heartThread.setDaemon(true);
        heartThread.start();
    }

    public void sendData(String content) {
        if (channel != null && channel.isActive()) {
            RequestMessage msg = new RequestMessage();
            msg.setRequestType("DATA_REPORT");
            msg.setDeviceCode(deviceCode);
            msg.setContent(content);
            channel.writeAndFlush(msg);
            log.info("发送数据: {}", content);
        } else {
            log.warn("通道未连接，无法发送数据");
        }
    }

    public void close() {
        connected = false;
        if (channel != null) channel.close();
        if (group != null) group.shutdownGracefully();
        log.info("客户端关闭");
    }

    public static void main(String[] args) throws Exception {
        String deviceCode = "DEV-" + UUID.randomUUID().toString().substring(0, 6);
        ProtocolMessageClientStart client = new ProtocolMessageClientStart("127.0.0.1", 8080, deviceCode);
        client.connect();

        // 模拟发送业务数据
        for (int i = 1; i <= 5; i++) {
            TimeUnit.SECONDS.sleep(10);
            client.sendData("{\"temperature\": " + (20 + i) + ", \"humidity\": 60}");
        }

        // 保持连接运行一段时间
        TimeUnit.MINUTES.sleep(2);
        client.close();
    }
}
