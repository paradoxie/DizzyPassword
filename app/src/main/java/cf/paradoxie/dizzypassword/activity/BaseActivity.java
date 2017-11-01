package cf.paradoxie.dizzypassword.activity;/**
 * Created by xiehehe on 15/11/8.
 */

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.R;

/**
 * User: xiehehe
 * Date: 2017-10-28
 * Time: 11:59
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        AppManager.getAppManager().addActivity(this);
        mImmersionBar = ImmersionBar.with(this);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.color_13)
                .init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

}
