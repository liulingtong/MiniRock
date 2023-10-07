package xyz.liulingtong.provider;

import xyz.liulingtong.enums.MetaDataEnum;
import xyz.liulingtong.enums.ProviderType;

public interface ContentProvider {
    /**
     * 写入数据
     * 文件锁加MappedByteBuffer可以保证多进程写文件的一致性和可见性
     *
     * @param meta 二进制元数据
     * @param data 二进制数据
     */
    boolean write(byte[] meta, byte[] data);

    byte[] read(MetaDataEnum metaDataEnum);
    byte[] read(MetaDataEnum metaDataEnum, long start, long length);

    /**
     * @param key 根据 key 进行初始化。其他文件根据 key 获取数据
     */
    boolean init(String key) throws Exception;


    /**
     * 每次写入之后，对数据版本进行更新，读取数据时，如果本地缓存数据版本和文件版本一致，则直接读取缓存
     * lastModify
     *
     * @return 数据版本
     */
    long getVersion();

    /**
     * 更新版本
     *
     * @param newVersion 更新后的版本
     */
    void updateVersion(long newVersion);

    /**
     * provider 类型
     *
     * @return 类型
     */
    ProviderType type();

    void close() throws Exception;
}
