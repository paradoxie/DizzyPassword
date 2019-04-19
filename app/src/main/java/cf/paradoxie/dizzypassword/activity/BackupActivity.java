package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.BackupBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;

public class BackupActivity extends BaseActivity {
    private TextView tv_backup;
    private EditText editText;
    private DialogView mDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("导出");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        ThemeUtils.initStatusBarColor(BackupActivity.this, ThemeUtils.getPrimaryDarkColor(BackupActivity.this));

        tv_backup = (TextView) findViewById(R.id.tv_backup);
        tv_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkActivity();
                mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        //校验密码
                        if (value.equals(SPUtils.get("password", "") + "")) {
                            //生成明文
                            String str = getAllData();
                            editText.setText(str);
                            hideInputWindow();
                            mDialogView.dismiss();
                        } else {
                            MyApplication.showToast(R.string.error_pwd);
                        }
                    }

                    @Override
                    public void negCliclListener(String value) {
                        //取消查看
                    }
                });
            }
        });
        editText = (EditText) findViewById(R.id.et_info);

    }

    private String getAllData() {
        List<AccountBean> mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
        List<BackupBean> mBackupBean = new ArrayList<>();
        for (AccountBean accountBean : mAccountBeans) {
            BackupBean backuoBean = new BackupBean();
            backuoBean.setName(DesUtil.decrypt(accountBean.getName(), SPUtils.getKey()));//名字
            backuoBean.setAccount(DesUtil.decrypt(accountBean.getAccount(), SPUtils.getKey()));//账号
            backuoBean.setPassword(DesUtil.decrypt(accountBean.getPassword(), SPUtils.getKey()));//密码
            backuoBean.setWebsite(DesUtil.decrypt(accountBean.getWebsite(), SPUtils.getKey()));//网址
            backuoBean.setNote(DesUtil.decrypt(accountBean.getNote(), SPUtils.getKey()));//备注
            backuoBean.setTag(accountBean.getTag());//标记

            mBackupBean.add(backuoBean);
        }

        Gson gson = new Gson();
        String str = gson.toJson(mBackupBean);

        return str;
    }

    private void checkActivity() {
        mDialogView = new DialogView(BackupActivity.this);
        mDialogView.setAccount(SPUtils.get("name", "") + "");
        try {
            if (!BackupActivity.this.isFinishing()) {
                mDialogView.show();
                hideInputWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
