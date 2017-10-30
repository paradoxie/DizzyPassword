package cf.paradoxie.dizzypassword.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.view.FlowLayout;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddActivity extends AppCompatActivity {
    private EditText et_name, et_note, et_account, et_password, et_tag;
    private Button bt_go;
    private String name, note, acount, password, tag;
    private FlowLayout mFlowLayout;
    private LayoutInflater mInflater;
    private SweetAlertDialog pDialog = null;
    private String[] mVals = new String[]{//常用tag
            "酷安", "腾讯", "游戏", "社交", "新闻", "公司"
            , "家庭", "WIFI", "购物", "京东", "亚马逊", "当当", "小米", "Google"
            , "邮箱", "网易", "知乎", "视频", "豆瓣", "阅读", "技术", "学习", "微博"
            , "公众号", "开车", "工作", "苹果", "阿里", "404", "福利", "音乐"
            , "APP", "看图", "社区", "百度", "论坛", "玩机", "个人", "学术"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加帐号");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initFlowView();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.show();

                name = et_name.getText().toString().trim();
                String name1 = DesUtil.encrypt(name, SPUtils.getKey());
                note = et_note.getText().toString().trim();
                String note1 = DesUtil.encrypt(note, SPUtils.getKey());
                acount = et_account.getText().toString().trim();
                String acount1 = DesUtil.encrypt(acount, SPUtils.getKey());
                password = et_password.getText().toString().trim();
                String password1 = DesUtil.encrypt(password, SPUtils.getKey());
                tag = et_tag.getText().toString().trim();
                //                String tag1 = DesUtil.encrypt(tag, SPUtils.getKey());
                String[] arr = tag.split("\\s+");
                List<String> tag1 = Arrays.asList(arr);

                AccountBean accountBean = new AccountBean();
                accountBean.setName(name1);
                accountBean.setNote(note1);
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
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void initFlowView() {
        mInflater = LayoutInflater.from(this);
        mFlowLayout = (FlowLayout) findViewById(R.id.flowlayout);
        initData();
    }

    private void init() {
        final TextInputLayout nameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        final TextInputLayout accountWrapper = (TextInputLayout) findViewById(R.id.accountWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final TextInputLayout noteWrapper = (TextInputLayout) findViewById(R.id.noteWrapper);
        final TextInputLayout tagWrapper = (TextInputLayout) findViewById(R.id.tagWrapper);
        nameWrapper.setHint("输入您所记录帐号的名称，比如酷安、酷安小号、酷安女号等");
        accountWrapper.setHint("请输入帐号信息");
        passwordWrapper.setHint("请输入密码信息");
        noteWrapper.setHint("请输入备注信息");
        tagWrapper.setHint("标记信息最多可选三个，手动输入请用空格隔开");


        et_name = (EditText) findViewById(R.id.et_name);
        et_note = (EditText) findViewById(R.id.et_web);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_tag = (EditText) findViewById(R.id.et_tag);
        bt_go = (Button) findViewById(R.id.bt_go);
    }

    private void initData() {
        for (int i = 0; i < mVals.length; i++) {
            final TextView tv = (TextView) mInflater.inflate(
                    R.layout.search_label_tv, mFlowLayout, false);
            tv.setTextColor(getResources().getColor(R.color.title_color));
            tv.setText(mVals[i]);

            final String str = tv.getText().toString();


            //点击事件
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断tag数量
                    String string = et_tag.getText().toString().trim();
                    int space = 0;
                    char[] ch = string.toCharArray();
                    for (int j = 0; j < ch.length; j++) {
                        if (ch[j] == ' ') {
                            space++;
                        }
                    }
                    if (space > 1) {
                        MyApplication.showToast("最多支持添加3个tag哟~");
                        return;
                    }
                    et_tag.setText(string + " " + str);
                    tv.setTextSize(10);
                    tv.setTextColor(getResources().getColor(R.color.pressed_color));
                    tv.setBackgroundResource(R.drawable.search_label);
                }
            });
            mFlowLayout.addView(tv);
        }

    }
}
