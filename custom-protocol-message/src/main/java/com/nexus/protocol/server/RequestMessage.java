package com.nexus.protocol.server;


import lombok.Data;

import java.io.Serializable;

/**
 * 设备与服务器之间的通信协议实体
 * 传输时会被序列化为 JSON 字符串，再进行 Base64 + 自定义帧封装
 * @author panlf
 * @date 2026/6/3
 */
@Data
public class RequestMessage implements Serializable {

    private static final long serialVersionUID = 2157837809448730595L;

    private String channelId;       // 通道唯一标识
    private String requestType;     // 消息类型：HEART_BEAT, ERROR.
    private String content;         // 业务数据
    private String deviceCode;      // 设备编码
    private Long workTimestamp;     // 时间戳
    private String sign;            // 签名
}
