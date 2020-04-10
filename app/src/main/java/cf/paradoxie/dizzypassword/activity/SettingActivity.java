package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.Constans;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.BaseConfig;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cf.paradoxie.dizzypassword.MyApplication.launchAppDetail;


public class SettingActivity extends BaseActivity {
    private String pwd;
    private DialogView mDialogView;
    private String copyCode = "";

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
        pwd = String.valueOf(SPUtils.get("password", ""));
        mDialogView = new DialogView(SettingActivity.this);


        Intent intent = getIntent();
        copyCode = intent.getStringExtra("copyCode");
        Log.d("----piccodee", copyCode);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_exit) {
                    checkActivity();
                    mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                        @Override
                        public void posClickListener(String value) {
                            //校验密码
                            if (value.equals(SPUtils.get("password", "") + "")) {
                                SPUtils.clear();//清除用户记录
                                BmobUser.logOut();   //清除缓存用户对象
                                AppManager.getAppManager().finishAllActivity();
                                startActivity(new Intent(SettingActivity.this, SignActivity.class));
                                finish();
                                hideInputWindow();
                                mDialogView.dismiss();
                            } else {
                                MyApplication.showToast(R.string.error_pwd);
                            }
                        }

                        @Override
                        public void negCliclListener(String value) {
                            //取消查看
                        }
                    });

                }
                return false;
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingPreferenceFragment()).commit();
        ThemeUtils.initStatusBarColor(SettingActivity.this, ThemeUtils.getPrimaryDarkColor(SettingActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set, menu);
        return true;
    }


    //    当前页面，主题切换后，需要手动进行颜色修改。下面只修改了状态栏和ToolBar的颜色。
    public void setColor() {
        ThemeUtils.setToolbarColor(SettingActivity.this, ThemeUtils.getPrimaryColor(SettingActivity.this));
        ThemeUtils.setWindowStatusBarColor(SettingActivity.this, ThemeUtils.getPrimaryDarkColor(SettingActivity.this));
        getFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingPreferenceFragment()).commit();
    }

    @SuppressLint("ValidFragment")
    public class SettingPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

        private int theme;
        private SharedPreferences sp;
        private Context mContext;

        public SettingPreferenceFragment() {
            mContext = MyApplication.mContext;
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
    }

    private void getVersion() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                        new AlertDialog.Builder(SettingActivity.this, R.style.AlertDialogCustom)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);

    }

    private void checkActivity() {
        mDialogView = new DialogView(SettingActivity.this);
        mDialogView.setMeaasge("确定退出当前帐号？", "帐号:" + (String.valueOf(SPUtils.get("name", ""))) + "\n密码:" + getCodePwd(pwd));
        try {
            if (!SettingActivity.this.isFinishing()) {
                mDialogView.show();
                hideInputWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getCodePwd(String string) {
        if (string.isEmpty() || string == null) {
            return null;
        } else {
            return replaceAction(string, "(?<=\\w{2})\\w(?=\\w{2})");
        }
    }

    private static String replaceAction(String username, String regular) {
        return username.replaceAll(regular, "*");
    }
}
