package cf.paradoxie.dizzypassword;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import cf.paradoxie.dizzypassword.activity.CrashLogActivity;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.bmob.v3.BmobUser;

import static cn.bmob.v3.BmobRealTimeData.TAG;

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {
    public static MyApplication mInstance;
    public static Context mContext;
    public static int first_check = 0;
    public static boolean isShow = true;
    private static Toast mToast;


    @Override
    public void onCreate() {
        Log.d(TAG, "----------------------[MyApplication] onCreate-------------------");
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(this);//开启抓取错误信息
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        CrashLogActivity.start(this, e);
        System.exit(1);
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


    /**
     * 显示Toast
     *
     * @param message
     */
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
     * 显示Snack
     *
     * @param view
     * @param s
     */
    public static void showSnack(View view, int s, int color) {
        Snackbar snackbar = Snackbar.make(view, s, Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        mView.setBackgroundColor(color);
        snackbar.show();
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


    /**
     * 根据包名打开第三方应用
     *
     * @param context
     * @param packageName
     * @throws PackageManager.NameNotFoundException
     */
    public static void openAppByPackageName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo pi;
        try {
            pi = MyApplication.getContext().getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = MyApplication.getContext().getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//重点是加这个
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static String GetNetworkType() {
        String strNetworkType = "";

        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

            }
        }

        return strNetworkType;
    }

}
