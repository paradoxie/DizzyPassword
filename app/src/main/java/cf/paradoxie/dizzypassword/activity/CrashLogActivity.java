package cf.paradoxie.dizzypassword.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;

public class CrashLogActivity extends BaseActivity {
    private static final String EXTRA_E = "e";

    public static void start(@NonNull Context context, @NonNull Throwable e) {
        Intent intent = new Intent(context, CrashLogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_E, e);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.CHINA);

    TextView tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("崩溃日志");
        setSupportActionBar(toolbar);
        tvInfo = (TextView) findViewById(R.id.tv_info);

        Throwable e = (Throwable) getIntent().getSerializableExtra(EXTRA_E);

        StringBuilder sb = new StringBuilder();
        sb.append("生产厂商：");
        sb.append(Build.MANUFACTURER).append("\n");
        sb.append("手机型号：");
        sb.append(Build.MODEL).append("\n");
        sb.append("系统版本：");
        sb.append(Build.VERSION.RELEASE).append("\n");
        sb.append("网络环境：");
        sb.append(MyApplication.GetNetworkType()).append("\n");
        sb.append("异常时间：");
        sb.append(dateFormat.format(new Date())).append("\n");
        sb.append("异常类型：");
        sb.append(e.getClass().getName()).append("\n\n");
        sb.append("异常信息：\n");
        sb.append(e.getMessage()).append("\n\n");
        sb.append("异常堆栈：\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        sb.append(writer.toString());

        String str = sb.toString();
        String s1, s2, s3 = "Caused by";
        if (str.contains(s3)) {//提取重点
            String[] ary = str.split("Caused by");
            s1 = ary[0];
            s2 = ary[1];
            str = s1 + "<br/><font color=red>" + s3 + "</font>" + s2;
            tvInfo.setText(Html.fromHtml(str.replace("\n", "<br/>")));
        } else {
            tvInfo.setText(str);
        }
        ThemeUtils.initStatusBarColor(CrashLogActivity.this, ThemeUtils.getPrimaryDarkColor(CrashLogActivity.this));

    }

    @Override
    public void onBackPressed() {
        //强制结束app
        AppManager.getAppManager().finishAllActivity();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        System.exit(0);
    }
}
