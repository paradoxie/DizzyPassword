package cf.paradoxie.dizzypassword.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class TeachActivity extends BaseActivity {
    private EditText et_key;
    private Button bt_go;
    private String key;
    private WebView wb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("修改教程");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }

    private void init() {
        final ProgressBar bar = (ProgressBar) findViewById(R.id.myProgressBar);
        wb = (WebView) findViewById(R.id.web);
        wb.getSettings().setJavaScriptEnabled(true);//支持js
        wb.setWebViewClient(new WebViewClient() {//屏蔽自动浏览器打开
            // Load opened URL in the application instead of standard browser
            // application
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
        wb.loadUrl("http://xiehehe.coding.me/2017/07/20/%E5%88%9D%E8%B0%88%E4%B8%89%E8%A7%82/");


        et_key = (EditText) findViewById(R.id.et_key);
        bt_go = (Button) findViewById(R.id.bt_go);
        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = et_key.getText().toString().trim();
                if (key.isEmpty()) {
                    MyApplication.showToast("您输入的key为空");
                    return;
                }
                if (SPUtils.get("key", "") == "") {
                    new SweetAlertDialog(TeachActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("确定修改为当前key吗？")
                            .setContentText("请确认已完成上面教程中的蛇皮操作\n请再次确认Application ID的准确")
                            .setCancelText("算了，用自带的")
                            .setConfirmText("我确定")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    SPUtils.put("key", key);
                                    sDialog.setTitleText("配置完成!")
                                            .setContentText("点击重启，app将自动关闭后重新启动\n请莫方😁")
                                            .setConfirmText("重启")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    //重新启动app
//                                                    AppManager.getAppManager().finishActivity(MainActivity.class);
//                                                    AppManager.getAppManager().finishAllActivity();
//                                                    Intent i = getBaseContext().getPackageManager()
//                                                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
//                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    startActivity(i);
                                                    Intent intent = getBaseContext().getPackageManager()
                                                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                                    PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                                    AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 1秒钟后重启应用
                                                    AppManager.getAppManager().finishAllActivity();
                                                    System.exit(0);

                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();
                }else {
                    MyApplication.showToast("您已经修改过Application ID啦~");
                }
            }
        });
    }

}
