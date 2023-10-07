package xyz.liulingtong.serializer;

public class SerializerTest<T> extends GenericType2<T> {

    public SerializerTest() {

    }

    public static void main(String[] args) {
        GenericType2<String> genericClass = new SerializerTest<String>(){};
        Class<String> stringClass = genericClass.getTClass();
        System.out.println(stringClass); // 输出：class java.lang.String

    }
}
