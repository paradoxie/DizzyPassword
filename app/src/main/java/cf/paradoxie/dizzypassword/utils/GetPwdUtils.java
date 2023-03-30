package cf.paradoxie.dizzypassword.utils;

import java.util.Random;

/**
 * Created by xiehehe on 2017/12/5.
 */

public class GetPwdUtils {
    /**
     * 获取密码
     * @param length 长度
     * @param str 传入类型
     * @return
     */
    public static String getPwd(int length,String str) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }
}
