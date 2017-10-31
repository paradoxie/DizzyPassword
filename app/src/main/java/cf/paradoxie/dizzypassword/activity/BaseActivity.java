package cf.paradoxie.dizzypassword.activity;/**
 * Created by xiehehe on 15/11/8.
 */

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cf.paradoxie.dizzypassword.AppManager;

/**
 * User: xiehehe
 * Date: 2015-11-08
 * Time: 11:59
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        AppManager.getAppManager().addActivity(this);
    }

}
