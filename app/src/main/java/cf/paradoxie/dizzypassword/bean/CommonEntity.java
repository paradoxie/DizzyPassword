package cf.paradoxie.dizzypassword.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class CommonEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String info;
    private String extraInfo;
    private String picUrl;
    private String jumpUrl;
    private int type;
    private int rankNum;
    private boolean  banner;

    public CommonEntity(int id, String name, String info, String extraInfo, String picUrl, String jumpUrl, int type, int rankNum, boolean banner) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.extraInfo = extraInfo;
        this.picUrl = picUrl;
        this.jumpUrl = jumpUrl;
        this.type = type;
        this.rankNum = rankNum;
        this.banner = banner;
    }

    @Ignore
    public CommonEntity(String name, String info, String extraInfo, String picUrl, String jumpUrl, int type, int rankNum, boolean banner) {
        this.name = name;
        this.info = info;
        this.extraInfo = extraInfo;
        this.picUrl = picUrl;
        this.jumpUrl = jumpUrl;
        this.type = type;
        this.rankNum = rankNum;
        this.banner = banner;
    }


    @Ignore
    public CommonEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRankNum() {
        return rankNum;
    }

    public void setRankNum(int rankNum) {
        this.rankNum = rankNum;
    }

    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }

    @Override
    public String toString() {
        return "CommonEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", extraInfo='" + extraInfo + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                ", type=" + type +
                ", rankNum=" + rankNum +
                ", banner=" + banner +
                '}';
    }


}
