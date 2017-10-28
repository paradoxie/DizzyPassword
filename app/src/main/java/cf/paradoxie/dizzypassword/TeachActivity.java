package cf.paradoxie.dizzypassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class TeachActivity extends Activity {
    private EditText et_key;
    private Button bt_go;
    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);
        init();
    }

    private void init() {


        et_key = (EditText) findViewById(R.id.et_key);
        bt_go = (Button) findViewById(R.id.bt_go);
        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = et_key.getText().toString().trim();
                new SweetAlertDialog(TeachActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定修改为当前key吗？")
                        .setContentText("请确认已完成上面教程中的蛇皮操作")
                        .setCancelText("算了，用自带的")
                        .setConfirmText("我确定")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("配置完成!")
                                        .setContentText("重启app即可生效(*^__^*)")
                                        .setConfirmText("重启")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                SPUtils.put("key", key);
                                                if (SPUtils.get("key", "") != "") {
                                                    MyApplication.showToast(SPUtils.get("key", "") + "");
                                                    //重新启动app
                                                    Intent i = getBaseContext().getPackageManager()
                                                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(i);
                                                }
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();

            }
        });
    }

}
