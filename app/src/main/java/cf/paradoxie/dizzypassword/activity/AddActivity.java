package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.view.FlowLayout;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddActivity extends BaseActivity {
    private TextInputLayout nameWrapper, accountWrapper, passwordWrapper, tagWrapper, noteWrapper;
    private EditText et_name, et_note, et_account, et_password, et_tag;
    private Button bt_go;
    private RxBean rxBean;
    private String name, note, acount, password, tag, id;
    private FlowLayout mFlowLayout;
    private LayoutInflater mInflater;
    private SweetAlertDialog pDialog = null;
    private String[] mVals = new String[]{//常用tag
             "重要", "个人", "公司", "工作", "娱乐","家庭", "APP"
            , "邮箱", "论坛", "游戏", "社交", "视频", "新闻", "阅读", "技术", "看图", "社区"
            , "购物", "玩机", "学术", "福利", "音乐", "学习", "开车", "公众号", "WIFI"
            , "酷安", "腾讯", "网易", "知乎", "豆瓣", "微博", "京东", "阿里", "百度", "小米"
            , "苹果", "亚马逊","微软", "Google", "404"};

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
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String name_1 = bundle.getString("name");
            String account_1 = bundle.getString("account");
            String password_1 = bundle.getString("password");
            String note_1 = bundle.getString("finalNote");
            String tag_1 = bundle.getString("tag");
            id = bundle.getString("id");
            et_name.setText(name_1);
            et_account.setText(account_1);
            et_password.setText(password_1);
            et_note.setText(note_1);
            et_tag.setText(tag_1);
        }


        initFlowView();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);


        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = et_name.getText().toString().trim();
                note = et_note.getText().toString().trim();
                acount = et_account.getText().toString().trim();
                password = et_password.getText().toString().trim();
                tag = et_tag.getText().toString().trim();

                if (name.isEmpty()) {
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError("记录的标题名称不能为空哦~");
                    return;
                } else if (acount.isEmpty()) {
                    accountWrapper.setErrorEnabled(true);
                    accountWrapper.setError("帐号不能为空哦~");
                    return;
                } else if (password.isEmpty()) {
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError("密码不能为空哦~");
                    return;
                } else if (tag.isEmpty()) {
                    tagWrapper.setErrorEnabled(true);
                    tagWrapper.setError("请至少选择一个Tag");
                    return;
                } else {
                    nameWrapper.setError("");// 必须加上这个，否则会导致内容删除后，error信息显示为空白
                    nameWrapper.setErrorEnabled(false);
                    accountWrapper.setError("");
                    accountWrapper.setErrorEnabled(false);
                    passwordWrapper.setError("");
                    passwordWrapper.setErrorEnabled(false);
                    tagWrapper.setError("");
                    tagWrapper.setErrorEnabled(false);
                }
                String note1 = DesUtil.encrypt(note, SPUtils.getKey());
                String name1 = DesUtil.encrypt(name, SPUtils.getKey());
                String acount1 = DesUtil.encrypt(acount, SPUtils.getKey());
                String password1 = DesUtil.encrypt(password, SPUtils.getKey());
                String[] arr = tag.split("\\s+");
                List<String> tag1 = Arrays.asList(arr);

                pDialog.show();
                AccountBean accountBean = new AccountBean();
                accountBean.setName(name1);
                accountBean.setNote(note1);
                accountBean.setAccount(acount1);
                accountBean.setPassword(password1);
                accountBean.setTag(tag1);
                accountBean.setUser(MyApplication.getUser());
                if (id == null) {
                    saveDate(accountBean);
                } else {
                    upDate(accountBean, id);
                }

            }
        });
    }

    private void upDate(AccountBean accountBean, String id) {
        accountBean.update(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyApplication.showToast("更新成功");
                    AppManager.getAppManager().finishActivity(MainActivity.class);
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    MyApplication.showToast("更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
                pDialog.dismiss();
            }
        });
    }

    private void saveDate(AccountBean accountBean) {
        accountBean.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    MyApplication.showToast("保存成功");
                    AppManager.getAppManager().finishActivity(MainActivity.class);
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    MyApplication.showToast("保存失败" + e.getMessage());
                }
                pDialog.dismiss();
            }
        });
    }

    private void initFlowView() {
        mInflater = LayoutInflater.from(this);
        mFlowLayout = (FlowLayout) findViewById(R.id.flowlayout);
        initData();
    }

    private void init() {
        nameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        accountWrapper = (TextInputLayout) findViewById(R.id.accountWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        noteWrapper = (TextInputLayout) findViewById(R.id.noteWrapper);
        tagWrapper = (TextInputLayout) findViewById(R.id.tagWrapper);
        nameWrapper.setHint("帐号的名称，比如酷安、酷安小号、酷安女号等");
        accountWrapper.setHint("帐户信息");
        passwordWrapper.setHint("密码信息");
        noteWrapper.setHint("备注信息，回车即可换行");
        tagWrapper.setHint("选择标记信息,输入请用空格隔开,注:可用于归类检索");


        et_name = (EditText) findViewById(R.id.et_name);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_note = (EditText) findViewById(R.id.et_web);
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
                    if (space > 3) {
                        MyApplication.showToast("最多支持添加5个tag哟~");
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
