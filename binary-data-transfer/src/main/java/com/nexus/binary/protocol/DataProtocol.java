package com.nexus.binary.protocol;

import lombok.Data;

/**
 * @author panlf
 * @date 2023/5/17
 */
@Data
public class DataProtocol {
    private byte[] head = {(byte) 0xef, (byte) 0xef};
    private byte[] data;
    private byte[] tail = {(byte) 0xfd, (byte) 0xfd};
}
