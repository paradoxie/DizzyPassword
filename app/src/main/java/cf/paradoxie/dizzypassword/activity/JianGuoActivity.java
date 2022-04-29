package cf.paradoxie.dizzypassword.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.paul623.wdsyncer.SyncConfig;
import com.paul623.wdsyncer.SyncManager;
import com.paul623.wdsyncer.api.OnListFileListener;
import com.paul623.wdsyncer.api.OnSyncResultListener;
import com.paul623.wdsyncer.model.DavData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;

@SuppressLint("HandlerLeak")
public class JianGuoActivity extends BaseActivity {
    private TextView tv_local, tv_local_to_cloud, tv_cloud, tv_cloud_to_local, tv_time;
    private DialogView mDialogView;
    private TextView tv_cloud_path;
    private SyncManager syncManager;
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
                    tv_time.setText("上次同步时间：\n" + time);
                    break;
            }
        }
    };
    private StringBuffer sb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianguo);
        configDavSync();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("坚果云同步");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
        syncManager = new SyncManager(this);

        ThemeUtils.initStatusBarColor(JianGuoActivity.this, ThemeUtils.getPrimaryDarkColor(JianGuoActivity.this));

        tv_local_to_cloud = findViewById(R.id.tv_local_to_cloud);
        tv_local_to_cloud.setOnClickListener(v -> {
            upLoad();
        });
        tv_local = findViewById(R.id.tv_local);
        tv_local.setOnClickListener(v -> {
            outputFile();
        });

        tv_time = findViewById(R.id.tv_time);
        tv_time.setText("上次同步时间：\n" + SPUtils.get("asyncTime", " "));
        tv_cloud = findViewById(R.id.tv_cloud);
        tv_cloud.setOnClickListener(view -> checkDir());
        tv_cloud_to_local = findViewById(R.id.tv_cloud_to_local);
        tv_cloud_to_local.setOnClickListener(v -> {
            if (tv_cloud_path.getText().toString().contains("dizzyPassword_security")) {
                downloadFile(syncManager);
            }
        });

        tv_cloud_path = findViewById(R.id.tv_cloud_path);

    }

    private void outputFile() {
        checkActivity();
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
        config.setPassWord("ajd9gy2yt65ccazq");
        config.setUserAccount("paradoxieix@gmail.com");
    }

    /**
     * 导出加密csv，并上传到坚果云
     */
    public void upLoad() {
        if (DataUtils.exportCsvSecurity()) {
            SyncManager syncManager = new SyncManager(this);
            File saveFile = new File(DataUtils.backFilePath_security);
            syncManager.uploadFile("dizzyPassword_security.csv", "去特么的密码", saveFile, new OnSyncResultListener() {
                @Override
                public void onSuccess(String result) {
                    Looper.prepare();
                    Looper.loop();
                    MyApplication.showToast("同步成功");
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = time;
                    uiHandler.sendMessage(msg);
                }

                @Override
                public void onError(String errorMsg) {
                    Looper.prepare();
                    Looper.loop();
                    MyApplication.showToast("好像没有任何更改...");
                }
            });
        } else {
            MyApplication.showToast("上传失败,请检查应用是否具体权限读写手机储存");
        }


    }

    public void checkDir() {
        Toast.makeText(this, "读取中,请稍后", Toast.LENGTH_SHORT).show();

        syncManager.listAllFile("去特么的密码", new OnListFileListener() {
            @Override
            public void listAll(List<DavData> davResourceList) {
                sb = new StringBuffer();
                for (DavData i : davResourceList) {
                    sb.append(i.getDisplayName() + "\n");
                }
                Log.e("getActivity", sb.toString());
//                tv_path.setText(sb.toString());
                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }

            @Override
            public void onError(String errorMsg) {
                Log.d("去特么的密码", "请求失败:" + errorMsg);
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
                for (AccountBean bean : accountBeans) {
                    Log.e("这是读取出来的", bean.toString());
                }
                MyApplication.showToast("恢复完成");
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
        mDialogView.setAccount(SPUtils.get("name", "") + "");
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
//                Log.e("source-->", sourceString);
                Pattern pattern = Pattern.compile("[^,]*,");
                Matcher matcher = pattern.matcher(sourceString);
                String[] lines = new String[8];
                int i = 0;
                while (matcher.find()) {
                    String find = matcher.group().replace(",", "");
                    lines[i] = find.trim();
//                    Log.e("TAG", "find=" + find + ",i=" + i + ",lines=" + lines[i]);
                    i++;
                }
                AccountBean bean = new AccountBean();
                bean.setName(lines[0]);
                bean.setAccount(lines[1]);
                bean.setPassword(lines[2]);
                bean.setTag(Arrays.asList(lines[3].split("-")));
                bean.setWebsite(lines[4]);
                bean.setNote(lines[5]);
                accountBeans.add(bean);
                a++;
            }
        } catch (NumberFormatException e) {
            Log.e("TAG", "NumberFormatException");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("TAG", "文件不存在");
            e.printStackTrace();
        }
        return accountBeans;
    }
}
