package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
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

        rl_support_finger = (RelativeLayout) findViewById(R.id.rl_support_finger);
        rl_unsupport_finger = (RelativeLayout) findViewById(R.id.rl_unsupport_finger);
        tv_open = (TextView) findViewById(R.id.tv_open);
        tv_open_unsupport_finger = (TextView) findViewById(R.id.tv_open_unsupport_finger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("安全验证");
        setSupportActionBar(toolbar);
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String isFinger = SPUtils.get("isFinger", "true") + "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isFinger.equals("false")) {//sdk23以下的版本直接数字码验证，或者勾选了不启用指纹
            codeCheck();
            if (isFinger.equals("false")) {
                tv_message.setText("已启用6位数字码进行安全验证");
            } else {
                tv_message.setText("当前设备版本过低，请使用6位数字码进行安全验证");
            }
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
        fingerPrinterView = (FingerPrinterView) findViewById(R.id.fpv);

        tv_open_unsupport_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeCheck();
                rl_support_finger.setVisibility(View.GONE);
                rxfingerPrinter.onStop();
                rxfingerPrinter.stopListening();
            }
        });

        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override
            public void onChange(int state) {
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
                        startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
                        finish();
                    }

                }
            }
        });
        rxfingerPrinter = new RxFingerPrinter(this);

        startFinger();
        rxfingerPrinter.begin().subscribe(observer);

//        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
//            @Override
//            public void onChange(int state) {
//                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
//                    fingerErrorNum = 0;
//                    tv_open.setText("指纹验证成功，正在进入app...");
//                    startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
//                    finish();
//                }
//                if (state == FingerPrinterView.STATE_WRONG_PWD) {
//                    tv_open.setText("指纹认证失败，还剩" + (5 - fingerErrorNum) + "次机会");
//                    if (fingerErrorNum == 5) {
//                        tv_open.setText("指纹验证失败，请退出等待一段时间后重试");
//                        tv_open.setBackgroundResource(R.drawable.red_button_background);
//                    }
//                    fingerPrinterView.setState(STATE_NO_SCANING);
//                }
//            }
//        });

    }

    /*
    数字码验证
     */
    private void codeCheck() {
        final String str = SPUtils.get("pwd", "") + "";
        rl_unsupport_finger.setVisibility(View.VISIBLE);
        tv_message = (TextView) findViewById(R.id.tv_message);
        view = (PswInputView) findViewById(R.id.psw_input);
        view.setInputCallBack(new PswInputView.InputCallBack() {
            @Override
            public void onInputFinish(String result) {
                checkCode(str, result);
                hideInputWindow();
            }
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
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            SPUtils.put("pwd", pwd);
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
                            finish();
                        }
                    })
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
        } else {//设置了安全码，开始比对
            if (pwd.equals(str)) {
                tv_message.setText("验证成功,正在进入app...");
                startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
                finish();
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
//                    Toast.makeText(SafeActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                } else {
//                    FPerException exception = info.getException();
//                    if (exception != null){
//                        Toast.makeText(SafeActivity.this,exception.getDisplayMessage(),Toast.LENGTH_SHORT).show();
//                    }
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        };
//        rxfingerPrinter.dispose();
//        Subscription subscription = rxfingerPrinter.begin().subscribe(new Subscriber<Boolean>() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
//                    return;
//                } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
//                        || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
//                    fingerPrinterView.setState(STATE_NO_SCANING);
//                } else {
//                    fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
//                }
//            }
//
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @SuppressLint("WrongConstant")
//            @Override
//            public void onError(Throwable e) {
//                if (e instanceof FPerException) {
//                    code = ((FPerException) e).getCode();
//                    if (code == 6) {
//                        tv_open.setText("指纹验证失败，请退出等待一段时间后重试");
//                        tv_open.setBackgroundResource(R.drawable.red_button_background);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onNext(Boolean aBoolean) {
//                if (aBoolean) {
//                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
//                } else {
//                    fingerErrorNum++;
//                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
//                }
//            }
//        });
//        rxfingerPrinter.addSubscription(this, subscription);
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
