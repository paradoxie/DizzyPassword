package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.RxBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.view.FlowLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class AddActivity extends BaseActivity {
    private TextInputLayout nameWrapper, accountWrapper, passwordWrapper, tagWrapper, noteWrapper;
    private EditText et_name, et_note, et_account, et_password, et_tag, et_web;
    private String name, note, acount, password, tag, id, web;
    private ImageView btn_get_pwd;
    private FlowLayout mFlowLayout;
    private LayoutInflater mInflater;
    private SweetAlertDialog pDialog = null;
    private String[] mVals = new String[]{//常用tag
            "重要", "个人", "公司", "工作", "娱乐", "家庭", "小号"
            , "邮箱", "论坛", "游戏", "社交", "视频", "新闻", "阅读", "技术", "看图", "社区"
            , "购物", "玩机", "学术", "福利", "音乐", "摄影", "漫画", "学习", "开车", "公众号", "WIFI"
            , "云盘", "QQ", "浏览器", "羊毛", "支付", "VPN", "贴吧", "二次元"
            , "酷安", "腾讯", "网易", "知乎", "豆瓣", "微博", "京东", "阿里", "百度", "小米"
            , "苹果", "亚马逊", "微软", "Google", "404"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("添加帐号");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> finish());


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> actionDone());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String name_1 = bundle.getString("name");
            String account_1 = bundle.getString("account");
            String password_1 = bundle.getString("password");
            String web_1 = bundle.getString("web");
            String note_1 = bundle.getString("finalNote");
            String tag_1 = bundle.getString("tag");
            id = bundle.getString("id");
            et_name.setText(name_1);
            et_account.setText(account_1);
            et_password.setText(password_1);
            et_web.setText(web_1);
            et_note.setText(note_1);
            et_tag.setText(tag_1);
        }


        initFlowView();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

    }

    private void actionDone() {
        name = et_name.getText().toString().trim();
        note = et_note.getText().toString().trim();
        acount = et_account.getText().toString().trim();
        password = et_password.getText().toString().trim();
        web = et_web.getText().toString().trim();
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
        } else if (tag.split("\\s+").length < 2) {
            tagWrapper.setErrorEnabled(true);
            tagWrapper.setError("请至少选择2-3个Tag");
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
        String web1 = DesUtil.encrypt(web, SPUtils.getKey());
        String password1 = DesUtil.encrypt(password, SPUtils.getKey());
        String[] arr = tag.split("\\s+");
        List<String> tag1 = Arrays.asList(arr);

        pDialog.show();
        AccountBean accountBean = new AccountBean();
        accountBean.setName(name1);
        accountBean.setNote(note1);
        accountBean.setAccount(acount1);
        accountBean.setWebsite(web1);
        accountBean.setPassword(password1);
        accountBean.setTag(tag1);
        accountBean.setUser(MyApplication.getUser());
        accountBean.setObjectId(getObjectId());
        if (id == null) {
            //0d52d42744
            saveDate(accountBean);
        } else {
            upDate(accountBean, id);
        }
    }

    private void upDate(AccountBean accountBean, String id) {

        //获取所有list
        List<AccountBean> beans = SPUtils.getDataList("beans", AccountBean.class);
        //删除这个id的条目
        Iterator<AccountBean> iterator = beans.iterator();
        while (iterator.hasNext()) {
            AccountBean next = iterator.next();
            if (id.equals(next.getObjectId())) {
                accountBean.setCreateAtTime(next.getCreatedAt());
                accountBean.setUpdatedAt(getCurrentDate());
                iterator.remove();
            }
        }

        //新条目添加到list
        beans.add(accountBean);
        //储存新的list
        SPUtils.setDataList("beans", beans);

        pDialog.dismiss();
        MyApplication.showToast("更新成功");
        AppManager.getAppManager().finishActivity(JianGuoMainActivity.class);
        Intent intent = new Intent(AddActivity.this, JianGuoMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveDate(AccountBean accountBean) {

        //获取所有list
        List<AccountBean> beans = SPUtils.getDataList("beans", AccountBean.class);
        //新条目添加到list
        accountBean.setCreateAtTime(getCurrentDate());
        accountBean.setUpdatedAt(getCurrentDate());
        beans.add(accountBean);
        //储存新的list
        SPUtils.setDataList("beans", beans);

        pDialog.dismiss();
        MyApplication.showToast("保存成功");
        AppManager.getAppManager().finishActivity(JianGuoMainActivity.class);
        Intent intent = new Intent(AddActivity.this, JianGuoMainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 获取当前日期时间
     *
     * @return
     */
    private String getCurrentDate() {
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(calendar);
    }

    private void initFlowView() {
        mInflater = LayoutInflater.from(this);
        mFlowLayout = findViewById(R.id.flowlayout);
        initData();
    }

    private void init() {
        nameWrapper = findViewById(R.id.nameWrapper);
        accountWrapper = findViewById(R.id.accountWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        noteWrapper = findViewById(R.id.noteWrapper);
        tagWrapper = findViewById(R.id.tagWrapper);
        nameWrapper.setHint("名称,比如酷安、酷安小号、酷安女号等");
        accountWrapper.setHint("帐户,邮箱、电话、用户名");
        passwordWrapper.setHint("密码,点击左侧图标可自动生成哦");
        noteWrapper.setHint("备注,回车即可换行");
        tagWrapper.setHint("标记,点击选择,输入请用空格隔开,注:可用于归类检索");


        et_name = findViewById(R.id.et_name);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        et_web = findViewById(R.id.et_website);
        et_note = findViewById(R.id.et_web);
        et_tag = findViewById(R.id.et_tag);
        btn_get_pwd = findViewById(R.id.btn_get_pwd);
        btn_get_pwd.setOnClickListener(view -> {
            //返回密码
            Intent intent = new Intent(AddActivity.this, GetPwdActivity.class);
            startActivity(intent);
        });


    }

    @SuppressLint("ResourceType")
    private void initData() {
        for (int i = 0; i < mVals.length; i++) {
            final TextView tv = (TextView) mInflater.inflate(
                    R.layout.search_label_tv, mFlowLayout, false);
            tv.setTextColor(getResources().getColor(R.color.color_bg));
            tv.setTextSize(14);
            tv.setText(mVals[i]);
            final String str = tv.getText().toString();

            //点击事件
            tv.setOnClickListener(v -> {
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
            });
            mFlowLayout.addView(tv);
        }

    }

    /**
     * 生成随机id
     *
     * @return objectId
     */
    private String getObjectId() {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            int number = random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();

        RxBus.getInstance().register(RxBean.class, rxBean -> {
            if (rxBean.getPwd() == "") {
                return;
            } else {
                et_password.setText(rxBean.getPwd());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
