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
    private String windowJumpPackage;
    private String windowDetailsContent;
    private String windowCopyContent;
    private String windowTitle;
    private String windowConfirm;
    private String riceUrl;
    private boolean iconRotate;

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

    public String getWindowJumpPackage() {
        return windowJumpPackage;
    }

    public void setWindowJumpPackage(String windowJumpPackage) {
        this.windowJumpPackage = windowJumpPackage;
    }

    public String getWindowDetailsContent() {
        return windowDetailsContent;
    }

    public void setWindowDetailsContent(String windowDetailsContent) {
        this.windowDetailsContent = windowDetailsContent;
    }

    public String getWindowCopyContent() {
        return windowCopyContent;
    }

    public void setWindowCopyContent(String windowCopyContent) {
        this.windowCopyContent = windowCopyContent;
    }

    public String getRiceUrl() {
        return riceUrl;
    }

    public void setRiceUrl(String riceUrl) {
        this.riceUrl = riceUrl;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getWindowConfirm() {
        return windowConfirm;
    }

    public void setWindowConfirm(String windowConfirm) {
        this.windowConfirm = windowConfirm;
    }

    public boolean isIconRotate() {
        return iconRotate;
    }

    public void setIconRotate(boolean iconRotate) {
        this.iconRotate = iconRotate;
    }
}
