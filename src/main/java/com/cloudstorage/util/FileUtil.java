package com.cloudstorage.util;

public class FileUtil {

    public static byte[] mergeChunks(byte[] chunk1, byte[] chunk2) {
        byte[] full = new byte[chunk1.length + chunk2.length];
        System.arraycopy(chunk1, 0, full, 0, chunk1.length);
        System.arraycopy(chunk2, 0, full, chunk1.length, chunk2.length);
        return full;
    }
}
