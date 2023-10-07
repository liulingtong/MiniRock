package xyz.liulingtong.service;

import xyz.liulingtong.enums.DataType;
import xyz.liulingtong.enums.MetaDataEnum;
import xyz.liulingtong.model.DataLocation;
import xyz.liulingtong.model.DataMeta;
import xyz.liulingtong.provider.ContentProvider;
import xyz.liulingtong.provider.FileContentProvider;
import xyz.liulingtong.serializer.FastJsonObjSerializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FastJsonObjService<T> {
    private final ContentProvider provider;
    private long cacheDataVersion;
    private Class<T> clazz;

    private FastJsonObjSerializer fastJsonObjSerializer;
    private ConcurrentHashMap<String, DataMeta> metaMap = new ConcurrentHashMap<>();

    public FastJsonObjService(ContentProvider provider) {
        this.provider = provider;
        this.cacheDataVersion = provider.getVersion();
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                clazz = (Class<T>) actualTypeArguments[0];
            }
        }
    }


    public T get(String key) {
        ConcurrentHashMap<String, DataMeta> meta = getMeta();
        DataMeta dataMeta = meta.get(key);
        if (dataMeta == null) {
            return null;
        } else {
            if (dataMeta.getDataType() != DataType.OBJECT) {
                throw new IllegalArgumentException("使用了错误的方法 , 对象类型为" + dataMeta.getDataType().name());
            } else {
                List<DataLocation> dataLocations = dataMeta.getDataLocations();
                if (dataLocations.isEmpty()) {
                    throw new IllegalArgumentException("对象数据为空 , key : " + key);
                } else {
                    DataLocation dataLocation = dataLocations.get(dataLocations.size() - 1);
                    byte[] read = this.provider.read(MetaDataEnum.DATA, dataLocation.getOffset(), dataLocation.getSize());
                    T deserialize = fastJsonObjSerializer.deserialize(read, clazz);
                    return deserialize;
                }
            }
        }
    }

    public void set(T t) {

    }

    private ConcurrentHashMap<String, DataMeta> getMeta() {
        if (cacheDataVersion != provider.getVersion()) {
            byte[] read = this.provider.read(MetaDataEnum.META);
            metaMap = (ConcurrentHashMap<String, DataMeta>) fastJsonObjSerializer.deserialize(read, ConcurrentHashMap.class);
        }
        return metaMap;
    }

    private void updateMeta() {

    }

    public static void main(String[] args) throws IOException {
        FastJsonObjService<String> stringFastJsonObjService = new FastJsonObjService<>(new FileContentProvider("123")){};
        System.out.println(stringFastJsonObjService.clazz.getTypeName());
    }
}
