package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.BackupBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BackupActivity extends BaseActivity {
    private TextView tv_backup, tv_back;
    private EditText editText;
    private DialogView mDialogView;
    private int successNum;
    private int failNum;

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
        tv_back = (TextView) findViewById(R.id.tv_back);
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

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!MyApplication.isSign()) {
                    MyApplication.showToast("没有登录喔");
                    return;
                }

                if (MyApplication.isNetworkAvailable(BackupActivity.this)) {
                    MyApplication.showToast("网络状态好像不太行喔");
                    return;
                }

                final String str = editText.getText().toString().trim();
                if (str.equals("")) {
                    MyApplication.showToast("文本框内没有内容喔");
                    return;
                }
                new SweetAlertDialog(BackupActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("导入数据")
                        .setContentText("在此之前请仔细查验数据格式")
                        .setConfirmText("导入")
                        .setCancelText("返回")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                editText.setText("\n");
                                addAccount(getList(str));
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });

    }


    private void addAccount(List<AccountBean> accountBeans) {

        for (AccountBean a : accountBeans) {
            AccountBean accountBean = new AccountBean();
            //构造加密信息，提交网络
            String note1 = DesUtil.encrypt(a.getNote(), SPUtils.getKey());
            String name1 = DesUtil.encrypt(a.getName(), SPUtils.getKey());
            String acount1 = DesUtil.encrypt(a.getAccount(), SPUtils.getKey());
            String web1 = DesUtil.encrypt(a.getWebsite(), SPUtils.getKey());
            String password1 = DesUtil.encrypt(a.getPassword(), SPUtils.getKey());
            List<String> tag1 = a.getTag();


            accountBean.setName(name1);
            accountBean.setNote(note1);
            accountBean.setAccount(acount1);
            accountBean.setWebsite(web1);
            accountBean.setPassword(password1);
            accountBean.setTag(tag1);
            accountBean.setUser(MyApplication.getUser());
            accountBean.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        successNum = successNum + 1;
                    } else {
                        failNum = failNum + 1;
                    }
                    editText.setText("成功" + successNum + "条\n" + "失败" + failNum + "条\n" + "返回列表时请手动刷新拉取最新云端数据");
                }
            });
        }
    }


    private List<AccountBean> getList(String strJson) {
        List<AccountBean> datalist = new ArrayList<>();
        try {
            JsonArray array = new JsonParser().parse(strJson).getAsJsonArray();
            for (final JsonElement elem : array) {
                datalist.add(new Gson().fromJson(elem, AccountBean.class));
            }
        } catch (Exception e) {
            MyApplication.showToast("数据格式有误");
        }

        return datalist;
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
