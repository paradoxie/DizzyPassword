package cf.paradoxie.dizzypassword.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import cf.paradoxie.dizzypassword.activity.TeachActivity;
import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.bean.CommonEntity;

/**
 * - [x] 导航跳转类型定义：
 * 0、跳转URL 浏览器web 页面
 * 1、弹窗显示/复制内容
 * 2、直接跳转外部APP，跳转响应app
 * 3、跳转本APP页面,活动详情，可自由复制内容，
 * 4、跳转本APP web 页面
 * <p>
 * 注意！！！！
 * app 的跳转需要在manifest声明包名，不然检查不到已安装！！！！！！
 */
public class JumpUtil {


    /**
     * 跳转
     *
     * @param context
     */
    public static void jump(Activity context, CommonEntity bean) {
        switch (bean.getType()) {
            case 0://默认浏览器打开
                MyApplication.showToast("正在打开");
                jumpToDefaultBrowser(context, bean.getJumpUrl());
                break;
            case 1://弹窗
                MyApplication.showToast("这是" + bean.getInfo());
                break;
            case 2://直接跳转外部APP，跳转响应app
                new Handler().postDelayed(() -> {
                    jumpToApp(context, bean.getJumpUrl());
                }, 300);//3秒后执行Runnable中的run方法

                break;
            case 3://跳转本APP页面,活动详情activity
                MyApplication.showToast("这是" + bean.getInfo());
                break;
            case 4://跳转本APP web 页面
                jumpToWebActivity(context, bean);
                break;
            default:
                break;
        }
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

    /**
     * 跳转打开APP
     *
     * @param context
     * @param url
     */
    private static void jumpToApp(Activity context, String url) {
        if (isAppInstalled(context, url)) {
            Intent intent2 = context.getPackageManager().getLaunchIntentForPackage(url); //这行代码比较重要
            intent2.setAction("android.intent.action.VIEW");
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        } else {
            MyApplication.showToast("未安装相应APP");
        }
    }

    private static boolean isAppInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName)) return false;
        try {
            //手机已安装，返回true
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            //手机未安装，跳转到应用商店下载，并返回false
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
            return false;
        }
    }

    /**
     * 跳转到加载web页面的activity
     *
     * @param navBean
     */
    private static void jumpToWebActivity(Activity context, CommonEntity navBean) {
        Intent intent = new Intent(AppManager.getAppManager().currentActivity(), TeachActivity.class);
        intent.putExtra("bean", navBean);
        context.startActivity(intent);
    }

    private static void openDialog(Activity context, CommonEntity navBean) {

    }

    /**
     * webview载入网页
     *
     * @param wb  控件
     * @param str 地址
     * @param bar 进度条
     */
    public static void loadUri(Context context, final WebView wb, int str, String url, final ProgressBar bar) {
        wb.getSettings().setJavaScriptEnabled(true);//支持js
        wb.setWebViewClient(new WebViewClient() {//屏蔽自动浏览器打开
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebView.HitTestResult hit = view.getHitTestResult();
                //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
                if (TextUtils.isEmpty(hit.getExtra()) || hit.getType() == 0) {
                    //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                    Log.e("重定向", "重定向: " + hit.getType() + " && EXTRA（）" + hit.getExtra() + "------");
                    Log.e("重定向", "GetURL: " + view.getUrl() + "\n" + "getOriginalUrl()" + view.getOriginalUrl());
                    Log.d("重定向", "URL: " + url);
                }

                if (url.startsWith("http://") || url.startsWith("https://")) { //加载的url是http/https协议地址
                    view.loadUrl(url);
                    return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走

                } else { //加载的url是自定义协议地址
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

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

    private void showDialog() {
//        MessageDialog.show("口令复制成功", "请", "确认").setCancelable(false).setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
//            @Override
//            public boolean onClick(MessageDialog baseDialog, View v) {
////                JumpUtil.jumpToDefaultBrowser(WelcomeActivity.this, appConfig.getUrl());
//                return true;
//            }
//        });
    }
}
