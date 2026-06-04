package com.nexus.protocol.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 自定义协议编码器
 * 将 RequestMessage 对象 -> JSON -> Base64 -> 添加协议头 -> 发送
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
public class ProtocolMessageEncoder extends MessageToByteEncoder<RequestMessage> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestMessage msg, ByteBuf out) throws Exception {
        // 1. 对象转 JSON
        String json = OBJECT_MAPPER.writeValueAsString(msg);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

        // 2. Base64 编码
        byte[] base64Bytes = Base64.getEncoder().encode(jsonBytes);
        int bodyLen = base64Bytes.length;

        if (bodyLen > 1024) {
            log.error("消息体过长({})，关闭连接", bodyLen);
            ctx.close();
            return;
        }

        // 3. 生成8位十六进制长度字符串（左补零）
        String lenHex = String.format("%08X", bodyLen);
        byte[] lenBytes = lenHex.getBytes(StandardCharsets.UTF_8);

        // 4. 写出协议帧
        out.writeBytes("@TAG".getBytes(StandardCharsets.UTF_8));
        out.writeBytes(lenBytes);
        out.writeBytes(base64Bytes);

        log.debug("编码完成: deviceCode={}, type={}, length={}", msg.getDeviceCode(), msg.getRequestType(), bodyLen);
    }
}
