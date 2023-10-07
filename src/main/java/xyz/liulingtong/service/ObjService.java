package xyz.liulingtong.service;

public interface ObjService<T> {

    T get(String key);

    void set(String key, T data);

    static class Meta {
        private static Long offset;
        private static Long length;

    }
}
