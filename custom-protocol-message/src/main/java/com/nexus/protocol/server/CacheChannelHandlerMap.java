package com.nexus.protocol.server;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理设备通道与设备编码的映射关系（线程安全）
 * @author panlf
 * @date 2026/6/3
 */
@Slf4j
public class CacheChannelHandlerMap {
    private static final Map<String, ChannelDeviceDto> CHANNEL_MAP = new ConcurrentHashMap<>();

    private CacheChannelHandlerMap() {} // 工具类私有构造

    /**
     * 添加或更新通道设备映射
     * @param channelId 通道ID（ctx.channel().id().asLongText()）
     * @param ctx ChannelHandlerContext
     * @param deviceCode 设备编码
     */
    public static void put(String channelId, ChannelHandlerContext ctx, String deviceCode) {
        ChannelDeviceDto dto = CHANNEL_MAP.computeIfAbsent(channelId, k -> new ChannelDeviceDto());
        dto.setChannelHandlerContext(ctx);
        dto.setDeviceCode(deviceCode);
        log.debug("通道映射更新: channelId={}, deviceCode={}", channelId, deviceCode);
    }

    /**
     * 根据通道ID获取设备信息
     */
    public static ChannelDeviceDto get(String channelId) {
        return CHANNEL_MAP.get(channelId);
    }

    /**
     * 移除通道映射（通道断开时调用）
     */
    public static void remove(String channelId) {
        CHANNEL_MAP.remove(channelId);
        log.debug("移除通道映射: channelId={}", channelId);
    }

    /**
     * 获取当前在线设备数量
     */
    public static int size() {
        return CHANNEL_MAP.size();
    }
}
