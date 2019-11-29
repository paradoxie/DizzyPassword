package cf.paradoxie.dizzypassword.utils;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cf.paradoxie.dizzypassword.db.AccountBean;

/**
 * Created by xiehehe on 2017/12/13.
 */

public class DataUtils {
    private static List<Map.Entry<String, Integer>> sMappingList;
    private static List<Map.Entry<String, Integer>> sMappingListName;

    /**
     * 标签操作
     * 获得频率最高的标签及重复次数
     *
     * @param s 传入的tag数组
     * @return map
     */
    public static ArrayList<Map.Entry<String, Integer>> getTagList(List<Map.Entry<String, Integer>> mappingList, List s) {
        sMappingList = mappingList;
        //        Map<String, Integer> tagMap = new HashMap<>();
        Map map = new HashMap();
        for (Object temp : s) {
            Integer count = (Integer) map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }

        sMappingList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //通过比较器实现比较排序
        Collections.sort(sMappingList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> mapping1, Map.Entry<String, Integer> mapping2) {
                return mapping2.getValue().compareTo(mapping1.getValue());
            }
        });
        return (ArrayList<Map.Entry<String, Integer>>) sMappingList;
    }

    /**
     * 标签操作
     * 获得所有标签及重复次数
     * 按名称排序
     *
     * @param s 传入的tag数组
     * @return map
     */
    public static ArrayList<Map.Entry<String, Integer>> getTagListByName(List<Map.Entry<String, Integer>> mappingList, List s) {
        sMappingListName = mappingList;
        Map map = new HashMap();
        for (Object temp : s) {
            Integer count = (Integer) map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }
        sMappingListName = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //通过比较器实现比较排序
        Collections.sort(sMappingListName, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> mapping1, Map.Entry<String, Integer> mapping2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(mapping1.getKey(), mapping2.getKey());
            }
        });
        return (ArrayList<Map.Entry<String, Integer>>) sMappingListName;
    }

    /**
     * 将条目按照名称排序
     *
     * @param accountBean
     * @return
     */
    public static List<AccountBean> getDataByName(List<AccountBean> accountBean) {

        Collections.sort(accountBean, new Comparator<AccountBean>() {
            public int compare(AccountBean accountBean1, AccountBean accountBean2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(DesUtil.decrypt(accountBean1.getName(), SPUtils.getKey()), DesUtil.decrypt(accountBean2.getName(), SPUtils.getKey()));
            }
        });
        return accountBean;
    }

    /**
     * 根据list中条目名称获取符合搜索字符的账户集合，tag和备注里面的也可以搜索到
     *
     * @param accountBeans
     * @param s
     * @return
     */
    public static List<AccountBean> searchDataByName(List<AccountBean> accountBeans, String s) {
        List<AccountBean> mAccountBeans = new ArrayList<>();
        for (AccountBean accountBean : accountBeans) {
            if (DesUtil.decrypt(accountBean.getName(), SPUtils.getKey()).contains(s) || accountBean.getTag().contains(s) ||
                    DesUtil.decrypt(accountBean.getNote(), SPUtils.getKey()).contains(s)) {
                mAccountBeans.add(accountBean);
            }
        }
        return mAccountBeans;
    }

    /**
     * 根据tag搜索，暂时去掉多tag联合检索
     *
     * @param accountBeans
     * @param s
     * @return
     */
    public static List<AccountBean> searchDataByTag(List<AccountBean> accountBeans, String s) {
        List<AccountBean> mAccountBeans = new ArrayList<>();
        for (AccountBean accountBean : accountBeans) {
            if (accountBean.getTag().contains(s)) {
                mAccountBeans.add(accountBean);
            }
        }
        return mAccountBeans;
    }

    /**
     * 密码三个月未更新标红
     *
     * @param updated
     * @return
     */
    public static boolean shouldChange(String updated) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long updateTime = 0;
        try {
            updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentTime - updateTime > 7776000000L) {
            return true;
        }
        return false;
    }

    /**
     * 密码等级
     *
     * @param Password
     * @return
     */
    public static String GetPwdSecurityLevel(String Password) {
        int sum = 0;
        sum += LengthAdd(Password);
        sum += LetterAdd(Password);
        sum += IntegerAdd(Password);
        sum += SymbolAdd(Password);
        sum += AwardAdd(Password);
        if (sum >= 90)
            return "很安全";
        else if (sum >= 80)
            return "安全";
        else if (sum >= 70)
            return "很强";
        else if (sum >= 60)
            return "强";
        else if (sum >= 40)
            return "一般";
        else if (sum >= 25)
            return "弱";
        return "很弱";
    }

    private static int LengthAdd(String Password) {
        if (Password.length() <= 6)
            return 5;
        else if (Password.length() >= 7 && Password.length() <= 9)
            return 10;
        else if (Password.length() >= 10 && Password.length() <= 13)
            return 15;
        else if (Password.length() >= 14 && Password.length() <= 18)
            return 20;
        else if (Password.length() >= 19 && Password.length() <= 23)
            return 25;
        else if (Password.length() >= 24 && Password.length() <= 30)
            return 30;
        else if (Password.length() >= 31 && Password.length() <= 35)
            return 35;
        else if (Password.length() >= 36 && Password.length() <= 42)
            return 40;
        else if (Password.length() >= 43 && Password.length() <= 46)
            return 45;
        return 50;
    }

    private static int LetterAdd(String Password) {
        int UpperLetter = 0;
        int LowerLetter = 0;
        for (int i = 0; i < Password.length(); i++) {
            String ch = String.valueOf(Password.charAt(i));
            if (ch.matches("[A-Z]"))
                UpperLetter++;
            else if (ch.matches("[a-z]"))
                LowerLetter++;
        }
        if (UpperLetter != 0 && LowerLetter != 0)
            return 27;
        else if ((UpperLetter == 0 && LowerLetter != 0) || (UpperLetter != 0 && LowerLetter == 0))
            return 15;
        return 0;
    }

    private static int IntegerAdd(String Password) {
        int NumOfInteger = 0;
        for (int i = 0; i < Password.length(); i++) {
            if (Password.charAt(i) >= '0' && Password.charAt(i) <= '9')
                NumOfInteger++;
        }
        if (NumOfInteger == 0)
            return 0;
        else if (NumOfInteger == 3)
            return 3;
        else if (NumOfInteger == 6)
            return 6;
        else if (NumOfInteger == 9)
            return 9;
        else if (NumOfInteger == 12)
            return 12;
        else if (NumOfInteger == 15)
            return 15;
        else if (NumOfInteger == 18)
            return 18;
        return 21;
    }

    private static int SymbolAdd(String Password) {
        int NumOfSymbol = 0;
        for (int i = 0; i < Password.length(); i++) {
            char ch = Password.charAt(i);
            if ((ch >= 0x21 && ch <= 0x2F) || (ch >= 0x3A && ch <= 0x40) || (ch >= 0x5B && ch <= 0x60) || (ch >= 0x7B && ch <= 0x7E))
                NumOfSymbol++;
        }
        if (NumOfSymbol == 0)
            return 0;
        else if (NumOfSymbol == 1)
            return 2;
        else if (NumOfSymbol == 3)
            return 5;
        else if (NumOfSymbol == 5)
            return 9;
        else if (NumOfSymbol == 7)
            return 13;
        else if (NumOfSymbol == 9)
            return 17;
        else if (NumOfSymbol == 11)
            return 20;
        return 24;
    }

    private static int AwardAdd(String Password) {
        int LetterNum = LetterAdd(Password);
        int IntegerNum = IntegerAdd(Password);
        int SymbolNum = SymbolAdd(Password);
        if (LetterNum != 0 && IntegerNum != 0 && SymbolNum == 0)
            return 6;
        else if (LetterNum == 10 && IntegerNum != 0 && SymbolNum != 0)
            return 9;
        else if (LetterNum == 15 && IntegerNum != 0 && SymbolNum != 0)
            return 13;
        else if (LetterNum == 20 && IntegerNum != 0 && SymbolNum != 0)
            return 17;
        else if (LetterNum == 25 && IntegerNum != 0 && SymbolNum != 0)
            return 21;
        else if (LetterNum == 30 && IntegerNum != 0 && SymbolNum != 0)
            return 24;
        else if (LetterNum == 35 && IntegerNum != 0 && SymbolNum != 0)
            return 27;
        return 0;
    }

}
