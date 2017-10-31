package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignActivity extends AppCompatActivity {
    TextInputLayout nameWrapper, passwordWrapper;
    private EditText et_username, et_password, et_key;
    private Button bt_sign_on, bt_sign_up, bt_sign_key;
    private String username, password, key;
    private SweetAlertDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登录注册");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("进行中...");
//        pDialog.setCancelable(false);

        nameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        bt_sign_on = (Button) findViewById(R.id.bt_sign_on);

        bt_sign_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                inputCheck(username, password);
                if (!DesUtil.isEmail(username)) {
                    MyApplication.showToast("邮箱格式不正确哦~");
                    return;
                }
                if (password.isEmpty()) {
                    return;
                }
                BmobUser user = new BmobUser();
                String username1 = DesUtil.encrypt(username, password);
                user.setUsername(username1);
                user.setPassword(password);
                pDialog.show();
                user.signUp(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser objectId, BmobException e) {
                        if (e == null) {
                            MyApplication.showToast("注册成功，可直接使用此帐号信息登录");
                        } else {
                            if (e.getErrorCode() == 202) {
                                MyApplication.showToast("注册失败:该邮箱已注册...");
                            } else {
                                MyApplication.showToast("注册失败:" + e.getMessage());
                            }
                        }
                        pDialog.dismiss();
                    }

                });
            }
        });
        bt_sign_up = (Button) findViewById(R.id.bt_sign_up);
        bt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                inputCheck(username, password);
                if (!DesUtil.isEmail(username)) {
                    MyApplication.showToast("邮箱格式不正确哦~");
                    return;
                }
                if (password.isEmpty()) {
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
                            MyApplication.showToast("登录失败：" + e.getMessage());
                        }
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void inputCheck(String name, String password) {

        if (name.isEmpty()) {
            nameWrapper.setErrorEnabled(true);
            nameWrapper.setError("请正确填写邮箱信息哦");
            return;
        } else if (!DesUtil.isEmail(name)) {
            nameWrapper.setErrorEnabled(true);
            nameWrapper.setError("请正确填写邮箱信息哦");
            return;
        } else if (password.isEmpty()) {
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError("密码不能为空哦~");
            return;
        } else {
            nameWrapper.setError("");
            nameWrapper.setErrorEnabled(false);
            passwordWrapper.setError("");
            passwordWrapper.setErrorEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
