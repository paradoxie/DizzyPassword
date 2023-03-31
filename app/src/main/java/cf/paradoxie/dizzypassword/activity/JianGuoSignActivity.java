package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class JianGuoSignActivity extends BaseActivity {
    private EditText et_username, et_password;
    private Button bt_sign_up;
    private CheckBox cb;
    private String username, password;
    private SweetAlertDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_jianguo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("数据已在坚果云");
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
                MyApplication.showToast("请仔细阅读注意事项，勾选后登录~");
                return;
            }
            username = et_username.getText().toString().trim();
            password = et_password.getText().toString().trim();
            if (!DesUtil.isEmail(username)) {
                MyApplication.showToast("邮箱格式不正确哦~");
                return;
            }
            if (password.isEmpty() || password.length() < 8) {
                MyApplication.showToast("密码位数不够哟~");
                return;
            }

            MyApplication.showToast("登录成功");
            SPUtils.put("password", password);
            SPUtils.put("name", username);
            hideInputWindow();
            Intent intent = new Intent(JianGuoSignActivity.this, JianGuoMainActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishAllActivity();
            finish();

        });
    }

}
