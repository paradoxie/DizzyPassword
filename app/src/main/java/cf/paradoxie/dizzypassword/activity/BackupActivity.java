package cf.paradoxie.dizzypassword.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.BackupBean;
import cf.paradoxie.dizzypassword.utils.DataUtils;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("导出");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_csv) {
                checkActivity();
                mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        //校验密码
                        requestAllPower();
                        if (value.equals(SPUtils.get("password", "") + "")) {

                            if (DataUtils.exportCsv()) {
                                MyApplication.showToast("成功导出至根目录");
                            } else {
                                MyApplication.showToast("导出失败,请检查应用是否具体权限读写手机储存");
                            }
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
            return false;
        });

        ThemeUtils.initStatusBarColor(BackupActivity.this, ThemeUtils.getPrimaryDarkColor(BackupActivity.this));

        tv_backup = findViewById(R.id.tv_backup);
        tv_backup.setOnClickListener(v -> {
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
        });
        editText = findViewById(R.id.et_info);


    }

    //申请权限，需要使用之前申请
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
            }
        }
    }





    private String getAllData() {
        List<AccountBean> mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
        List<BackupBean> mBackupBean = new ArrayList<>();
        for (AccountBean accountBean : mAccountBeans) {
            BackupBean backuoBean = new BackupBean();
            backuoBean.setId(accountBean.getObjectId());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_backup, menu);
        return true;
    }
}
