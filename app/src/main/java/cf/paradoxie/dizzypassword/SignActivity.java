package cf.paradoxie.dizzypassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SignActivity extends AppCompatActivity {
    private EditText et_username, et_password;
    private Button bt_sign;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Bmob.initialize(this, "46b1709520ec4d0afa17e505680202da");


        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        bt_sign = (Button) findViewById(R.id.bt_sign);
        bt_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();

                BmobUser user = new BmobUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(username);
                user.signUp(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser objectId, BmobException e) {
                        if (e == null) {
                            toast("注册成功，用户objectId为：" + objectId);
                        } else {
                            toast("注册失败：" + e.getMessage());
                        }
                    }

                });
            }
        });

    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
