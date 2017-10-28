package cf.paradoxie.dizzypassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import cf.paradoxie.dizzypassword.db.AccountBean;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private boolean optionMenuOn = true;  //显示optionmenu
    private Menu aMenu;         //获取optionmenu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检测menu操作，第二次进入app时是否显示menu
        if (!(Boolean) SPUtils.get("optionMenuOn", true)) {
            optionMenuOn = false;
            checkOptionMenu();
            //            MyApplication.showToast(SPUtils.get("optionMenuOn",true)+"");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyApplication.isSign()) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                } else {
                    //缓存用户对象为空时， 可打开用户注册界面…
                    Intent intent = new Intent(MainActivity.this, SignActivity.class);
                    startActivity(intent);
                }
            }
        });

        if (SPUtils.get("key", "") + "" == "") {
            Bmob.initialize(this, "46b1709520ec4d0afa17e505680202da");
            MyApplication.showToast("啥意思");
        } else {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
            MyApplication.showToast("现在是新的key");
        }
        if (!MyApplication.isSign()) {
            //缓存用户对象为空时， 可打开用户注册界面…
            Intent intent = new Intent(MainActivity.this, SignActivity.class);
            startActivity(intent);
        } else {
            findDate();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.get("key", "") + "" != null) {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
            findDate();
        }

    }

    private void findDate() {
        BmobQuery<AccountBean> query = new BmobQuery<>();
        BmobUser user = new BmobUser();
        if (MyApplication.getUser() != null) {
            String id = MyApplication.getUser().getObjectId();

            user.setObjectId(id);
            query.addWhereEqualTo("user", new BmobPointer(user));
            query.findObjects(new FindListener<AccountBean>() {

                @Override
                public void done(List<AccountBean> objects, BmobException e) {
                    if (objects != null) {
                        for (AccountBean list : objects) {
                            MyApplication.showToast(list.getName().toString());
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_change) {
            // 跳转到一个web页面，获取并修改Bmob的key的教程
            Intent intent = new Intent(MainActivity.this, TeachActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_delete) {
            //            MyApplication.showToast(R.string.action_delete + "");
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("隐藏key配置入口")
                    .setContentText("点击确定后将使用此刻配置中的key，右上角的三个点将消失")
                    .setCancelText("我再看看吧")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            optionMenuOn = false;
                            SPUtils.put("optionMenuOn", optionMenuOn);
                            checkOptionMenu();
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    //是否显示menu
    private void checkOptionMenu() {
        if (null != aMenu) {
            if (optionMenuOn) {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(true);
                    aMenu.getItem(i).setEnabled(true);
                }
            } else {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(false);
                    aMenu.getItem(i).setEnabled(false);
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        aMenu = menu;
        checkOptionMenu();
        return super.onPrepareOptionsMenu(menu);
    }
}
