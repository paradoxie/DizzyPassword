package cf.paradoxie.dizzypassword.bean;

/**
 * Created by xiehehe on 2017/10/29.
 */

public class RxBean {


    private String message;
    private String action;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    private String pwd;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
