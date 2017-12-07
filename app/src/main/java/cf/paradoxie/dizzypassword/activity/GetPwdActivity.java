package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mixiaoxiao.smoothcompoundbutton.SmoothSwitch;
import com.xw.repo.BubbleSeekBar;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.utils.GetPwdUtils;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.view.SingleLineTextView;

/**
 * Created by xiehehe on 2017/12/5.
 */

public class GetPwdActivity extends Activity {
    //单4
    private static final String NUMBER_CHAR = "0123456789";//数字
    public static final String LOWER_LETTER_CHAR = "abcdefghijkllmnopqrstuvwxyz";//小写
    public static final String UPPER_LETTER_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//大写
    public static final String STRING_CHAR = ".*[~!@#$%^&.?]{,}_-+=<>:;/";//字符
    //双6
    public static final String LOWER_NUMBER_CHAR = NUMBER_CHAR + LOWER_LETTER_CHAR;//数字+小写
    public static final String UPPER_NUMBER_CHAR = NUMBER_CHAR + UPPER_LETTER_CHAR;//数字+大写
    public static final String STRING_NUMBER_CHAR = NUMBER_CHAR + STRING_CHAR;//数字+字符
    public static final String STRING_LOWER_CHAR = LOWER_LETTER_CHAR + STRING_CHAR;//字符+小写
    public static final String STRING_UPPER_CHAR = UPPER_LETTER_CHAR + STRING_CHAR;//字符+大写
    public static final String LOWER_UPPER_CHAR = UPPER_LETTER_CHAR + LOWER_LETTER_CHAR;//小写+大写
    //三4
    public static final String STRING_LOWER_NUMBER_CHAR = LOWER_NUMBER_CHAR + STRING_CHAR;//数字+小写+字符
    public static final String STRING_UPPER_NUMBER_CHAR = UPPER_NUMBER_CHAR + STRING_CHAR;//数字+大写+字符
    public static final String STRING_UPPER_LOWER_CHAR = LOWER_UPPER_CHAR + STRING_CHAR;//小写+大写+字符
    public static final String NUMBER_UPPER_LOWER_CHAR = LOWER_UPPER_CHAR + NUMBER_CHAR;//小写+大写+数字
    //四1
    public static final String ALL_CHAR = NUMBER_CHAR + STRING_CHAR + LOWER_LETTER_CHAR + UPPER_LETTER_CHAR;

    private SingleLineTextView et_pwd;
    private BubbleSeekBar sb_num;
    private SmoothSwitch ss1, ss2, ss3, ss4;
    private Button btn_get, btn_sure;
    private int num;
    private String pwd;
    private RxBean mRxBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpwd);
        init();
    }

    @SuppressLint("WrongViewCast")
    private void init() {
        et_pwd = (SingleLineTextView) findViewById(R.id.et_pwd);
        sb_num = (BubbleSeekBar) findViewById(R.id.sb_num);
        ss1 = (SmoothSwitch) findViewById(R.id.ss1);
        ss2 = (SmoothSwitch) findViewById(R.id.ss2);
        ss3 = (SmoothSwitch) findViewById(R.id.ss3);
        ss4 = (SmoothSwitch) findViewById(R.id.ss4);

        mRxBean = new RxBean();
        sb_num.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                num = progress;
            }
        });

        btn_get = (Button) findViewById(R.id.btn_get);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num<6){
                    MyApplication.showToast("请选择密码位数");
                    return;
                }
                if (ss1.isChecked() && !ss2.isChecked() && !ss3.isChecked() && !ss4.isChecked()) {
                    //1仅数字
                    pwd = GetPwdUtils.getPwd(num, NUMBER_CHAR);
                }else if (!ss1.isChecked() && ss2.isChecked() && !ss3.isChecked() && !ss4.isChecked()){
                    //2仅小写
                    pwd = GetPwdUtils.getPwd(num, LOWER_LETTER_CHAR);
                }else if (!ss1.isChecked() && !ss2.isChecked() && ss3.isChecked() && !ss4.isChecked()){
                    //3仅大写
                    pwd = GetPwdUtils.getPwd(num, UPPER_LETTER_CHAR);
                }else if (!ss1.isChecked() && !ss2.isChecked() && !ss3.isChecked() && ss4.isChecked()){
                    //4仅字符
                    pwd = GetPwdUtils.getPwd(num, STRING_CHAR);
                }else if (ss1.isChecked() && ss2.isChecked() && !ss3.isChecked() && !ss4.isChecked()){
                    //5数字+小写
                    pwd = GetPwdUtils.getPwd(num, LOWER_NUMBER_CHAR);
                }else if (ss1.isChecked() && !ss2.isChecked() && ss3.isChecked() && !ss4.isChecked()){
                    //6数字+大写
                    pwd = GetPwdUtils.getPwd(num, UPPER_NUMBER_CHAR);
                }else if (ss1.isChecked() && !ss2.isChecked() && !ss3.isChecked() && ss4.isChecked()){
                    //7数字+字符
                    pwd = GetPwdUtils.getPwd(num, STRING_NUMBER_CHAR);
                }else if (!ss1.isChecked() && ss2.isChecked() && !ss3.isChecked() && ss4.isChecked()){
                    //8字符+小写
                    pwd = GetPwdUtils.getPwd(num, STRING_LOWER_CHAR);
                }else if (!ss1.isChecked() && !ss2.isChecked() && ss3.isChecked() && ss4.isChecked()){
                    //9字符+大写
                    pwd = GetPwdUtils.getPwd(num, STRING_UPPER_CHAR);
                }else if (!ss1.isChecked() && ss2.isChecked() && ss3.isChecked() && !ss4.isChecked()){
                    //10小写+大写
                    pwd = GetPwdUtils.getPwd(num, LOWER_UPPER_CHAR);
                }else if (ss1.isChecked() && ss2.isChecked() && !ss3.isChecked() && ss4.isChecked()){
                    //11数字+小写+字符
                    pwd = GetPwdUtils.getPwd(num, STRING_LOWER_NUMBER_CHAR);
                }else if (ss1.isChecked() && !ss2.isChecked() && ss3.isChecked() && ss4.isChecked()){
                    //12数字+大写+字符
                    pwd = GetPwdUtils.getPwd(num, STRING_UPPER_NUMBER_CHAR);
                }else if (!ss1.isChecked() && ss2.isChecked() && ss3.isChecked() && ss4.isChecked()){
                    //13小写+大写+字符
                    pwd = GetPwdUtils.getPwd(num, STRING_UPPER_LOWER_CHAR);
                }else if (ss1.isChecked() && ss2.isChecked() && ss3.isChecked() && !ss4.isChecked()){
                    //14小写+大写+数字
                    pwd = GetPwdUtils.getPwd(num, NUMBER_UPPER_LOWER_CHAR);
                }else if (ss1.isChecked() && ss2.isChecked() && ss3.isChecked() && ss4.isChecked()){
                    //15全部
                    pwd = GetPwdUtils.getPwd(num, ALL_CHAR);
                }
                et_pwd.setText(pwd);
            }
        });
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回密码到上一个界面
                if (et_pwd.getText()==""){
                    MyApplication.showToast("啥也没有哦~");
                    return;
                }
                mRxBean.setPwd(et_pwd.getText().toString().trim());
                RxBus.getInstance().post(mRxBean);
                finish();
            }
        });
    }
}
