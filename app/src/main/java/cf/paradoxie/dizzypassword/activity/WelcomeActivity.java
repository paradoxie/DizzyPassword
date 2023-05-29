package cf.paradoxie.dizzypassword.activity;

import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_PWD;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AppConfig;
import cf.paradoxie.dizzypassword.data.DataDeal;
import cf.paradoxie.dizzypassword.databinding.ActivityWelcomeBinding;
import cf.paradoxie.dizzypassword.http.HttpListener;
import cf.paradoxie.dizzypassword.http.HttpUtils;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;
import cn.bmob.v3.BmobUser;
import co.infinum.goldfinger.Goldfinger;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private Goldfinger goldfinger;
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  //设置为全屏
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//状态栏字体颜色设置为黑色这个是Android 6.0才出现的属性   默认是白色
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//设置为透明色
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        goldfinger = new Goldfinger.Builder(this).build();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        binding.tvVersion.setText(Utils.GetVersionName());
        getAppConfigUrl();

        binding.tvProgress.setOnClickListener(v -> {
            initViewWithData();
        });
        binding.code.setOnClickListener(v -> {
            gotoActivity(SafeActivity.class);
        });
    }

    private void getAppConfigUrl() {
        binding.tvProgress.setText("正在获取版本信息...");

        HttpUtils.getInstance().getAppConfigUrl(new HttpListener<AppConfig>() {
            @Override
            public void success(AppConfig appConfig) {
                Log.e("TAG", appConfig.toString());
                int locatedVersionCode = Utils.GetVersion();
                DataDeal.getInstance(WelcomeActivity.this).setAppConfig(appConfig);
                int tencentVersionCode = appConfig.getVersion_code();
                if (tencentVersionCode > locatedVersionCode) {
                    runOnUiThread(() -> {
                        if (appConfig.getAppDownLoadUrl().equals("-")) {
                            openKuan();
                        } else {
                            openLanzou(appConfig);
                        }
                    });
                } else {
                    initViewWithData();
                }

            }

            @Override
            public void failed() {
                initViewWithData();
            }
        });
    }

    private void openLanzou(AppConfig appConfig) {
        new AlertDialog.Builder(WelcomeActivity.this)
                .setTitle("有更新")
                .setMessage("前往下载更新包")
                .setCancelable(false)
                .setPositiveButton("前往", (dialogInterface, i) -> {
                    Utils.jumpToDefaultBrowser(WelcomeActivity.this, appConfig.getAppDownLoadUrl());
                })
                .setNeutralButton("我就不.GIF", (d, i) -> {
                    initViewWithData();
                }).show();
    }



    private void openKuan() {
        new AlertDialog.Builder(WelcomeActivity.this)
                .setTitle("有重大更新")
                .setMessage("赶快前往酷安更新吧")
                .setCancelable(false)
                .setPositiveButton("前往", (dialogInterface, i) -> {
                    //前往酷安
                    MyApplication.launchAppDetail(MyApplication.getContext().getPackageName(), "com.coolapk.market");
                })
                .setNeutralButton("我就不.GIF", (d, i) -> {
                    initViewWithData();
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvProgress.setText("点击唤起指纹验证");
    }

    public void initViewWithData() {


        String isFinger = SPUtils.get("isFinger", "true") + "";
        if (!isFinger.equals("false")) {
            binding.tvProgress.setText("正在唤起指纹验证");
            Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(this)
                    .title("请进行指纹验证")
                    .negativeButtonText("使用安全码")
                    .description("如指纹认证失败，可使用安全码")
                    .subtitle("")
                    .build();
            goldfinger.authenticate(params, new Goldfinger.Callback() {
                @Override
                public void onError(@NonNull Exception e) {
                    /* Critical error happened */
                    binding.tvProgress.setText("认证失败,请使用安全码");
                }

                @Override
                public void onResult(@NonNull Goldfinger.Result result) {
                    /* Result received */
                    if (result.reason() == Goldfinger.Reason.NEGATIVE_BUTTON) {
                        gotoActivity(SafeActivity.class);
                    } else if (result.reason() == Goldfinger.Reason.USER_CANCELED) {
                        binding.tvProgress.setText("取消指纹认证，点击唤起");

                    } else if (result.reason() == Goldfinger.Reason.CANCELED) {
                        binding.tvProgress.setText("取消指纹认证，点击唤起");

                    } else if (result.reason() == Goldfinger.Reason.AUTHENTICATION_START) {
                        binding.tvProgress.setText("开始验证指纹...");

                    } else if (result.reason() == Goldfinger.Reason.HARDWARE_UNAVAILABLE) {
                        binding.tvProgress.setText("指纹不支持，正在进入安全码验证...");
                        gotoActivity(SafeActivity.class);
                    } else if (result.reason() == Goldfinger.Reason.AUTHENTICATION_SUCCESS) {
                        binding.tvProgress.setText("指纹成功,正在进入...");
                        fingerSuccess();
                    } else {
                        binding.tvProgress.setText("认证失败,请使用安全码");
                        goldfinger.cancel();
                    }
                }
            });
        } else {
            binding.tvProgress.setText("正在进入安全码...");
            gotoActivity(SafeActivity.class);
        }
//        fingerSuccess();
    }

    private void fingerSuccess() {
        String jianguo = SPUtils.get(WEBDAV_PWD, "") + "";
        BmobUser user = MyApplication.getUser();

        if (!TextUtils.isEmpty(jianguo) || user != null) {
            mHandler.postDelayed(() -> {
                if (TextUtils.isEmpty(jianguo)) {
                    startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(MyApplication.getContext(), JianGuoMainActivity.class));
                }
                finish();
            }, 500);

        } else {
            mHandler.postDelayed(() -> {
                gotoActivity(ChoiceActivity.class);
                finish();
            }, 500);

        }
    }

    public void gotoActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}