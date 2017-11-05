package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignActivity extends BaseActivity {
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
                if (password.isEmpty() || password.length() < 8) {
                    MyApplication.showToast("密码位数不够哟~");
                    return;
                }
                new SweetAlertDialog(SignActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("注册信息确认")
                        .setContentText("请牢记您的注册信息\n帐号:" + username + "\n密码:" + password)
                        .setCancelText("哦填错了")
                        .setConfirmText("确定")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {

                                //注册
                                BmobUser user = new BmobUser();
                                final String username1 = DesUtil.encrypt(username, password);
                                user.setUsername(username1);
                                user.setPassword(password);
                                pDialog.show();
                                final AccountBean accountBean = new AccountBean();
                                user.signUp(new SaveListener<BmobUser>() {
                                    @Override
                                    public void done(BmobUser objectId, BmobException e) {
                                        if (e == null) {
                                            SPUtils.put("password", password);
                                            SPUtils.put("name", username);
                                            accountBean.setName(DesUtil.encrypt("去特么的密码",password));
                                            accountBean.setAccount(username1);
                                            accountBean.setPassword(DesUtil.encrypt(password,password));
                                            accountBean.setTag(new ArrayList<String>(){{add("个人"); add("当前");add("APP");add("备忘");add("记录");}});
                                            accountBean.setNote(DesUtil.encrypt("1.该条信息注册登录后默认创建\n" +
                                                    "2.帐号不支持找回，请牢记此条帐号信息\n" +
                                                    "3.点击tag信息可对具有相同tag的条目进行搜索\n" +
                                                    "4.条目默认排序为创建时间排序，点击时间可根据最近修改排序\n" +
                                                    "5.此条可删除",password));
                                            accountBean.setUser(MyApplication.getUser());
                                            accountBean.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    MyApplication.showToast("注册成功，已直接登录");
                                                }
                                            });
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
                            }
                        })
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

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
        } else if (password.isEmpty() || password.length() < 8) {
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError("密码不能少于8位哟~");
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
