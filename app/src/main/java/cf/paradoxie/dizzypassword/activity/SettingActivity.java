package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;


public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingPreferenceFragment()).commit();
        ThemeUtils.initStatusBarColor(SettingActivity.this, ThemeUtils.getPrimaryDarkColor(SettingActivity.this));
    }

    //    当前页面，主题切换后，需要手动进行颜色修改。下面只修改了状态栏和ToolBar的颜色。
    public void setColor() {
        ThemeUtils.setToolbarColor(SettingActivity.this, ThemeUtils.getPrimaryColor(SettingActivity.this));
        ThemeUtils.setWindowStatusBarColor(SettingActivity.this, ThemeUtils.getPrimaryDarkColor(SettingActivity.this));

    }

    public static class SettingPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private int theme;
        private SharedPreferences sp;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            theme = sp.getInt("theme_change", R.style.Theme7);
        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("theme_change")) {
                int newTheme = sp.getInt("theme_change", theme);
                if (newTheme != theme && getActivity() != null) {
                    SettingActivity settingActivity = (SettingActivity) getActivity();
                    settingActivity.setTheme(newTheme);
                    settingActivity.setColor();
                    this.onCreate(null);
                }
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            Intent intent;
            switch (preference.getKey()) {
                case "id_change":
                    intent = new Intent(AppManager.getAppManager().currentActivity(), TeachActivity.class);
                    startActivity(intent);
                    break;
                case "about":
                    intent = new Intent(AppManager.getAppManager().currentActivity(), AboutActivity.class);
                    startActivity(intent);
                    break;
                case "share":
                    DesUtil.share(AppManager.getAppManager().currentActivity(), getString(R.string.share_note));

                    break;
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
