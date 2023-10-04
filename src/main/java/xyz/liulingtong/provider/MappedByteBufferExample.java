package xyz.liulingtong.provider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class MappedByteBufferExample {
    public static void main(String[] args) {
        try {
            // 创建RandomAccessFile对象，以读写模式打开文件
            File exampleFile = new File("example.txt");
            RandomAccessFile file = new RandomAccessFile(exampleFile, "rw");
            file.write("abc".getBytes(StandardCharsets.UTF_8));
            long l = exampleFile.lastModified();
            // 获取文件通道
            FileChannel channel = file.getChannel();
            long size = channel.size();
            System.out.println("文件大小 = " + size);

            // 将文件映射到内存中
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            // 读取文件内容
            System.out.println("原始文件内容：");
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            System.out.println();

            Thread.sleep(10000);
            if (exampleFile.lastModified() != l) {
                long size1 = channel.size();
                buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size1);
                System.out.println("检测到文件改动，重新加载文件，文件长度 = " + size1);
            }
            // 修改文件内容
            buffer.position(0); // 将位置设置为文件开头
            while (buffer.remaining() > 0) {
                byte b = buffer.get();
                buffer.put(buffer.position() - 1, (byte) Character.toUpperCase(b));
            }

            // 刷新缓冲区，将修改写入文件
            buffer.force();

            // 重新读取文件内容
            buffer.position(0); // 将位置设置为文件开头
            System.out.println("修改后的文件内容：");
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            System.out.println();

            // 关闭文件通道和文件
            channel.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
