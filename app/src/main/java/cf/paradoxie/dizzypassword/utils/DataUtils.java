package cf.paradoxie.dizzypassword.utils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
