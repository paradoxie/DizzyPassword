package cf.paradoxie.dizzypassword.base;

/**
 * Created by xiehehe on 2018/5/10.
 */

public class Constans {
    public static final String APPLICATION_ID = "46b1709520ec4d0afa17e505680202da";//正式
    public static final String CONFIG_ID = "1590689a00";//正式
    public static final String WORDS_ID = "b47f703f38";//正式
    //    public static final String PIC_ID = "http://gank.io/api/data/福利/";//正式
    public static final String PIC_ID = "https://api.66mz8.com/api/rand.img.php?type=%E7%BE%8E%E5%A5%B3&format=json";//正式
    public static final String WORDS_ID_CHICKEN = "http://www.dutangapp.cn/u/toxic?date=";//正式

    public static final String QQ_ID = "UhZSiApvdWYS2rVVQAsAFv7eqo_8MyJQ";
    //名人名言
    public static final String WORDS = "http://api.tianapi.com/txapi/dictum/index?key=40712b0313e1cce334f4442655bf6d64&num=1";

    public static String pic_url = "https://ghproxy.com/https://raw.githubusercontent.com/lingyia/APIIMG/master/pure/";

    public static String APP_CONFIG_URL = "";
    public static String AD = "";
    public static String UN_BACK = "UN_BACK";

    public static String getUrl() {
        int num = (int) (Math.random() * 1000);
        return pic_url + num + ".jpg";
    }
}
