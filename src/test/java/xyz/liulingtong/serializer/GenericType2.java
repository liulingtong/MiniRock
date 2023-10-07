package xyz.liulingtong.serializer;

import java.lang.reflect.ParameterizedType;

public abstract class GenericType2<T> {

    public Class<T> class2;

    public Class<T> getTClass() {
        class2 = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return class2;
    }

    public void useGenericType() {
        System.out.println(getTClass());
    }

    public static void main(String[] args) {
        //  new GenericType2<String>() {}; 匿名内部类
        GenericType2<String> stringGenericType = new SerializerTest<>(){};
        stringGenericType.useGenericType();
    }
}
