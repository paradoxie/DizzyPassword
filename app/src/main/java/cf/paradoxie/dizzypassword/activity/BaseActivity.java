package cf.paradoxie.dizzypassword.activity;/**
 * Created by xiehehe on 15/11/8.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;

/**
 * User: xiehehe
 * Date: 2017-10-28
 * Time: 11:59
 */
public abstract class BaseActivity extends AppCompatActivity {
    private int theme;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreCreate();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        AppManager.getAppManager().addActivity(this);
    }

    private void onPreCreate() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        theme = sp.getInt("theme_change", R.style.Theme7);
        setTheme(theme);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int newTheme = sp.getInt("theme_change", theme);
        if (newTheme != theme) {
            recreate();
        }
    }

    /**
     * 判断网络是否连接
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public  boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 获取活动网络信息
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }
}
