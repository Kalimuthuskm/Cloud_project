package com.cloudstorage.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.web.multipart.MultipartFile;

import java.util.zip.GZIPInputStream;

public class FileUtil {

    // ✅ Merge two byte chunks into one file
    public static byte[] mergeChunks(byte[] chunk1, byte[] chunk2) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(chunk1);
        outputStream.write(chunk2);
        return outputStream.toByteArray();
    }

    // ✅ Compress file data before encryption (GZIP)
    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
        }
        return bos.toByteArray();
    }

    // ✅ Decompress file data after decryption
    public static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] tmp = new byte[1024];
        int len;
        while ((len = gzip.read(tmp)) > 0) {
            buffer.write(tmp, 0, len);
        }

        return buffer.toByteArray();
    }

    // ✅ Fix: Proper byte array slicing method
    public static byte[] slice(byte[] fileBytes, int start, int end) {
        byte[] slice = new byte[end - start];
        System.arraycopy(fileBytes, start, slice, 0, end - start);
        return slice;
    }
    public static MultipartFile createMultipartFileFromBytes(byte[] content, String filename) {
        return new MultipartFile() {
            @SuppressWarnings("null")
            @Override
            public String getName() {
                return filename;
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                return null; // Set content type if needed
            }

            @Override
            public boolean isEmpty() {
                return content.length == 0;
            }

            @Override
            public long getSize() {
                return content.length;
            }

            @SuppressWarnings("null")
            @Override
            public byte[] getBytes() throws IOException {
                return content;
            }

            @SuppressWarnings("null")
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(content);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (FileOutputStream out = new FileOutputStream(dest)) {
                    out.write(content);
                }
            }
        };
    }
}
    

