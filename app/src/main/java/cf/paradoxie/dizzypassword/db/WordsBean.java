package cf.paradoxie.dizzypassword.db;

import cn.bmob.v3.BmobObject;

/**
 * Created by xiehehe on 2018/5/9.
 */

public class WordsBean extends BmobObject {
    private String famous_name;
    private String famous_saying;

    public String getFamous_name() {
        return famous_name;
    }

    public void setFamous_name(String famous_name) {
        this.famous_name = famous_name;
    }

    public String getFamous_saying() {
        return famous_saying;
    }

    public void setFamous_saying(String famous_saying) {
        this.famous_saying = famous_saying;
    }
}
