package cf.paradoxie.dizzypassword.activity;

import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_ACCOUNT;
import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_PWD;
import static cf.paradoxie.dizzypassword.base.Constants.WEBDAV_SERVER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.bean.CommonEntity;
import cf.paradoxie.dizzypassword.databinding.ActivitySignJianguoBinding;
import cf.paradoxie.dizzypassword.utils.JumpUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class JianGuoSignActivity extends BaseActivity {
    private EditText et_username, et_password;
    private Button bt_sign_up;
    private CheckBox cb;
    private String server, username, jianguo_pwd, password;
    private SweetAlertDialog pDialog = null;
    private ActivitySignJianguoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_jianguo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("数据已在webdav");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        ThemeUtils.initStatusBarColor(JianGuoSignActivity.this, ThemeUtils.getPrimaryDarkColor(JianGuoSignActivity.this));

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(JianGuoSignActivity.this));
        pDialog.setTitleText("进行中...");

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        cb = findViewById(R.id.cb);

        bt_sign_up = findViewById(R.id.bt_sign_up);
        bt_sign_up.setOnClickListener(view -> {
            if (!cb.isChecked()) {
                MyApplication.showToast("请点击阅读隐私协议，勾选后登录~");
                return;
            }
            server = binding.etAddress.getText().toString().trim();
            if (server.isEmpty()) {
                MyApplication.showToast("请填写webdav地址~");
                return;
            }
            username = et_username.getText().toString().trim();
            if (username.isEmpty()) {
                MyApplication.showToast("请填写webdav账户");
                return;
            }

            jianguo_pwd = binding.etJianguoPwd.getText().toString().trim();
            if (jianguo_pwd.isEmpty()) {
                MyApplication.showToast("请填写webdav应用密码");
                return;
            }

            password = et_password.getText().toString().trim();

            if (password.isEmpty() || password.length() < 8) {
                MyApplication.showToast("密码位数不够哟~");
                return;
            }

            String key = binding.etPasswordConfirm.getText().toString();
            if (!password.equals(key)) {
                binding.tvEncryptPasswordNotify.setVisibility(View.VISIBLE);
                return;
            }

            MyApplication.showToast("保存成功");
            SPUtils.put(WEBDAV_SERVER, server);
            SPUtils.put(WEBDAV_ACCOUNT, username);
            SPUtils.put(WEBDAV_PWD, jianguo_pwd);

            SPUtils.put("password", password);
            SPUtils.put("name", username);
            hideInputWindow();
            Intent intent = new Intent(JianGuoSignActivity.this, JianGuoMainActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishAllActivity();
            finish();

        });

        findViewById(R.id.tv_private).setOnClickListener(v -> {
            Intent intent1 = new Intent(AppManager.getAppManager().currentActivity(), AboutActivity.class);
            startActivity(intent1);
        });
        findViewById(R.id.tv_notify_2).setOnClickListener(v -> {
            CommonEntity commonEntity = new CommonEntity();
            commonEntity.setType(0);
            commonEntity.setJumpUrl("https://rbtzzwgihc.feishu.cn/docx/CfPHdxZFAo4nsxxevwBctxUmnUc");
            JumpUtil.jump(JianGuoSignActivity.this, commonEntity);
        });
        findViewById(R.id.tv_notify_3).setOnClickListener(v -> {
            CommonEntity commonEntity = new CommonEntity();
            commonEntity.setType(0);
            commonEntity.setJumpUrl("https://rbtzzwgihc.feishu.cn/docx/RUjod6wjCoOZH3xSd6gcOGvNnSh");
            JumpUtil.jump(JianGuoSignActivity.this, commonEntity);
        });
    }

}
