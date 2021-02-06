package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.paul623.wdsyncer.SyncManager;
import com.paul623.wdsyncer.api.OnListFileListener;
import com.paul623.wdsyncer.api.OnSyncResultListener;
import com.paul623.wdsyncer.model.DavData;

import java.util.List;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.Constans;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.BaseConfig;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cf.paradoxie.dizzypassword.MyApplication.launchAppDetail;

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
                //                    this.onCreate(null);
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
                MyApplication.joinQQGroup(Constans.QQ_ID);
                break;
            case "update":
                getVersion();
                break;
            case "red_package":
//                    ClipboardManager cm = (ClipboardManager) AppManager.getAppManager().currentActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                    if (copyCode.equals("")) {
//                        cm.setText(getString(R.string.red_package_string));
//                    } else {
//                        cm.setText(copyCode);
//                    }
//                    try {
//                        MyApplication.openAppByPackageName(AppManager.getAppManager().currentActivity(), "com.eg.android.AlipayGphone");
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
                MyApplication.showToast("拉倒吧，留着给自己买鸡腿去~~支持一下主页旋转的那碗饭就行啦");
                break;

            case "back_up":
                Intent intent2 = new Intent(AppManager.getAppManager().currentActivity(), BackupActivity.class);
                startActivity(intent2);
                break;

            case "dav":
//                checkDir();
//                upLoad();
//                Intent intent3 = new Intent(AppManager.getAppManager().currentActivity(), JianGuoActivity.class);
//                startActivity(intent3);
                MyApplication.showToast("过年了xdm，年后回来再继续");
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
        final SweetAlertDialog pDialog = new SweetAlertDialog(MyApplication.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));
        pDialog.setTitleText("检查中");
        pDialog.setCancelable(false);
        pDialog.show();
        BmobQuery<BaseConfig> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Constans.CONFIG_ID, new QueryListener<BaseConfig>() {
            @Override
            public void done(BaseConfig baseConfig, BmobException e) {
                if (e == null) {
                    int newVersion = Integer.parseInt(baseConfig.getNewVersion());
                    String title = baseConfig.getTitle();
                    String details = baseConfig.getDetails();
                    if (newVersion > MyApplication.GetVersion()) {//新版本大于本地版本
                        new AlertDialog.Builder(MyApplication.getContext(), R.style.AlertDialogCustom)
                                .setTitle(title)
                                .setMessage(details)
                                .setCancelable(false)
                                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //前往酷安
                                        launchAppDetail(MyApplication.getContext().getPackageName(), "com.coolapk.market");
                                    }
                                })
                                .setNeutralButton("我就不.GIF", null)
                                .show();
                    } else {
                        MyToast.show(MyApplication.getContext(), "没有新版本哦~", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

                    }
                } else {
                    Log.e("-----", e.getMessage());
                }
                pDialog.dismiss();
            }
        });
    }
}
