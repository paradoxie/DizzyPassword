package cf.paradoxie.dizzypassword.http;

import java.io.Serializable;

/**
 * Created by actlistener on 2018/4/8.
 */

public class ResponseMap<T> implements Serializable {

    public static final int STATUS_SUCCESS = 0;

    private int code = -1;

    private String msg = "";

    private T data = null;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
