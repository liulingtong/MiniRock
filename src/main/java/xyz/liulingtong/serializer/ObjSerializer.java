package xyz.liulingtong.serializer;

public interface ObjSerializer {
    byte[] serialize(Object data);

    <T> T deserialize(byte[] data, Class<T> tClass);
}
