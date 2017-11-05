package cf.paradoxie.dizzypassword.utils;

import android.content.Context;
import android.content.Intent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by xiehehe on 2017/10/29.
 */

public class DesUtil {

    // 密钥，是加密解密的凭据
    private static String PASSWORD_CRYPT_KEY = "";//长度为8的倍数
    private final static String DES = "DES";

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {

        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);

    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(src);

    }

    /**
     * 密码解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data, String key) {

        try {
            return new String(decrypt(hex2byte(data.getBytes()), key.getBytes()));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 密码加密
     *
     * @param password
     * @return
     * @throws Exception
     */
    public final static String encrypt(String password, String key) {
        try {
            return byte2hex(encrypt(password.getBytes(), key.getBytes()));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {

        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    // 测试用例，不需要传递任何参数，直接执行即可。
    //    public static void main(String[] args) {
    //        String basestr = "this is 我的 #$%^&()first encrypt program 知道吗?DES算法要求有一个可信任的随机数源 --//*。@@@1";
    //        String str1 = encrypt(basestr);
    //
    //        System.out.println("密钥: " + getKey());
    //        System.out.println("原始值: " + basestr);
    //        System.out.println("加密后: " + str1);
    //        System.out.println("解密后: " + decrypt(str1));
    //        System.out.println("为空时 is : " + decrypt(encrypt("")));
    //    }

    /**
     * 从数组总随机取出一定数量的值，组成新的数组
     *
     * @param array 原始数组
     * @param count 需要的数量
     * @return
     */
    public static Integer[] getRandomFromArray(Integer[] array, int count) {
        // ArrayList<Integer>arrayList =null;
        Integer[] a = array;
        Integer[] result = new Integer[count];
        Random random = new Random();
        int m = count; // 要随机取的元素个数
        if (m > a.length) {
            a = getMoreArray(a, m);
        }
        boolean r[] = new boolean[a.length];
        int n = 0;
        while (true) {
            int temp = random.nextInt(a.length);
            if (!r[temp]) {
                if (n == m) // 取到足量随机数后退出循环
                    break;
                n++;
                //                System.out.println("得到的第" + n + "个随机数为：" + a[temp]);
                result[n - 1] = a[temp];
                r[temp] = true;
            }
        }
        //        Log.i("array", String.valueOf(result.length));
        return result;

    }

    private static Integer[] getMoreArray(Integer[] array, int count) {
        // ArrayList<Integer>arrayList =null;
        Random r = new Random(array.length);
        List list = Arrays.asList(array);
        ArrayList newList = new ArrayList<>(list);
        if (count > newList.size()) {
            newList.addAll(newList);
            newList.addAll(newList);
            newList.addAll(newList);
            newList.addAll(newList);
            newList.addAll(newList);
            newList.addAll(newList);
        }
        Integer[] a = (Integer[]) newList.toArray(new Integer[newList.size()]);
        return a;
    }

    /**
     * 验证邮箱的合法性
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        return str.matches("^[\\w-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 分享功能
     *
     * @param context
     * @param Title
     */
    public static void share(Context context, String Title) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_TEXT, Title);
        context.startActivity(Intent.createChooser(share, "分享到"));
    }

    /**
     * List转string
     *
     * @param list
     * @param separator
     * @return
     */
    public static String listToString(List list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    // 测试用例，不需要传递任何参数，直接执行即可。
    //    public static void main(String[] args) {
    //        List<String> list = new ArrayList<>();
    //        list.add("购物");
    //        list.add("当当");
    //        list.add("微博");
    //        list.add("豆瓣");
    //        list.add("小米");
    //        System.out.println(listToString(list," "));
    //    }
}
