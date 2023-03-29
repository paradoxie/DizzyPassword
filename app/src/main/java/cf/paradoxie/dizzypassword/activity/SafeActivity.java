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
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.FingerPrinterView;
import cf.paradoxie.dizzypassword.view.PswInputView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.IdentificationInfo;
import zwh.com.lib.RxFingerPrinter;

import static zwh.com.lib.CodeException.FINGERPRINTERS_FAILED_ERROR;
import static zwh.com.lib.CodeException.HARDWARE_MISSIING_ERROR;
import static zwh.com.lib.CodeException.KEYGUARDSECURE_MISSIING_ERROR;
import static zwh.com.lib.CodeException.NO_FINGERPRINTERS_ENROOLED_ERROR;
import static zwh.com.lib.CodeException.PERMISSION_DENIED_ERROE;

/**
 * Created by xiehehe on 2017/10/30.
 */

public class SafeActivity extends BaseActivity {
    private PswInputView view;
    private FingerPrinterView fingerPrinterView;
    private int fingerErrorNum = 0; // 指纹错误次数
    private RxFingerPrinter rxfingerPrinter;
    private DisposableObserver<IdentificationInfo> observer;
    private RelativeLayout rl_support_finger, rl_unsupport_finger;
    private TextView tv_message;
    private TextView tv_open;
    private TextView tv_open_unsupport_finger;
    private int code = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);

        rl_support_finger = findViewById(R.id.rl_support_finger);
        rl_unsupport_finger = findViewById(R.id.rl_unsupport_finger);
        tv_open = findViewById(R.id.tv_open);
        tv_open_unsupport_finger = findViewById(R.id.tv_open_unsupport_finger);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("安全验证");
        setSupportActionBar(toolbar);
        String isFinger = SPUtils.get("isFinger", "true") + "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isFinger.equals("false")) {//sdk23以下的版本直接数字码验证，或者勾选了不启用指纹
            codeCheck();
            if (isFinger.equals("false")) {
                tv_message.setText("已启用6位数字码进行安全验证");
            } else {
                tv_message.setText("当前设备版本过低，请使用6位数字码进行安全验证");
            }
            rl_support_finger.setVisibility(View.GONE);
        } else {//SDK23以上显示指纹
            fingerCheck();
            if (code != 999) {
                codeCheck();
                if (code == PERMISSION_DENIED_ERROE) {
                    tv_message.setText("当前没有指纹权限，将使用6位数字码进行安全验证或在设置中授权后重试");
                } else if (code == HARDWARE_MISSIING_ERROR) {
                    tv_message.setText("当前没有指纹模块，将使用6位数字码进行安全验证");
                } else if (code == KEYGUARDSECURE_MISSIING_ERROR) {
                    tv_message.setText("当前没有设置锁屏密码，将使用6位数字码进行安全验证");
                } else if (code == NO_FINGERPRINTERS_ENROOLED_ERROR) {
                    tv_message.setText("当前没有指纹录入，将使用6位数字码进行安全验证或在设置中录入指纹后重试");
                } else if (code == FINGERPRINTERS_FAILED_ERROR) {
                    tv_message.setText("指纹认证失败，将使用6位数字码进行安全验证");
                }
            } else {
                rl_unsupport_finger.setVisibility(View.GONE);
                rl_support_finger.setVisibility(View.VISIBLE);
            }
        }

        ThemeUtils.initStatusBarColor(SafeActivity.this, ThemeUtils.getPrimaryDarkColor(SafeActivity.this));

    }

    /*
    指纹验证
     */
    private void fingerCheck() {
        fingerPrinterView = findViewById(R.id.fpv);

        tv_open_unsupport_finger.setOnClickListener(view -> {
            codeCheck();
            rl_support_finger.setVisibility(View.GONE);
            rxfingerPrinter.onStop();
            rxfingerPrinter.stopListening();
        });

        fingerPrinterView.setOnStateChangedListener(state -> {
            if (state == FingerPrinterView.STATE_WRONG_PWD) {
                fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
            }
            if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                fingerErrorNum = 0;
                String str = SPUtils.get("pwd", "") + "";
                if (str.equals("")) {
                    MyToast.show(SafeActivity.this, "首次使用app，请设置安全码，此码仅保存本地", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));
                    codeCheck();
                    rl_support_finger.setVisibility(View.GONE);
                } else {
                    tv_open.setText("指纹验证成功，正在进入app...");
                    safeToIn();
                }

            }
        });
        rxfingerPrinter = new RxFingerPrinter(this);

        startFinger();
        rxfingerPrinter.begin().subscribe(observer);
    }

    private void safeToIn() {
        String jianguo = SPUtils.get("jianguo_pwd", "") + "";
        if (TextUtils.isEmpty(jianguo)) {
            startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(MyApplication.getContext(), JianGuoMainActivity.class));
            finish();
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


    private void startFinger() {
        observer = new DisposableObserver<IdentificationInfo>() {
            @Override
            protected void onStart() {
                if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
                    return;
                } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
                        || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(IdentificationInfo info) {
                if (info.isSuccessful()) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxfingerPrinter != null) {
            rxfingerPrinter.dispose();
        }
        fingerErrorNum = 0;
    }

}
