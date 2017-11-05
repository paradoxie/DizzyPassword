package cf.paradoxie.dizzypassword.utils;

import java.util.Comparator;

import cf.paradoxie.dizzypassword.db.AccountBean;

/**
 * Created by xiehehe on 2017/11/4.
 */

public class SortByTime implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        AccountBean bean1 = (AccountBean) o;
        AccountBean bean2 = (AccountBean) t1;
        int flag = bean1.getUpdatedAt().compareTo(bean2.getUpdatedAt());
        return flag;
    }

}
