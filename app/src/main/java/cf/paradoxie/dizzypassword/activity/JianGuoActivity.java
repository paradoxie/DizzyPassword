package cf.paradoxie.dizzypassword.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.Constans;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.RxBean;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cf.paradoxie.dizzypassword.view.MyDialog;
import cf.paradoxie.dizzypassword.wdsyncer.SyncConfig;
import cf.paradoxie.dizzypassword.wdsyncer.SyncManager;
import cf.paradoxie.dizzypassword.wdsyncer.api.OnListFileListener;
import cf.paradoxie.dizzypassword.wdsyncer.api.OnSyncResultListener;
import cf.paradoxie.dizzypassword.wdsyncer.model.DavData;

@SuppressLint("HandlerLeak")
public class JianGuoActivity extends BaseActivity {
    private TextView tv_local, tv_local_to_cloud, tv_cloud, tv_cloud_to_local, tv_time, tv_time_down;
    private DialogView mDialogView;
    private TextView tv_cloud_path;
    private SyncManager syncManager;
    private boolean hasConfig;
    private final Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_cloud_path.setText("云端读取成功：\n" + sb.toString());
                    break;

                case 2:
                    String time = (String) msg.obj;
                    SPUtils.put("asyncTime", time);
                    SPUtils.put(Constans.UN_BACK, "0");
                    tv_time.setText("已上传：\n" + time + "\n 请检查坚果云后台是否存在数据");
                    break;
                case 3:
                    MyApplication.showToast("恢复完成，请回到列表界面点击右上角刷新按钮");
                    String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    SPUtils.put("asyncTimeDown", time1);
                    tv_time_down.setText("已恢复：\n" + time1 + "\n 请回到列表界面点击右上角刷新按钮");
                    break;
            }
        }
    };
    private StringBuffer sb;

    private TextView tv_notify, tv_account, tv_pwd;
    private LinearLayout ll_jianguo;
    private String jianguo_account, pwd;
    private EditText edit_account, edit_pwd;
    SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy/M/d HH:mm:ss");
    SimpleDateFormat sdfTarget = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianguo);
//        configDavSync();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("坚果云同步");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
        requestAllPower();
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_jian) {
                configJianGuoDialog();
            }
            return false;
        });

        syncManager = new SyncManager(this);

        ThemeUtils.initStatusBarColor(JianGuoActivity.this, ThemeUtils.getPrimaryDarkColor(JianGuoActivity.this));


        tv_notify = findViewById(R.id.tv_notify);
        tv_account = findViewById(R.id.tv_account);
        tv_pwd = findViewById(R.id.tv_pwd);
        ll_jianguo = findViewById(R.id.ll_jianguo);

        setJianGuoAccount();

        tv_local_to_cloud = findViewById(R.id.tv_local_to_cloud);
        tv_local_to_cloud.setOnClickListener(v -> {
            if (checkPwd()) {
                upLoad();
            } else {
                configJianGuoDialog();
            }
        });
        tv_local = findViewById(R.id.tv_local);
        tv_local.setOnClickListener(v -> {
            outputFile();
        });

        tv_time = findViewById(R.id.tv_time);
        tv_time_down = findViewById(R.id.tv_time_down);
        tv_time.setText("上次上传时间：\n" + SPUtils.get("asyncTime", ""));
        tv_time_down.setText("上次恢复时间：\n" + SPUtils.get("asyncTimeDown", ""));
        tv_cloud = findViewById(R.id.tv_cloud);
        tv_cloud.setOnClickListener(view -> {
            if (checkPwd()) {
                checkDir();
            } else {
                configJianGuoDialog();
            }
        });
        tv_cloud_to_local = findViewById(R.id.tv_cloud_to_local);
        tv_cloud_to_local.setOnClickListener(v -> {
            if (tv_cloud_path.getText().toString().contains("dizzyPassword_security")) {
                downloadFile(syncManager);
                MyApplication.showToast("恢复完成，请回到列表界面点击右上角刷新按钮");
//                RxBus.getInstance().post(new RxBean("refresh"));
            } else {
                MyApplication.showToast("请先点击【检查云端文件】");
            }
        });

        tv_cloud_path = findViewById(R.id.tv_cloud_path);

    }

    private boolean checkPwd() {
        String pwd = SPUtils.get("jianguo_pwd", "") + "";
        return !TextUtils.isEmpty(pwd);
    }

    private void configJianGuoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);

        final View dialog = factory.inflate(R.layout.dialog, null);
        edit_account = dialog.findViewById(R.id.dialog_ed_account);
        edit_pwd = dialog.findViewById(R.id.dialog_ed_pwd);
        builder.setTitle("配置坚果云");    //设置对话框标题
        builder.setIcon(R.drawable.jian_0);   //设置对话框标题前的图标

        builder.setView(dialog);
        builder.setPositiveButton("确认", null);
        builder.setNegativeButton("取消", null);
        AlertDialog mDialog = builder.create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positionButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positionButton.setOnClickListener(v -> {
                    String s = edit_account.getText().toString();
                    String s1 = edit_pwd.getText().toString();
                    if (s.length() < 1 || s1.length() < 1) {
                        MyApplication.showToast("云服务账户或密码不能为空");
                    } else {
                        SPUtils.put("jianguo_account", s);
                        SPUtils.put("jianguo_pwd", s1);
                        setJianGuoAccount();
                        mDialog.dismiss();
                    }

                });
                negativeButton.setOnClickListener(v -> {
                    MyApplication.showToast("取消");
                    mDialog.dismiss();
                });
            }
        });
        mDialog.show();
    }

    /**
     * 设置坚果云账号
     */
    private void setJianGuoAccount() {
        jianguo_account = SPUtils.get("jianguo_account", "") + "";

        if ("".equals(jianguo_account)) {
            hasConfig = false;
            tv_notify.setVisibility(View.VISIBLE);
            ll_jianguo.setVisibility(View.GONE);
            configJianGuoDialog();
        } else {
            hasConfig = true;
            tv_notify.setVisibility(View.GONE);
            ll_jianguo.setVisibility(View.VISIBLE);

            pwd = SPUtils.get("jianguo_pwd", "") + "";
            tv_account.setText("账号：" + jianguo_account);
            tv_pwd.setText("密码：" + Utils.getCodePwd(pwd));
            configDavSync();
        }
    }

    private void outputFile() {
        checkActivity();
        requestAllPower();
        mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
            @Override
            public void posClickListener(String value) {
                //校验密码
                requestAllPower();
                if (value.equals(SPUtils.get("password", "") + "")) {
                    if (DataUtils.exportCsvSecurity()) {
                        MyApplication.showToast("成功导出至根目录");
                    } else {
                        MyApplication.showToast("导出失败,请检查应用是否具体权限读写手机储存");
                    }
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

    /**
     * 配置sync
     */
    public void configDavSync() {
        SyncConfig config = new SyncConfig(this);
        config.setServerUrl("https://dav.jianguoyun.com/dav/");
        config.setUserAccount(jianguo_account);
        config.setPassWord(pwd);
    }

    /**
     * 导出加密csv，并上传到坚果云
     */
    public void upLoad() {
        if (DataUtils.exportCsvSecurity()) {
            SyncManager syncManager = new SyncManager(this);
            File saveFile = new File(DataUtils.backFilePath_security);
            MyDialog.getInstance().show(this, "上传中...");
            syncManager.uploadFile("dizzyPassword_security.csv", "去特么的密码", saveFile, new OnSyncResultListener() {
                @Override
                public void onSuccess(String result) {
                    Looper.prepare();

                    MyApplication.showToast("同步成功");
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = time;
                    uiHandler.sendMessage(msg);
                    MyDialog.getInstance().dismiss();
                    Looper.loop();
                }

                @Override
                public void onError(String errorMsg) {
                    Looper.prepare();
                    MyApplication.showToast("好像没有任何更改...");
                    MyDialog.getInstance().dismiss();
                    Looper.loop();
                }
            });
        } else {
            MyApplication.showToast("上传失败,请检查应用是否具体权限读写手机储存");
        }


    }

    public void checkDir() {
        MyDialog.getInstance().show(this, "读取中,请稍后...");
        syncManager.listAllFile("去特么的密码", new OnListFileListener() {
            @Override
            public void listAll(List<DavData> davResourceList) {
                sb = new StringBuffer();
                for (DavData i : davResourceList) {
                    sb.append(i.getDisplayName() + "\n");
                }
                Log.e("getActivity", sb.toString());
                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
                MyDialog.getInstance().dismiss();
            }

            @Override
            public void onError(String errorMsg) {
                Log.d("去特么的密码", "请求失败:" + errorMsg);
                MyDialog.getInstance().dismiss();
                MyApplication.showToast("读取失败:" + errorMsg);
            }
        });

    }

    /**
     * 下载文件
     *
     * @param syncManager
     */
    private void downloadFile(SyncManager syncManager) {
        syncManager.downloadFile("dizzyPassword_security.csv", "去特么的密码", new OnSyncResultListener() {
            @Override
            public void onSuccess(String result) {
                Log.e("这是下载", result);
                //result为文件路径，解析csv,存入sp，然后重置主界面内容
                List<AccountBean> accountBeans = readCSV(result);
                Log.e("解析完成", accountBeans.size() + "条");
                for (AccountBean bean : accountBeans) {
                    Log.e("这是读取出来的", bean.toString());
                }
                Log.e("存入本地", "");
                SPUtils.setDataList("beans", accountBeans);
                SPUtils.put(Constans.UN_BACK, "0");
                Log.e("完成", "");
                Message msg = new Message();
                msg.what = 3;
                uiHandler.sendMessage(msg);
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    //申请权限，需要使用之前申请
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
            }
        }
    }


    private void checkActivity() {
        mDialogView = new DialogView(JianGuoActivity.this);
        mDialogView.setMeaasge(SPUtils.get("name", "") + "", "\n密码:" + Utils.getCodePwd(String.valueOf(SPUtils.get("password", ""))));
        try {
            if (!JianGuoActivity.this.isFinishing()) {
                mDialogView.show();
                hideInputWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private List<AccountBean> readCSV(String path) {
        List<AccountBean> accountBeans = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileInputStream fiStream;
        Scanner scanner;
        try {
            fiStream = new FileInputStream(file);
            scanner = new Scanner(fiStream, "UTF-8");
            scanner.nextLine();//读下一行,把表头越过。不注释的话第一行数据就越过去了
            int a = 0;
            while (scanner.hasNextLine()) {
                String sourceString = scanner.nextLine();
                Pattern pattern = Pattern.compile("[^,]*,");
                Matcher matcher = pattern.matcher(sourceString);
                String[] lines = sourceString.split(",");
                AccountBean bean = new AccountBean();
                bean.setObjectId(lines[0]);
                bean.setName(lines[1]);
                bean.setAccount(lines[2]);
                bean.setPassword(lines[3]);
                bean.setTag(Arrays.asList(lines[4].split("-")));
                bean.setWebsite(lines[5]);
                bean.setNote(lines[6]);
                bean.setCreateAtTime(lines[7]);
                bean.setUpdatedAt(lines[8]);
                accountBeans.add(bean);
                a++;
            }
        } catch (Exception e) {
            Looper.prepare();
            Log.e("TAG", e.toString());
            MyApplication.showToast("恢复失败，请重新尝试");
            e.printStackTrace();
            Looper.loop();
        }
        return accountBeans;
    }

    /**
     * 时间格式转换
     *
     * @param oldTime
     * @return
     */
    private String getTime(String oldTime) {
        try {
            return sdfTarget.format(sdfSource.parse(oldTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jianguo, menu);
        return true;
    }


}
