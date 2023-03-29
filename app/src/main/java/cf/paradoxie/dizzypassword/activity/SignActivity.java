package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignActivity extends BaseActivity {
    private EditText et_username, et_password;
    private Button bt_sign_on, bt_sign_up;
    private String username, password;
    private SweetAlertDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("登录");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(SignActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_change) {
                Intent intent = new Intent(AppManager.getAppManager().currentActivity(), TeachActivity.class);
                startActivity(intent);

            }
            return false;
        });
        ThemeUtils.initStatusBarColor(SignActivity.this, ThemeUtils.getPrimaryDarkColor(SignActivity.this));

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(SignActivity.this));
        pDialog.setTitleText("进行中...");

        et_username =  findViewById(R.id.et_username);
        et_password =  findViewById(R.id.et_password);
        bt_sign_on =  findViewById(R.id.bt_sign_on);

        bt_sign_on.setOnClickListener(view -> {
//            Intent intent = new Intent(SignActivity.this, JianGuoSignActivity.class);
//            startActivity(intent);
            MyApplication.showToast("仅限老用户登录使用，以完成私有坚果云数据备份~");
        });
        bt_sign_up =  findViewById(R.id.bt_sign_up);
        bt_sign_up.setOnClickListener(view -> {
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
            BmobUser bu2 = new BmobUser();
            String username1 = DesUtil.encrypt(username, password);
            bu2.setUsername(username1);
            bu2.setPassword(password);
            pDialog.show();
            bu2.login(new SaveListener<BmobUser>() {

                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e == null) {
                        MyApplication.showToast("登录成功");
                        SPUtils.put("password", password);
                        SPUtils.put("name", username);
                        Intent intent = new Intent(SignActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (e.getErrorCode() == 101) {
                            MyApplication.showToast("登录失败：用户名或密码不正确，或确认是否已注册");
                        } else {
                            MyApplication.showToast("登录失败：" + e.getMessage() + e.getErrorCode());
                        }

                    }
                    pDialog.dismiss();
                }
            });
        });
    }

}
