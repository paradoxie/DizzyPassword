package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.SPUtils;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_change) {
                    // 跳转到一个web页面，获取并修改Bmob的key的教程
                    Intent intent = new Intent(AboutActivity.this, TeachActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
        if (SPUtils.get("first_in","")+""==""){
            showNote();
        }
        note = (TextView) findViewById(R.id.note);
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNote();
            }
        });
        version_info = (TextView) findViewById(R.id.version_info);

        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pi.versionName;
            version_info.setText("V" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showNote() {
        new SweetAlertDialog(AboutActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("免责声明")
                .setContentText(
                        "1.本app为非盈利开源项目，任何拷贝复制的相同项目与本app无关" +
                                "\n2.后台数据储存由Bmob云服务提供，作者承诺不会对后台用户数据进行任何操作" +
                                "\n3.任何加密技术都有被破解的可能性，由此造成的损失与本app及作者无关" +
                                "\n4.作为作者，建议用户自己申请Bmob云服务进行私人信息储存，相关教程请点击右上角查看")
                .setConfirmText("好的我知道啦")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SPUtils.put("first_in","第一次进入app");
                        sDialog.cancel();
                    }
                })
                .show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }
}
