package cf.paradoxie.dizzypassword.db;

import cn.bmob.v3.BmobObject;

/**
 * Created by xiehehe on 2018/5/9.
 */

public class BaseConfig extends BmobObject {
    private String newVersion;
    private String title;
    private String details;
    private String toast;

    public String getToast() {
        return toast;
    }

    public void setToast(String toast) {
        this.toast = toast;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }
}
