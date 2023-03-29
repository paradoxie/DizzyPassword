package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AlertDialog;


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

public class WelcomeActivity extends BaseActivity {
    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding(R.layout.activity_welcome);
        binding.tvVersion.setText(Utils.GetVersionName());
        getAppConfigUrl();
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


    public void initViewWithData() {
        binding.tvProgress.setText("正在进入应用，请稍候...");
        String pwd = SPUtils.get("jianguo_pwd", "") + "";
        BmobUser user = MyApplication.getUser();

        if (!TextUtils.isEmpty(pwd) || user != null) {
            mHandler.postDelayed(() -> {
                gotoActivity(SafeActivity.class);
                finish();
            }, 2000);

        } else {
            mHandler.postDelayed(() -> {
                gotoActivity(ChoiceActivity.class);
                finish();
            }, 2000);

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}