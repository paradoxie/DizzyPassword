package cf.paradoxie.dizzypassword.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;

/**
 * 通用工具类
 */
public class Utils {
    /**
     * 复制粘贴
     *
     * @param activity
     * @param v
     * @param s
     */
    public static void copy(Activity activity, View v, CharSequence s) {
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(s);
        MyApplication.showSnack(v, R.string.str_copy, ThemeUtils.getPrimaryColor(activity));
    }

    /**
     * webview载入网页
     *
     * @param wb  控件
     * @param str 地址
     * @param bar 进度条
     */
    public static void loadUri(final WebView wb, int str, String url, final ProgressBar bar) {
        wb.getSettings().setJavaScriptEnabled(true);//支持js
        wb.setWebViewClient(new WebViewClient() {//屏蔽自动浏览器打开
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wb.getSettings().setUseWideViewPort(true);//自适应屏幕
        wb.getSettings().setDomStorageEnabled(true);
        wb.getSettings().setAllowFileAccess(true);
        wb.getSettings().setAppCacheEnabled(true);
        wb.getSettings().setSupportZoom(true); //支持缩放
        wb.getSettings().setDefaultTextEncodingName("utf-8");//设置编码
        wb.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wb.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //处理webview内部返回事件
        wb.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                //按返回键操作并且能回退网页
                if (i == KeyEvent.KEYCODE_BACK && wb.canGoBack()) {
                    //后退
                    wb.goBack();
                    return true;
                }
            }

            return false;
        });
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
        if (url == null) {
            wb.loadUrl(MyApplication.getContext().getString(str));
        } else {
            wb.loadUrl(url);
        }
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

    /**
     * 获取版本号名称
     *
     * @return
     */
    public static String GetVersionName() {
        PackageInfo pi = null;
        try {
            pi = MyApplication.getContext().getPackageManager().getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            String versionName = pi.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int GetVersion() {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = MyApplication.getContext()
                    .getPackageManager()
                    .getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static void loadImg(ImageView imageView, String imgUrl, boolean a) {

        if (null == imageView || TextUtils.isEmpty(imgUrl)) {
            return;
        }

        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(30);
        //通过RequestOptions扩展功能
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300).circleCrop();
        if (a) {
            Glide.with(MyApplication.getContext())
                    .load(imgUrl)
                    .apply(options)
                    .error(R.mipmap.ic_logo)
                    .placeholder(R.mipmap.ic_loading)
                    .into(imageView);
        } else {
            Glide.with(MyApplication.getContext())
                    .load(imgUrl)
//                .apply(options)
                    .error(R.mipmap.ic_logo)
                    .placeholder(R.mipmap.ic_loading)
                    .into(imageView);
        }

    }

    public static String getData() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);

        return dateNowStr;
    }
    /**
     * 跳转默认浏览器打开
     *
     * @param context
     * @param url
     */
    public static void jumpToDefaultBrowser(Activity context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static String getCodePwd(String string) {
        if (string.isEmpty() || string == null) {
            return null;
        } else {
            return replaceAction(string, "(?<=\\w{2})\\w(?=\\w{2})");
        }
    }

    private static String replaceAction(String username, String regular) {
        return username.replaceAll(regular, "*");
    }

}
