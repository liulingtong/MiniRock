package xyz.liulingtong.service;

import xyz.liulingtong.provider.ContentProvider;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class ObjService<T> {
    private ContentProvider provider;
    private long dataVersion;

    private final ConcurrentHashMap<String, Meta> metaMap = new ConcurrentHashMap<>();

    public ObjService(ContentProvider provider) {
        this.provider = provider;
        this.dataVersion = provider.version();
    }


    public T get(String key) {
        return null;
    }

    public void set(T t) {

    }

    private void parseMeta() {
        if (dataVersion != provider.version()) {

        }

    }

    private byte[] serializeObj(String key, T data) throws IllegalAccessException {
        Field[] fields = data.getClass().getFields();
        StringBuilder result = new StringBuilder();
        for (Field field : fields) {
            field.setAccessible(true);
            result.append("\0");
            switch (field.getName()) {
                case "java.lang.String", "java.lang.Integer" -> result.append(field.get(data));
                default -> result.append("@").append(key).append(field.getName());
            }
        }
        return new byte[0];
    }

    private static class Meta {
        private static Long offset;
        private static Long length;
    }
}
