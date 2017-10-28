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
              String  name1 = DesUtil.encrypt(name,SPUtils.getKey());
                web = et_web.getText().toString().trim();
                String web1 = DesUtil.encrypt(web,SPUtils.getKey());
                acount = et_account.getText().toString().trim();
                String acount1 = DesUtil.encrypt(acount,SPUtils.getKey());
                password = et_password.getText().toString().trim();
                String password1 = DesUtil.encrypt(password,SPUtils.getKey());
                tag = et_tag.getText().toString().trim();
                String tag1 = DesUtil.encrypt(tag,SPUtils.getKey());

                AccountBean accountBean = new AccountBean();
                accountBean.setName(name1);
                accountBean.setWebsite(web1);
                accountBean.setAccount(acount1);
                accountBean.setPassword(password1);
                accountBean.setTag(tag1);
                accountBean.setUser(MyApplication.getUser());

                accountBean.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            MyApplication.showToast("保存成功");
                            finish();
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
