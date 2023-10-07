package xyz.liulingtong.provider;

import xyz.liulingtong.enums.MetaDataEnum;
import xyz.liulingtong.enums.ProviderType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileContentProvider implements ContentProvider {
//    private Logger loggerFactory = LoggerFactory.getLogger(this.getClass());

    private File metaFile = null;

    private File dataFile = null;
    private long metaLastModify = -1;
    private long dataLastModify = -1;
    private long version;

    private MappedByteBuffer metaBuffer = null;
    private MappedByteBuffer dataBuffer = null;

    private RandomAccessFile metaAccessFile = null;
    private RandomAccessFile dataAccessFile = null;

    public FileContentProvider(String key) throws IOException {
        boolean success = init(key);
        if (!success) {
            throw new IllegalArgumentException(key);
        }
    }

    @Override
    public boolean write(byte[] meta, byte[] data) {
        writeToFile(meta, metaAccessFile);
        writeToFile(data, dataAccessFile);
        metaLastModify = metaFile.lastModified();
        dataLastModify = dataFile.lastModified();
        return false;
    }

    private void writeToFile(byte[] content, RandomAccessFile file) {
        FileChannel channel = file.getChannel();
        FileLock lock = null;
        try {
            lock = channel.lock();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, file.length());
            map.position(0).put(0, content, 0, content.length);
            map.force();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (lock != null) {
                    lock.release();
                }
            } catch (Exception ignore) {

            }
        }
    }


    @Override
    public byte[] read(MetaDataEnum metaDataEnum) {
        return read(metaDataEnum, 0, metaDataEnum == MetaDataEnum.META ? metaFile.length() : dataFile.length());
    }

    @Override
    public byte[] read(MetaDataEnum metaOrData, long start, long length) {
        File operateFile;
        long fileLastModify;
        RandomAccessFile operateAccessFile;
        MappedByteBuffer operateBuffer;
        if (metaOrData == MetaDataEnum.META) {
            operateFile = metaFile;
            operateAccessFile = metaAccessFile;
            fileLastModify = metaLastModify;
        } else {
            operateFile = dataFile;
            operateAccessFile = dataAccessFile;
            fileLastModify = dataLastModify;
        }
        if (operateFile.lastModified() != fileLastModify) {
            try {
                operateBuffer = operateAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, start, length);
                if (metaOrData == MetaDataEnum.META) {
                    metaBuffer = operateBuffer;
                } else {
                    dataBuffer = operateBuffer;
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        operateBuffer = metaOrData == MetaDataEnum.META ? metaBuffer : dataBuffer;
        return operateBuffer.array();
    }


    @Override
    public boolean init(String key) throws IOException, IllegalArgumentException {
        File file = new File(key);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new IllegalArgumentException("创建目录失败 , dir = " + key);
            }
        }
        metaFile = new File(key, "meta.obj");
        dataFile = new File(key, "data.obj");
        metaLastModify = metaFile.lastModified();
        dataLastModify = dataFile.lastModified();
        metaAccessFile = new RandomAccessFile(metaFile, "rw");
        dataAccessFile = new RandomAccessFile(dataFile, "rw");
        metaBuffer = metaAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, metaAccessFile.length());
        dataBuffer = dataAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, dataAccessFile.length());
        metaFile.createNewFile();
        dataFile.createNewFile();
        return true;
    }

    @Override
    public long getVersion() {
        return metaFile.lastModified() + dataFile.lastModified();
    }

    @Override
    public void updateVersion(long newVersion) {
    }

    @Override
    public ProviderType type() {
        return ProviderType.FILE;
    }

    @Override
    public void close() throws IOException {
        if (metaAccessFile != null) {
            metaAccessFile.close();
        }
        if (dataAccessFile != null) {
            dataAccessFile.close();
        }
    }
}
