package cf.paradoxie.dizzypassword.base;

/**
 * Created by xiehehe on 2018/5/10.
 */

public class Constants {
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

    public static String APP_CONFIG_URL = "https://docs.qq.com/doc/DTWR2ZXBIdHJkZURO";
    public static String AD = "http://fluff.paradoxie.top:2334/pwd/getCommonPwds";
    public static String UN_BACK = "UN_BACK";
    public static final String IS_KEY_FOR_PWD = "isKeyForPwd";
    public static final String IS_KEY_FOR_PWD_DAY = "isKeyForPwdDay";
    public static final String WEBDAV_SERVER = "jianguo_server";
    public static final String WEBDAV_ACCOUNT = "jianguo_account";
    public static final String WEBDAV_PWD = "jianguo_pwd";
    public static final String INFINI_CLOUD_URL = "https://rbtzzwgihc.feishu.cn/docx/CfPHdxZFAo4nsxxevwBctxUmnUc";
    public static final String JIANGUO_URL = "https://rbtzzwgihc.feishu.cn/docx/RUjod6wjCoOZH3xSd6gcOGvNnSh";

    public static String getUrl() {
        int num = (int) (Math.random() * 1000);
        return pic_url + num + ".jpg";
    }
}
