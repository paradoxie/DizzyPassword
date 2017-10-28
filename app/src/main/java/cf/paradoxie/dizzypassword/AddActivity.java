package cf.paradoxie.dizzypassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cf.paradoxie.dizzypassword.db.AccountBean;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddActivity extends AppCompatActivity {
    private EditText et_name, et_web, et_account, et_password, et_tag;
    private Button bt_go;
    private String name, web, acount, password, tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();

        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = et_name.getText().toString().trim();
                web = et_web.getText().toString().trim();
                acount = et_account.getText().toString().trim();
                password = et_password.getText().toString().trim();
                tag = et_tag.getText().toString().trim();

                AccountBean accountBean = new AccountBean();
                accountBean.setName(name);
                accountBean.setWebsite(web);
                accountBean.setAccount(acount);
                accountBean.setPassword(password);
                accountBean.setTag(tag);
                accountBean.setUser(MyApplication.getUser());

                accountBean.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            MyApplication.showToast("保存成功");
                        } else {
                            MyApplication.showToast("保存失败" + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void init() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_web = (EditText) findViewById(R.id.et_web);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_tag = (EditText) findViewById(R.id.et_tag);
        bt_go = (Button) findViewById(R.id.bt_go);
    }
}
