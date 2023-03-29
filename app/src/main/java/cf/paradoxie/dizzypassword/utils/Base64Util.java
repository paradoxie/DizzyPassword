package cf.paradoxie.dizzypassword.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Util {
    /*编码*/
    public static String encodeBase64(String encodeStr){
        if (encodeStr == null) return null;
        try {
            byte[] b = Base64.encode(encodeStr.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }
    /*解码*/
    public static String decodeBase64(String decodeStr){
        if (decodeStr == null) return null;
        try {
            byte[] b = Base64.decode(decodeStr, Base64.DEFAULT);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }


}
