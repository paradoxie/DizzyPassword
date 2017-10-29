package cf.paradoxie.dizzypassword.activity;

import android.app.Activity;
import android.os.Bundle;
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

public class SignActivity extends Activity {
    private EditText et_username, et_password, et_key;
    private Button bt_sign_on, bt_sign_up, bt_sign_key;
    private String username, password, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);


        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
//        et_key = (EditText) findViewById(R.id.et_key);

        bt_sign_on = (Button) findViewById(R.id.bt_sign_on);
        bt_sign_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                BmobUser user = new BmobUser();

                String username1 = DesUtil.encrypt(username,password);
                user.setUsername(username1);
                user.setPassword(password);
                user.signUp(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser objectId, BmobException e) {
                        if (e == null) {
                            MyApplication.showToast("注册成功，可直接使用此帐号信息登录");
                        } else {
                            MyApplication.showToast("注册失败：" + e.getMessage());
                        }
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
                BmobUser bu2 = new BmobUser();
                String username1 = DesUtil.encrypt(username,password);
                bu2.setUsername(username1);
                bu2.setPassword(password);
                bu2.login(new SaveListener<BmobUser>() {

                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null) {
                            MyApplication.showToast("登录成功");
                            SPUtils.put("password",password);
                            finish();
                        } else {
                            MyApplication.showToast("登录失败：" + e);
                        }
                    }
                });
            }
        });
    }
}
