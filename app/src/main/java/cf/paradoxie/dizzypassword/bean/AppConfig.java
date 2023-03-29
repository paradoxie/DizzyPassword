package cf.paradoxie.dizzypassword.bean;

public class AppConfig {
    //最新版本
    private int version_code;
    //下载链接
    private String appDownLoadUrl;

    //下载链接
    private String buyUrl;

    //头图链接
    private String avatarUrl;

    //格言
    private String maxim;
    //俏皮话
    private String witty;

    public String getWitty() {
        return witty;
    }

    public void setWitty(String witty) {
        this.witty = witty;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getAppDownLoadUrl() {
        return appDownLoadUrl;
    }

    public void setAppDownLoadUrl(String appDownLoadUrl) {
        this.appDownLoadUrl = appDownLoadUrl;
    }

    public String getBuyUrl() {
        return buyUrl;
    }

    public void setBuyUrl(String buyUrl) {
        this.buyUrl = buyUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMaxim() {
        return maxim;
    }

    public void setMaxim(String maxim) {
        this.maxim = maxim;
    }

    public AppConfig(int version_code, String appDownLoadUrl, String buyUrl, String avatarUrl, String maxim,String witty) {
        this.version_code = version_code;
        this.appDownLoadUrl = appDownLoadUrl;
        this.buyUrl = buyUrl;
        this.avatarUrl = avatarUrl;
        this.maxim = maxim;
        this.witty = witty;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "version_code=" + version_code +
                ", appDownLoadUrl='" + appDownLoadUrl + '\'' +
                ", buyUrl='" + buyUrl + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", maxim='" + maxim + '\'' +
                ", witty='" + witty + '\'' +
                '}';
    }
}
