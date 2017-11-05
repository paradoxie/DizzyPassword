package cf.paradoxie.dizzypassword.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.TestStackAdapter;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.SortByTime;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements CardStackView.ItemExpendListener {
    private boolean optionMenuOn = true;  //显示optionmenu
    private Menu aMenu;         //获取optionmenu
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4,
            R.color.color_5, R.color.color_6, R.color.color_7, R.color.color_8,
            R.color.color_9, R.color.color_10, R.color.color_11, R.color.color_12,
            R.color.color_13, R.color.color_14, R.color.color_15, R.color.color_16,
            R.color.color_17, R.color.color_18, R.color.color_19, R.color.color_20,
            R.color.color_21, R.color.color_22, R.color.color_23, R.color.color_24,
            R.color.color_25, R.color.color_26
    };

    private CardStackView mStackView;
    private TestStackAdapter mTestStackAdapter;

    private List<AccountBean> mAccountBeans;
    private Button bt_search;
    private EditText et_search;
    private TextView tip;
    private SweetAlertDialog pDialog = null;
    private static Boolean isExit = false;
    private BmobUser user = new BmobUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检测menu操作，第二次进入app时是否显示menu
        if (!(Boolean) SPUtils.get("optionMenuOn", true)) {
            optionMenuOn = false;
            checkOptionMenu();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (SPUtils.get("name", "") != "") {
            toolbar.setTitle("");
            toolbar.setSubtitle((String.valueOf(SPUtils.get("name", ""))));
        }
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    if (mStackView.isExpending()) {
                        mStackView.clearSelectPosition();
                        mStackView.removeAllViews();
                    }
                    findDate();
                }
                if (id == R.id.action_share) {
                    DesUtil.share(MainActivity.this, getString(R.string.share_note));
                }
                if (id == R.id.action_delete) {
                    //跳转到关于页面
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        tip = (TextView) findViewById(R.id.tip);
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
                    finish();
                }
            }
        });

        mStackView = (CardStackView) findViewById(R.id.stackview_main);
        mStackView.setItemExpendListener(this);

        if (SPUtils.get("key", "") + "" == "") {
            //            Bmob.initialize(this, "46b1709520ec4d0afa17e505680202da");//正式版
            Bmob.initialize(this, "949a1379183be6d8a655037d1282c146");//测试版
        } else {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
        }
        if (!MyApplication.isSign()) {
            tip.setVisibility(View.VISIBLE);
        } else {
            findDate();
        }
        et_search = (EditText) findViewById(R.id.et_search);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate(et_search.getText().toString().trim());
            }
        });
        final SortByTime sortByTime = new SortByTime();
        RxBus.getInstance().toObserverable(RxBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxBean>() {
                    @Override
                    public void call(RxBean rxBean) {
                        if (rxBean.getMessage() != null) {
                            searchDate(rxBean.getMessage());
                            return;
                        }
                        if (rxBean.getAction() != null) {
                            if (mStackView.isExpending()) {
                                mStackView.clearSelectPosition();
                                mStackView.removeAllViews();
                            }
                            findDateByTime(sortByTime);
                            return;
                        }
                    }
                });
    }


    private void searchDate(String s) {
        //手动清除一次全部view，避免重用时的重合
        if (mStackView.isExpending()) {
            mStackView.clearSelectPosition();
            mStackView.removeAllViews();
        }
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载中");
        if (!MainActivity.this.isFinishing()) {
            pDialog.show();
        }
        BmobQuery<AccountBean> query = new BmobQuery<AccountBean>();
        String[] search = {s};
        query.addWhereContainsAll("tag", Arrays.asList(search));
        query.addWhereEqualTo("user", new BmobPointer(user));
        query.findObjects(new FindListener<AccountBean>() {

            @Override
            public void done(List<AccountBean> object, BmobException e) {
                if (e == null) {
                    mAccountBeans = object;
                    mTestStackAdapter = new TestStackAdapter(MyApplication.getContext(), mAccountBeans);
                    mStackView.setAdapter(mTestStackAdapter);
                    mTestStackAdapter.notifyDataSetChanged();
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                                    mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                                }
                            }
                            , 100
                    );

                } else {
                    MyApplication.showToast("不知道哪里出问题了" + e);
                }
                pDialog.dismiss();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.get("key", "") + "" != "") {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
        }
    }

    private void findDate() {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载中");
        pDialog.show();
        BmobQuery<AccountBean> query = new BmobQuery<>();
        if (MyApplication.getUser() != null) {
            String id = MyApplication.getUser().getObjectId();
            user.setObjectId(id);
            query.addWhereEqualTo("user", new BmobPointer(user));
            query.findObjects(new FindListener<AccountBean>() {

                @Override
                public void done(List<AccountBean> objects, BmobException e) {
                    if (objects != null) {
                        mAccountBeans = objects;
                        if (mAccountBeans.size() < 1) {
                            tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                            tip.setVisibility(View.VISIBLE);
                            pDialog.dismiss();
                            return;
                        }
                        tip.setVisibility(View.GONE);
                        mTestStackAdapter = new TestStackAdapter(MyApplication.getContext(), mAccountBeans);
                        mStackView.setAdapter(mTestStackAdapter);
                        mTestStackAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                                    }
                                }
                                , 100
                        );
                    } else {
                        tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                        tip.setVisibility(View.VISIBLE);
                    }
                    pDialog.dismiss();
                }
            });
        }

    }

    private void findDateByTime(final SortByTime sortByTime) {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载中");
        if (!MainActivity.this.isFinishing()) {
            pDialog.show();
        }
        BmobQuery<AccountBean> query = new BmobQuery<>();
        if (MyApplication.getUser() != null) {
            String id = MyApplication.getUser().getObjectId();
            user.setObjectId(id);
            query.addWhereEqualTo("user", new BmobPointer(user));
            query.findObjects(new FindListener<AccountBean>() {

                @Override
                public void done(List<AccountBean> objects, BmobException e) {
                    if (objects != null) {
                        Collections.sort(objects, Collections.reverseOrder(sortByTime));
                        mAccountBeans = objects;
                        MyApplication.showToast("已按最近更新时间排序");
                        if (mAccountBeans.size() < 1) {
                            tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                            tip.setVisibility(View.VISIBLE);
                            pDialog.dismiss();
                            return;
                        }
                        tip.setVisibility(View.GONE);
                        mTestStackAdapter = new TestStackAdapter(MyApplication.getContext(), mAccountBeans);
                        mStackView.setAdapter(mTestStackAdapter);
                        mTestStackAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                                    }
                                }
                                , 100
                        );
                    } else {
                        tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                        tip.setVisibility(View.VISIBLE);
                    }
                    pDialog.dismiss();
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void onItemExpend(boolean expend) {

    }

    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true;
            // 准备退出
            MyApplication.showToast("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

}
