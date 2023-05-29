package cf.paradoxie.dizzypassword.base;

import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_ACCOUNT;
import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_PWD;
import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_SERVER;
import static cn.bmob.v3.BmobRealTimeData.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.WhiteToastStyle;

import cf.paradoxie.dizzypassword.activity.CrashLogActivity;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.wdsyncer.SyncConfig;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {
    public static MyApplication mInstance;
    public static Context mContext;
    public static int first_check = 0;
    public static String killTime;
    private int mFinalCount;
    Handler handler;


    @Override
    public void onCreate() {
        Log.d(TAG, "----------------------[MyApplication] onCreate-------------------");
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
        initToast();
        //开启抓取错误信息
//        Thread.setDefaultUncaughtExceptionHandler(this);
        configDavSync();

//        Bmob.resetDomain("http://xiepwd.ofcoder.com/8/");
        Bmob.resetDomain("http://bmob.paradoxie.top/8/");
        String key = SPUtils.get("key", "") + "";
        if (("").equals(key)) {
            Bmob.initialize(this, Constants.APPLICATION_ID);
        } else {
            Bmob.initialize(this, key);
        }

        handler = new Handler();

        checkStatus();//监听前后台状态
    }

    private void initToast() {
        ToastUtils.init(this);
        ToastUtils.setStyle(new WhiteToastStyle());
//        ToastUtils.setView(R.layout.toast_view);
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 50);
    }

    /**
     * 配置坚果云sync
     */
    public void configDavSync() {
        String webdav_server = SPUtils.get(WEBDAV_SERVER, "") + "";
        String jianguo_account = SPUtils.get(WEBDAV_ACCOUNT, "") + "";
        String pwd = SPUtils.get(WEBDAV_PWD, "") + "";

        if (!"".equals(jianguo_account)) {
            SyncConfig config = new SyncConfig(this);
            config.setServerUrl(webdav_server);
            config.setPassWord(pwd);
            config.setUserAccount(jianguo_account);
        }


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


    //检验是否存在本地用户，返回true/false
    public static boolean isSign() {
        String password = SPUtils.get("password", "") + "";
        if (getUser() != null || !"".equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean allowChange() {
        String password = SPUtils.get("password", "") + "";
        if ("".equals(password)) {
            showToast("本地账号没有登录噢");
            return false;
        }
        if (getUser() != null) {
            showToast("当前提供个人webdav操作，请备份webdav后退出，登录界面点击【直接使用webdav】");
            return false;
        }
        return true;
    }

    public static BmobUser getUser() {
        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        return bmobUser;
    }

    //判断网络是否可用
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
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


    /**
     * 加入QQ群
     *
     * @return
     */
    public static boolean joinQQGroup() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + Constants.QQ_ID));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 跳转酷安
     *
     * @param appPkg
     * @param marketPkg
     */
    public static void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            first_check = 2;
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * 校验前后台状态
     */
    private void checkStatus() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e("onActivityStarted", mFinalCount + "");
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台
                Log.i("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                    killTime = (String) SPUtils.get("killTime", 60 + "");
                    if (first_check != 0) {
                        MyApplication.showToast("程序进入后台啦," + killTime + "秒后将关闭操作权限");
                        Integer i = Integer.parseInt(killTime);
                        handler.postDelayed(runnable, 1000 * i);//默认30s
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 显示Toast
     *
     * @param error_pwd
     */
    public static void showToast(int error_pwd) {
        if (TextUtils.isEmpty(error_pwd + "")) {
            return;
        }

        if (ToastUtils.isInit()) {
            ToastUtils.show(error_pwd);
        }
    }

    /**
     * 显示Toast
     *
     * @param message
     */
    public static void showToast(CharSequence message) {

        if (TextUtils.isEmpty(message)) {
            return;
        }

        if (ToastUtils.isInit()) {
            ToastUtils.show(message);
        }
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


}
