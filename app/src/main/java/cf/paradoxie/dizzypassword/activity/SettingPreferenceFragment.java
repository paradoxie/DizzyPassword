package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.util.Log;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.Constans;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AppConfig;
import cf.paradoxie.dizzypassword.bean.BaseConfig;
import cf.paradoxie.dizzypassword.data.DataDeal;
import cf.paradoxie.dizzypassword.http.HttpListener;
import cf.paradoxie.dizzypassword.http.HttpUtils;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cf.paradoxie.dizzypassword.base.MyApplication.launchAppDetail;

@SuppressLint("ValidFragment")
public class SettingPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private int theme;
    private SharedPreferences sp;

    public SettingPreferenceFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SwitchPreference switch_preference = (SwitchPreference) findPreference("is_finger");
        switch_preference.setOnPreferenceChangeListener(this);
        SwitchPreference switch_preference_image = (SwitchPreference) findPreference("is_head_image");
        switch_preference_image.setOnPreferenceChangeListener(this);

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
        SettingActivity settingActivity = (SettingActivity) getActivity();
        if (key.equals("theme_change")) {
            int newTheme = sp.getInt("theme_change", theme);
            if (newTheme != theme && getActivity() != null) {
                settingActivity.setTheme(newTheme);
                settingActivity.setColor();
            }
        } else if (key.equals("id_change")) {
            Preference pref = findPreference(key);
            EditTextPreference etp = (EditTextPreference) pref;
            Editable num = etp.getEditText().getText();
            String killTime = num.toString();
            if (!killTime.isEmpty()) {
                SPUtils.put("killTime", killTime);
                MyToast.show(settingActivity, "后台存活时长修改成功:" + killTime + "秒", ThemeUtils.getPrimaryColor(settingActivity));

            }
            etp.getEditText().setText("");

        } else if (key.equals("id_code")) {
            Preference pref = findPreference(key);
            EditTextPreference etp = (EditTextPreference) pref;
            Editable num = etp.getEditText().getText();
            String pwd = num.toString();
            if (pwd.length() != 6) {
                MyToast.show(settingActivity, "修改失败，安全码必须6位哦！", ThemeUtils.getPrimaryColor(settingActivity));
            } else {
                SPUtils.put("pwd", pwd);
                MyToast.show(settingActivity, "安全码修改成功(*^__^*)", ThemeUtils.getPrimaryColor(settingActivity));
            }

            etp.getEditText().setText("");
        }
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        switch (preference.getKey()) {
            case "id_change":

                break;

            case "is_finger":

                break;
            case "is_head_image":

                break;
            case "about":
                Intent intent1 = new Intent(AppManager.getAppManager().currentActivity(), AboutActivity.class);
                startActivity(intent1);
                break;
            case "share":
                DesUtil.share(AppManager.getAppManager().currentActivity(), getString(R.string.share_note));
                break;
            case "group":
                MyApplication.joinQQGroup();
                break;
            case "update":
                getVersion();
                break;
            case "red_package":
                MyApplication.showToast("拉倒吧，留着给自己买鸡腿去~~支持一下主页右下角那碗饭就行啦");
                break;

            case "back_up":
                Intent intent2 = new Intent(AppManager.getAppManager().currentActivity(), BackupActivity.class);
                startActivity(intent2);
                break;

            case "dav":
//                checkDir();
//                upLoad();
                Intent intent3 = new Intent(AppManager.getAppManager().currentActivity(), JianGuoActivity.class);
                startActivity(intent3);
//                MyApplication.showToast("过年了xdm，年后回来再继续");
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "is_finger":
                Log.d("-----", newValue + "");
                SPUtils.put("isFinger", newValue + "");
                break;

            case "is_head_image":
                Log.d("-----", newValue + "");
                SPUtils.put("isHeadImage", newValue + "");
                break;

            default:
                break;
        }
        return true;
    }

    private void getVersion() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));
        pDialog.setTitleText("检查中");
        pDialog.setCancelable(false);
        pDialog.show();
        HttpUtils.getInstance().getAppConfigUrl(new HttpListener<AppConfig>() {
            @Override
            public void success(AppConfig appConfig) {
                Log.e("TAG", appConfig.toString());
                int locatedVersionCode = Utils.GetVersion();
                DataDeal.getInstance(getActivity()).setAppConfig(appConfig);
                int tencentVersionCode = appConfig.getVersion_code();
                if (tencentVersionCode > locatedVersionCode) {
                    getActivity().runOnUiThread(() -> {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("有重大更新")
                                .setMessage("赶快前往酷安更新吧")
                                .setCancelable(false)
                                .setPositiveButton("前往", (dialogInterface, i) -> {
                                    //前往酷安
                                    MyApplication.launchAppDetail(MyApplication.getContext().getPackageName(), "com.coolapk.market");
                                })
                                .setNeutralButton("我就不.GIF", (d, i) -> {
                                    MyApplication.showToast("你等着！");
                                }).show();
                    });
                }
                pDialog.dismiss();

            }

            @Override
            public void failed() {
                pDialog.dismiss();
            }
        });
    }
}
