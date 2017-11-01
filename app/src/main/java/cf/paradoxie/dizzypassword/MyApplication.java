package cf.paradoxie.dizzypassword;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.bmob.v3.BmobUser;

import static cn.bmob.v3.BmobRealTimeData.TAG;

public class MyApplication extends Application {
    public static MyApplication mInstance;
    public static Context mContext;
    public static boolean isShow = true;
    private static Toast mToast;

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


    public static void showToast(CharSequence message) {

        if (isShow && message != null && !MyApplication.isStrNull(message + ""))
            if (mToast == null) {
                mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(message);
            }
        mToast.show();
    }

    /**
     * 判断为空,可以empty替换??
     *
     * @param str
     * @return
     */
    public static boolean isStrNull(String str) {
        if (null == str) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        } else if ("null".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
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

    /**
     * webview载入网页
     *
     * @param wb  控件
     * @param str 地址
     * @param bar 进度条
     */
    public static void loadUri(WebView wb, int str, final ProgressBar bar) {
        wb.getSettings().setJavaScriptEnabled(true);//支持js
        wb.setWebViewClient(new WebViewClient() {//屏蔽自动浏览器打开
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wb.getSettings().setUseWideViewPort(true);//自适应屏幕
        wb.getSettings().setSupportZoom(true); //支持缩放
        wb.getSettings().setDefaultTextEncodingName("utf-8");//设置编码
        wb.requestFocus();
        wb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        wb.loadUrl(getContext().getString(str));
    }
}
