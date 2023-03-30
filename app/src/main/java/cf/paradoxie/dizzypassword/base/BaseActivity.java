package cf.paradoxie.dizzypassword.base;/**
 * Created by xiehehe on 15/11/8.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;

/**
 * User: xiehehe
 * Date: 2017-10-28
 * Time: 11:59
 */
public abstract class BaseActivity extends AppCompatActivity {
    private int theme;
    protected Activity SELF = null;
    private SharedPreferences sp;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        SELF = this;
        onPreCreate();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        AppManager.getAppManager().addActivity(this);
    }

    private void onPreCreate() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        theme = sp.getInt("theme_change", getDefaultTheme());
        setTheme(theme);

    }

    private int getDefaultTheme() {
        int theme;
        int[] a = new int[]{R.style.Theme1, R.style.Theme2, R.style.Theme3, R.style.Theme4, R.style.Theme5, R.style.Theme6, R.style.Theme7, R.style.Theme8,
                R.style.Theme9, R.style.Theme10, R.style.Theme11, R.style.Theme15, R.style.Theme16, R.style.Theme17, R.style.Theme19
        };
        int index = (int) (Math.random() * a.length);
        theme = a[index];
        return theme;
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
    public boolean isConnected() {
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

    /**
     * 隐藏软键盘
     */
    public void hideInputWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    protected <T extends ViewDataBinding> T getBinding(@LayoutRes int layoutId) {
        return DataBindingUtil.setContentView(this, layoutId);
    }

    public void gotoActivity(Class clazz) {
        startActivity(new Intent(SELF, clazz));
    }

}
