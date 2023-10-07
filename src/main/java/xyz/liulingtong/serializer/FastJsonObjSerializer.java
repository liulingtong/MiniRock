package xyz.liulingtong.serializer;

import com.alibaba.fastjson2.JSON;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FastJsonObjSerializer implements ObjSerializer {
    @Override
    public byte[] serialize(Object data) {
        String jsonString = JSON.toJSONString(data);
        try {
            return compressString(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> tClass) {
        String jsonString;
        try {
            jsonString = decompressBytes(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JSON.parseObject(jsonString, tClass);
    }

    private static byte[] compressString(String inputString) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            gzipOutputStream.write(inputString.getBytes());
        }
        return outputStream.toByteArray();
    }

    private static String decompressBytes(byte[] compressedBytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedBytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toString();
        }
    }
}
