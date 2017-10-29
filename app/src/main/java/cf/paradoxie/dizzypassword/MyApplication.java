package cf.paradoxie.dizzypassword;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.bmob.v3.BmobUser;

import static cn.bmob.v3.BmobRealTimeData.TAG;

public class MyApplication extends Application {
    public static MyApplication mInstance;
    public static Context mContext;

    @Override
    public void onCreate() {
        Log.d(TAG, "[MyApplication] onCreate");
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
    }

    //返回
    public static Context getContext() {
        return mContext;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    private String getKey() {
        return SPUtils.get("key", "") + "";
    }


    public static void showToast(String s) {
        Toast.makeText(MyApplication.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    //检验是否存在本地用户，返回true/false
    public static boolean isSign() {
        if (getUser() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static BmobUser getUser() {
        BmobUser bmobUser = BmobUser.getCurrentUser();
        return bmobUser;
    }
}
