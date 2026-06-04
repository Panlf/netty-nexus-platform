package com.nexus.protocol.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * 自定义协议解码器
 * 协议格式： 4字节魔数("@TAG") + 8字节十六进制长度(左补零) + 消息体(Base64编码后的JSON)
 * 解码后将消息体还原为 RequestMessage 对象
 *
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
public class ProtocolMessageDecoder extends ByteToMessageDecoder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 最小需要 4(魔数) + 8(长度) = 12 字节
        if (in.readableBytes() < 12) {
            return;
        }
        in.markReaderIndex();

        // 1. 读取魔数
        byte[] magic = new byte[4];
        in.readBytes(magic);
        if (!"@TAG".equals(new String(magic, StandardCharsets.UTF_8))) {
            in.resetReaderIndex();
            log.error("非法协议头，关闭连接: {}", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }

        // 2. 读取8字节长度（十六进制字符串，例如 "00000123"）
        byte[] lenBytes = new byte[8];
        in.readBytes(lenBytes);
        String lenHex = new String(lenBytes, StandardCharsets.UTF_8);
        int bodyLen;
        try {
            bodyLen = Integer.parseInt(lenHex, 16);
        } catch (NumberFormatException e) {
            in.resetReaderIndex();
            log.error("长度字段格式错误: {}", lenHex);
            ctx.close();
            return;
        }

        // 限制最大1KB（可根据需求调整）
        if (bodyLen < 1 || bodyLen > 1024) {
            in.resetReaderIndex();
            log.error("非法长度: {}，关闭连接", bodyLen);
            ctx.close();
            return;
        }

        // 3. 检查数据是否完整
        if (in.readableBytes() < bodyLen) {
            in.resetReaderIndex();
            return; // 等待更多数据
        }

        // 4. 读取并解码消息体
        byte[] encodedBody = new byte[bodyLen];
        in.readBytes(encodedBody);
        byte[] jsonBytes = Base64.getDecoder().decode(encodedBody);
        String json = new String(jsonBytes, StandardCharsets.UTF_8);

        // 5. 反序列化为 RequestMessage 对象
        RequestMessage msg = OBJECT_MAPPER.readValue(json, RequestMessage.class);
        log.info("解码成功: type={}, deviceCode={}", msg.getRequestType(), msg.getDeviceCode());

        out.add(msg);
    }
}
