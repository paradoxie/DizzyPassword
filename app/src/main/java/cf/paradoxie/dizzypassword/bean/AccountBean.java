package cf.paradoxie.dizzypassword.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class AccountBean extends BmobObject implements Comparable {
    private String id;
    private String name;//名字
    private List<String> tag;//标记
    private String account;//帐号
    private String password;//密码
    private String website;//网址
    private String note;//备注


    public void setCreateAtTime(String time) {
        super.setCreatedAt(time);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private BmobUser user;//需要关联的一个用户

    public List<String> getTag() {
        return tag;
    }

    public String getNote() {
        return note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "AccountBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tag=" + tag +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", website='" + website + '\'' +
                ", note='" + note + '\'' +
                ", user=" + user +
                ", createTime=" + getCreatedAt() +
                ", updateTime=" + getUpdatedAt() +
                ", objectId=" + getObjectId() +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return getUpdatedAt().compareTo(((AccountBean) o).getUpdatedAt());
    }
}
