package com.nexus.upload.bean;

import java.io.File;
import java.io.Serializable;

/**
 * @author panlf
 * @date 2021/10/3
 */
public class UploadFile implements Serializable {
    private static final long serialVersionUID = -5292380060585118637L;
    private File file;
    private String fileMd5;
    private int startPosition;
    private byte[] bytes;
    private int endPosition;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }
}
