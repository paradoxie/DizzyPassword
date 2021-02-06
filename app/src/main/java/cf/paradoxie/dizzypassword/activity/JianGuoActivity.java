package cf.paradoxie.dizzypassword.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.paul623.wdsyncer.SyncConfig;
import com.paul623.wdsyncer.SyncManager;
import com.paul623.wdsyncer.api.OnListFileListener;
import com.paul623.wdsyncer.api.OnSyncResultListener;
import com.paul623.wdsyncer.model.DavData;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.BackupBean;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class JianGuoActivity extends BaseActivity {
    private TextView tv_local_to_cloud, tv_cloud_to_local;
    private DialogView mDialogView;
    private TextView tv_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianguo);
        configDavSync();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("坚果云同步");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_csv) {
                checkActivity();
                mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        //校验密码
                        requestAllPower();
                        if (value.equals(SPUtils.get("password", "") + "")) {

                            if (DataUtils.exportCsv()) {
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
            return false;
        });

        ThemeUtils.initStatusBarColor(JianGuoActivity.this, ThemeUtils.getPrimaryDarkColor(JianGuoActivity.this));

        tv_local_to_cloud = (TextView) findViewById(R.id.tv_local_to_cloud);
        tv_cloud_to_local = (TextView) findViewById(R.id.tv_cloud_to_local);
        tv_local_to_cloud.setOnClickListener(v -> {
            upLoad();
//                checkActivity();
//                mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
//                    @Override
//                    public void posClickListener(String value) {
//                        //校验密码
//                        if (value.equals(SPUtils.get("password", "") + "")) {
//                            //生成明文
//
//                            mDialogView.dismiss();
//                        } else {
//                            MyApplication.showToast(R.string.error_pwd);
//                        }
//                    }
//
//                    @Override
//                    public void negCliclListener(String value) {
//                        //取消查看
//                    }
//                });
        });

        tv_cloud_to_local.setOnClickListener(view -> checkDir());


        tv_path = findViewById(R.id.tv_path);
        tv_path.setText(fileIsExists(DataUtils.backFilePath_security) ? "加密文件存在" : "加密文件不存在");
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
                }

                @Override
                public void onError(String errorMsg) {
                    Looper.prepare();
                    Looper.loop();
                }
            });
        } else {
            MyApplication.showToast("导出失败,请检查应用是否具体权限读写手机储存");
        }


    }

    public void checkDir() {
        Toast.makeText(this, "读取中,请稍后", Toast.LENGTH_SHORT).show();
        SyncManager syncManager = new SyncManager(this);

        syncManager.listAllFile("去特么的密码", new OnListFileListener() {
            @Override
            public void listAll(List<DavData> davResourceList) {
                StringBuffer sb = new StringBuffer();
                for (DavData i : davResourceList) {
                    sb.append(i.getDisplayName() + "\n");
                }
                Log.e("getActivity", sb.toString());
                if (sb.toString().contains("dizzyPassword_security")) {
                    downloadFile(syncManager);
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_backup, menu);
        return true;
    }
}
