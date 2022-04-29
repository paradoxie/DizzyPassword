package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
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
        toolbar.setTitle("登录注册");
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
            new SweetAlertDialog(SignActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("注册信息确认")
                    .setContentText("请牢记您的注册信息\n帐号:" + username + "\n密码:" + password)
                    .setCancelText("哦填错了")
                    .setConfirmText("确定")
                    .setConfirmClickListener(sDialog -> {

                        //注册
                        BmobUser user = new BmobUser();
                        final String username1 = DesUtil.encrypt(username, password);
                        user.setUsername(username1);
                        user.setPassword(password);
                        pDialog.show();
                        user.signUp(new SaveListener<BmobUser>() {
                            @Override
                            public void done(BmobUser objectId, BmobException e) {
                                if (e == null) {
                                    SPUtils.put("password", password);
                                    SPUtils.put("name", username);
                                    MyApplication.showToast("注册成功，已直接登录");
                                    Intent intent = new Intent(SignActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (e.getErrorCode() == 202) {
                                        MyApplication.showToast("注册失败:该邮箱已注册...");
                                    } else {
                                        MyApplication.showToast("注册失败:" + e.getMessage() + e.getErrorCode());
                                    }
                                }
                                pDialog.dismiss();
                                sDialog.cancel();
                            }
                        });
                    })
                    .showCancelButton(true)
                    .setCancelClickListener(sDialog -> sDialog.cancel())
                    .show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
