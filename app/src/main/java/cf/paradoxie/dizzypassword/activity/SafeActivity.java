package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.PswInputView;
import cn.bmob.v3.BmobUser;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiehehe on 2017/10/30.
 */

public class SafeActivity extends BaseActivity {
    private PswInputView view;


//    private RelativeLayout rl_support_finger,
    private RelativeLayout    rl_unsupport_finger;
    private TextView tv_message;
    private TextView tv_open;
    private TextView tv_open_unsupport_finger;
    private int code = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);

//        rl_support_finger = findViewById(R.id.rl_support_finger);
        rl_unsupport_finger = findViewById(R.id.rl_unsupport_finger);
        tv_open = findViewById(R.id.tv_open);
        tv_open_unsupport_finger = findViewById(R.id.tv_open_unsupport_finger);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("安全验证");
        setSupportActionBar(toolbar);
        ThemeUtils.initStatusBarColor(SafeActivity.this, ThemeUtils.getPrimaryDarkColor(SafeActivity.this));
        String isFinger = SPUtils.get("isFinger", "true") + "";

        codeCheck();


    }

    private void safeToIn() {
        String jianguo = SPUtils.get("jianguo_pwd", "") + "";
        BmobUser user = MyApplication.getUser();

        if (!TextUtils.isEmpty(jianguo) || user != null) {
            mHandler.postDelayed(() -> {
                if (TextUtils.isEmpty(jianguo)) {
                    startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(MyApplication.getContext(), JianGuoMainActivity.class));
                }
                AppManager.getAppManager().finishAllActivity();
                finish();
            }, 1000);

        } else {
            mHandler.postDelayed(() -> {
                gotoActivity(ChoiceActivity.class);
                finish();
            }, 1000);

        }
    }

    /*
    数字码验证
     */
    private void codeCheck() {
        final String str = SPUtils.get("pwd", "") + "";
        rl_unsupport_finger.setVisibility(View.VISIBLE);
        tv_message = findViewById(R.id.tv_message);
        view = findViewById(R.id.psw_input);
        view.setInputCallBack(result -> {
            checkCode(str, result);
            hideInputWindow();
        });

    }

    /**
     * 校验安全码
     *
     * @param str 手机中保存的安全码
     * @param pwd 本次输入的安全码
     */
    private void checkCode(String str, final String pwd) {
        if (str.equals("")) {//还没有设置安全码
            new SweetAlertDialog(SafeActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("安全码设置")
                    .setContentText("您正在进行首次安全码设置，以后进入APP都将使用此安全码进行验证，您的安全码为\n" + pwd)
                    .setCancelText("我再想想")
                    .setConfirmText("确定")
                    .setConfirmClickListener(sDialog -> {
                        SPUtils.put("pwd", pwd);
                        sDialog.dismissWithAnimation();
                        safeToIn();
                    })
                    .showCancelButton(true)
                    .setCancelClickListener(sDialog -> sDialog.cancel())
                    .show();
        } else {//设置了安全码，开始比对
            if (pwd.equals(str)) {
                tv_message.setText("验证成功,正在进入app...");
                safeToIn();
            } else {
                view.clearResult();
                tv_message.setText("安全码验证错误，请重新尝试");
                tv_message.setTextColor(getResources().getColor(R.color.red_btn_bg_pressed_color));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
