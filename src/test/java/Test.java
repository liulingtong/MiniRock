import com.alibaba.fastjson2.JSON;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        int intValue = 123112323;
        byte[] byteArray = new byte[]{
                (byte) ((intValue >> 24) & 0xFF),
                (byte) ((intValue >> 16) & 0xFF),
                (byte) ((intValue >> 8) & 0xFF),
                (byte) (intValue & 0xFF)
        };
        System.out.println(Arrays.toString("\0".getBytes(StandardCharsets.UTF_8)));
        System.out.println(Arrays.toString(byteArray));
        HashMap<String, String> aaa = new HashMap<>();
        aaa.put("123", "3456");
        aaa.put("1244", "123333333333");
        System.out.println(JSON.toJSONString(aaa));
    }
}
