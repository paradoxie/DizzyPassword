package cf.paradoxie.dizzypassword.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cn.bmob.v3.BmobUser;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class EatRiceActivity extends BaseActivity {

    private WebView wb;
    private String url = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eatrice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("吃饭时间");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        url = intent.getStringExtra("url");


        init();
        ThemeUtils.initStatusBarColor(EatRiceActivity.this, ThemeUtils.getPrimaryDarkColor(EatRiceActivity.this));

    }

    private void init() {
        final ProgressBar bar = (ProgressBar) findViewById(R.id.myProgressBar);
        wb = (WebView) findViewById(R.id.web);
        if (!url.equals("")) {
            MyApplication.loadUri(wb, 0, url, bar);
        } else {
            MyToast.show(EatRiceActivity.this, "链接载入发生了一点问题", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

        }

    }

}
