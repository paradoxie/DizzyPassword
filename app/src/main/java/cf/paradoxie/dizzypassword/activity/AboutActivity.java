package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiehehe on 2017/10/31.
 */

public class AboutActivity extends BaseActivity {
    private TextView version_info, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("免责及隐私协议");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        if (SPUtils.get("first_in", "") + "" == "") {
            showNote();
        }
        note =   findViewById(R.id.note);
        note.setOnClickListener(view -> showNote());
        version_info =   findViewById(R.id.version_info);

        version_info.setText(Utils.GetVersionName());
        ThemeUtils.initStatusBarColor(AboutActivity.this, ThemeUtils.getPrimaryDarkColor(AboutActivity.this));
    }

    private void showNote() {
        new SweetAlertDialog(AboutActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("免责声明")
                .setContentText(
                                "1.本app为非盈利开源项目，任何拷贝复制的相同项目与本app无关" +
                                "\n2.后台数据储存由Bmob云服务提供，作者承诺不会对后台用户数据进行任何操作" +
                                "\n3.任何加密技术都有被破解的可能性，由此造成的损失与本app及作者无关" +
                                "\n4.作为作者，建议用户自己申请Bmob云服务进行私人信息储存，相关教程请点击登录界面右上角")
                .setConfirmText("好的我知道啦")
                .setConfirmClickListener(sDialog -> {
                    SPUtils.put("first_in", "第一次进入app");
                    sDialog.cancel();
                })
                .show();

    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
