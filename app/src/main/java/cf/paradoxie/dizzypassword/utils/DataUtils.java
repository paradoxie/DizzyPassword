package cf.paradoxie.dizzypassword.utils;

import java.text.Collator;
import java.util.ArrayList;
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
     * @param accountBean
     * @return
     */
    public static List<AccountBean> getDataByName(List<AccountBean> accountBean){

        Collections.sort(accountBean, new Comparator<AccountBean>() {
            public int compare(AccountBean accountBean1, AccountBean accountBean2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(DesUtil.decrypt(accountBean1.getName(), SPUtils.getKey()),DesUtil.decrypt(accountBean2.getName(), SPUtils.getKey()));
            }
        });
        return accountBean;
    }

    /**
     * 根据list中条目名称获取符合搜索字符的账户集合，tag和备注里面的也可以搜索到
     * @param accountBeans
     * @param s
     * @return
     */
    public  static List<AccountBean> searchDataByName(List<AccountBean> accountBeans , String s){
        List<AccountBean> mAccountBeans = new ArrayList<>();
        for (AccountBean accountBean:accountBeans){
            if (DesUtil.decrypt(accountBean.getName(),SPUtils.getKey()).contains(s)||accountBean.getTag().contains(s)||
                    DesUtil.decrypt(accountBean.getNote(),SPUtils.getKey()).contains(s)){
                mAccountBeans.add(accountBean);
            }
        }
        return mAccountBeans;
    }

    /**
     * 根据tag搜索，暂时去掉多tag联合检索
     * @param accountBeans
     * @param s
     * @return
     */
    public  static List<AccountBean> searchDataByTag(List<AccountBean> accountBeans , String s){
        List<AccountBean> mAccountBeans = new ArrayList<>();
        for (AccountBean accountBean:accountBeans){
            if (accountBean.getTag().contains(s)){
                mAccountBeans.add(accountBean);
            }
        }
        return mAccountBeans;
    }

}
