package xyz.liulingtong.provider;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SharedMemoryExample {
    public static void main(String[] args) {
        try {
            // 创建RandomAccessFile对象，以读写模式打开文件
            RandomAccessFile file = new RandomAccessFile("example.txt", "rw");

            // 获取文件通道
            FileChannel channel = file.getChannel();

            // 将文件映射到内存中
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());

            // 在第一个进程中修改文件内容
            if (args.length > 0 && args[0].equals("process1")) {
                buffer.putChar('H'); // 修改第一个字符为'H'
                buffer.putChar('e'); // 修改第二个字符为'e'

                // 刷新缓冲区，将修改写入文件
                buffer.force();
            }

            // 等待一段时间，以便第一个进程完成修改
            Thread.sleep(2000);

            // 在第二个进程中读取文件内容
            if (args.length > 0 && args[0].equals("process2")) {
                // 重新加载内存映射区域
                buffer.clear(); // 清空缓冲区
                buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

                // 读取文件内容
                System.out.println("最新的文件内容：");
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                System.out.println();
            }

            // 关闭文件通道和文件
            channel.close();
            file.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}