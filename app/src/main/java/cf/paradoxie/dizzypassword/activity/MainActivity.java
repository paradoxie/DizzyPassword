package cf.paradoxie.dizzypassword.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.TestStackAdapter;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.SortUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import km.lmy.searchview.SearchView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements CardStackView.ItemExpendListener, View.OnClickListener {
    private boolean optionMenuOn = true;  //显示optionmenu
    private Menu aMenu;         //获取optionmenu
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4
            , R.color.color_5, R.color.color_6
            , R.color.color_7, R.color.color_8,
            R.color.color_9, R.color.color_10, R.color.color_11, R.color.color_12,
            R.color.color_13, R.color.color_14, R.color.color_15, R.color.color_16,
            R.color.color_17, R.color.color_18, R.color.color_19, R.color.color_20,
            R.color.color_21, R.color.color_22, R.color.color_23, R.color.color_24,
            R.color.color_25, R.color.color_26
    };

    private CardStackView mStackView;
    private TestStackAdapter mTestStackAdapter;

    private List<AccountBean> mAccountBeans;
    private TextView tip;
    private SweetAlertDialog pDialog = null;
    private static Boolean isExit = false;
    private BmobUser user = new BmobUser();
    public static Toolbar toolbar = null;
    private DialogView mDialogView;
    private LinearLayout main_btn;
    private long mCurrentPlayTime;
    private ObjectAnimator animator;
    private ImageView refresh, red_package, setting, search;
    private SearchView mSearchView;
    private FloatingActionButton fab;
    List<Map.Entry<String, Integer>> mappingList = null;
    List<String> historys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeUtils.initStatusBarColor(MainActivity.this, ThemeUtils.getPrimaryDarkColor(MainActivity.this));

        mSearchView = (SearchView) findViewById(R.id.searchView);
        refresh = (ImageView) findViewById(R.id.refresh);
        red_package = (ImageView) findViewById(R.id.red_package);
        setting = (ImageView) findViewById(R.id.setting);
        search = (ImageView) findViewById(R.id.search);
        refresh.setOnClickListener(this);
        red_package.setOnClickListener(this);
        setting.setOnClickListener(this);
        search.setOnClickListener(this);
        main_btn = (LinearLayout) findViewById(R.id.main_btn);
        main_btn.setVisibility(View.VISIBLE);
        //检测menu操作，第二次进入app时是否显示menu
        if (!(Boolean) SPUtils.get("optionMenuOn", true)) {
            optionMenuOn = false;
            checkOptionMenu();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (SPUtils.get("name", "") != "") {
            if (MyApplication.first_check == 0) {
                toolbar.setNavigationIcon(R.drawable.yep_selector);
            } else {
                toolbar.setNavigationIcon(R.drawable.yep);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.first_check == 0) {
                        checkActivity();
                        mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                            @Override
                            public void posClickListener(String value) {
                                //校验密码
                                if (value.equals(SPUtils.get("password", "") + "")) {
                                    MyApplication.first_check++;
                                    hideInputWindow();
                                    //换图标，解锁
                                    toolbar.setNavigationIcon(R.drawable.yep);
                                    mDialogView.dismiss();
                                } else {
                                    MyApplication.showToast("密码错了哦~");
                                }
                            }

                            @Override
                            public void negCliclListener(String value) {
                                //取消查看
                            }
                        });
                    } else {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("锁定数据操作")
                                .setContentText(
                                        "帐号:" + (String.valueOf(SPUtils.get("name", ""))) + "\n密码:" + (String.valueOf(SPUtils.get("password", ""))) +
                                                "\n\n确定要锁定当前操作权限么？")
                                .setConfirmText("锁定")
                                .setCancelText("算啦")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        MyApplication.first_check = 0;

                                        //换图标：加锁
                                        toolbar.setNavigationIcon(R.drawable.yep_selector);
                                        sDialog.cancel();
                                    }
                                })
                                .show();
                    }
                }
            });
        }

        tip = (TextView) findViewById(R.id.tip);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //初始化搜索数据操作
        searchData();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyApplication.isSign()) {
                    if (MyApplication.first_check == 0) {
                        checkActivity();
                        mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                            @Override
                            public void posClickListener(String value) {
                                //校验密码
                                if (value.equals(SPUtils.get("password", "") + "")) {
                                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                                    startActivity(intent);
                                    MyApplication.first_check++;
                                    hideInputWindow();
                                    mDialogView.dismiss();
                                    toolbar.setNavigationIcon(R.drawable.yep);
                                } else {
                                    MyApplication.showToast("密码错了哦~");
                                }
                            }

                            @Override
                            public void negCliclListener(String value) {
                                //取消查看
                            }
                        });
                    } else {
                        Intent intent = new Intent(MainActivity.this, AddActivity.class);
                        startActivity(intent);
                        //                        finish();
                    }
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
            Bmob.initialize(this, "949a1379183be6d8a655037d1282c146");//测试版
        } else {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
        }
        if (!MyApplication.isSign()) {
            tip.setVisibility(View.VISIBLE);
        } else {
            search.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            //取缓存数据
            if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
                findOnLineDate();
            } else {
                findOffLineDate();
            }

        }

        //获得tag的统计数据
        getTags();

        final SortUtils sortUtils = new SortUtils();
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
                            findDateByTime(sortUtils);
                            return;
                        }
                    }

                });
    }


    private void findOffLineDate() {

        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("离线数据加载中...");
        pDialog.show();
        mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);

        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                        pDialog.dismiss();
                    }
                }
                , 1500
        );
    }

    private void checkActivity() {
        mDialogView = new DialogView(MainActivity.this);
        mDialogView.setAccount(SPUtils.get("name", "") + "");
        try {
            if (!MainActivity.this.isFinishing()) {
                mDialogView.show();
                hideInputWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchDate(String s) {
        //手动清除一次全部view，避免重用时的重合
        if (mStackView.isExpending()) {
            mStackView.clearSelectPosition();
            mStackView.removeAllViews();
        }
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("查找中...");
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
                if (object.size() != 0) {
                    mAccountBeans = object;
                    mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
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

    private void findOnLineDate() {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("联网加载中...");
        pDialog.show();
        startAnim();
        BmobQuery<AccountBean> query = new BmobQuery<>();
        if (MyApplication.getUser() != null) {
            String id = MyApplication.getUser().getObjectId();
            user.setObjectId(id);
            query.addWhereEqualTo("user", new BmobPointer(user));
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存获取数据，如果没有，再从网络获取。
            query.findObjects(new FindListener<AccountBean>() {

                @Override
                public void done(List<AccountBean> objects, BmobException e) {
                    if (objects != null) {
                        mAccountBeans = objects;
                        if (mAccountBeans.size() < 1) {
                            if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
                                tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                                tip.setVisibility(View.VISIBLE);
                                pDialog.dismiss();
                                stopAnim(animator);
                                return;
                            }
                        }
                        tip.setVisibility(View.GONE);
                        //缓存
                        SPUtils.setDataList("beans", mAccountBeans);
                        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
                        mStackView.setAdapter(mTestStackAdapter);
                        mTestStackAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                                        // hideAnimate();
                                        stopAnim(animator);
                                    }
                                }
                                , 100
                        );
                    } else {
                        if (e.getErrorCode() == 9016 && SPUtils.getDataList("beans", AccountBean.class).size() > 1) {
                            MyApplication.showToast("网络好像不可以哦~");
                            stopAnim(animator);
                            pDialog.dismiss();
                            return;
                        } else {
                            tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                            tip.setVisibility(View.VISIBLE);
                        }
                    }
                    pDialog.dismiss();

                }
            });
        }

    }

    private void findDateByTime(final SortUtils sortUtils) {
        try {//233，这个地方单独判断isFinish还是会崩，得再捕捉一次
            if (!MainActivity.this.isFinishing()) {
                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("加载中");
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        Collections.sort(objects, Collections.reverseOrder(sortUtils));
                        mAccountBeans = objects;
                        MyToast.show(MainActivity.this, "已按最近更新时间排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

                        if (mAccountBeans.size() < 1) {
                            tip.setText("好像还没有记录什么帐号信息，点击右下角添加吧(*^__^*)");
                            tip.setVisibility(View.VISIBLE);
                            pDialog.dismiss();
                            return;
                        }
                        tip.setVisibility(View.GONE);
                        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
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
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                }
            });
        }


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

    /**
     * 隐藏软键盘
     */
    private void hideInputWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                mSearchView.setNewHistoryList(getHistory());
                mSearchView.autoOpenOrClose();
                fab.setVisibility(View.GONE);
                break;

            case R.id.refresh:
                if (MyApplication.isSign()) {
                    if (mStackView.isExpending()) {
                        mStackView.clearSelectPosition();
                        mStackView.removeAllViews();
                    }
                    findOnLineDate();

                } else {
                    MyApplication.showToast("您还木有登录哦~");

                }
                break;
            case R.id.red_package:
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("口令复制成功")
                        .setContentText("支付宝红包，金额随机，最高￥99喔😃\n" +
                                "\n每天都可以来领取一次哈\n话说最近的红包好像都变大了呢...\n")
                        .setConfirmText("前往支付宝领取")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                ClipboardManager cm = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setText(getString(R.string.red_package_string_little));
                                try {
                                    MyApplication.openAppByPackageName(MainActivity.this, "com.eg.android.AlipayGphone");
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                sDialog.cancel();
                            }
                        })
                        .show();
                break;
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void startAnim() {
        animator = ObjectAnimator.ofFloat(refresh, "rotation", 0f, 360.0f);
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        startAnimation(animator);
    }

    private void stopAnim(ObjectAnimator mRotateAntiClockwiseAnimator) {
        mCurrentPlayTime = mRotateAntiClockwiseAnimator.getCurrentPlayTime();
        mRotateAntiClockwiseAnimator.cancel();
    }

    private void startAnimation(ObjectAnimator mRotateAntiClockwiseAnimator) {
        mRotateAntiClockwiseAnimator.start();
        mRotateAntiClockwiseAnimator.setCurrentPlayTime(mCurrentPlayTime);
    }


    private void searchData() {
        //设置历史记录点击事件
        mSearchView.setHistoryItemClickListener(new SearchView.OnHistoryItemClickListener() {
            @Override
            public void onClick(String historyStr, int position) {
                mSearchView.getEditTextView().setText(historyStr);
            }
        });

        mSearchView.getBackIV().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchView.close();
                fab.setVisibility(View.VISIBLE);
            }
        });
        mSearchView.setNewHistoryList(getHistory());
        mSearchView.setOnCleanHistoryClickListener(new SearchView.OnCleanHistoryClickListener() {
            @Override
            public void onClick() {
                SPUtils.remove("historyLists");
                MyApplication.showToast("搜索记录删除成功");
            }
        });
        //设置软键盘搜索按钮点击事件
        mSearchView.setOnSearchActionListener(new SearchView.OnSearchActionListener() {
            @Override
            public void onSearchAction(String searchText) {
                addHistory(searchText);//历史记录存入sp
                if (searchText.contains("(")) {
                    String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                    searchText = searchText.replace(str, "");
                    searchDate(searchText);
                    mSearchView.close();
                    fab.setVisibility(View.VISIBLE);
                    return;
                }
                if (mStackView.isExpending()) {
                    mStackView.clearSelectPosition();
                    mStackView.removeAllViews();
                }
                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("搜索中...");
                pDialog.show();
                String name = DesUtil.encrypt(searchText.trim(), SPUtils.getKey());
                String id = MyApplication.getUser().getObjectId();
                user.setObjectId(id);
                BmobQuery<AccountBean> query = new BmobQuery<>();
                query.addWhereEqualTo("name", name);
                query.addWhereEqualTo("user", new BmobPointer(user));
                //                boolean isCache = query.hasCachedResult(AccountBean.class);
                //                if (isCache) {
                //                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
                //                } else {
                //                    query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
                //                }
                query.findObjects(new FindListener<AccountBean>() {

                    @Override
                    public void done(List<AccountBean> object, BmobException e) {
                        if (e == null) {
                            if (object.size() != 0) {
                                mAccountBeans = object;
                                mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
                                mStackView.setAdapter(mTestStackAdapter);
                                mTestStackAdapter.notifyDataSetChanged();
                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                                            }
                                        }
                                        , 100
                                );
                            } else {
                                MyApplication.showToast("好像没有叫这个名字的条目哦~试试Tag看看");
                            }
                        } else {
                            MyApplication.showToast("发生了什么~(⊙ˍ⊙)" + e.getMessage());
                        }
                        pDialog.dismiss();
                    }

                });
                mSearchView.close();
                fab.setVisibility(View.VISIBLE);
                //                mSearchView.addOneHistory(searchText);
            }
        });


    }

    private void getTags() {
        final List<String> s = new ArrayList<>();

        BmobQuery<AccountBean> bmobQuery = new BmobQuery<>();
        String id = MyApplication.getUser().getObjectId();
        user.setObjectId(id);
        bmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        bmobQuery.addQueryKeys("tag");
        bmobQuery.findObjects(new FindListener<AccountBean>() {
            @Override
            public void done(List<AccountBean> object, BmobException e) {
                if (e == null) {
                    Log.i("bmob", "查询成功：共" + object.size() + "条数据。");
                    //获取所有的tag
                    for (int j = 0; j < object.size(); j++) {
                        s.addAll(object.get(j).getTag());
                    }
                    List<Map.Entry<String, Integer>> tags;
                    List<Map.Entry<String, Integer>> tags_name;
                    tags = getTagList(s);
                    tags_name = getTagListByName(s);
                    String[] strings = new String[tags.size()];
                    String[] strings_name = new String[tags_name.size()];
                    for (int i = 0; i < tags.size(); i++) {
                        strings[i] = tags.get(i).getKey() + "(" + tags.get(i).getValue() + ")";
                    }
                    for (int i = 0; i < tags_name.size(); i++) {
                        strings_name[i] = tags_name.get(i).getKey() + "(" + tags_name.get(i).getValue() + ")";
                    }
                    mSearchView.initFlowView(strings);
                    mSearchView.initFlowViewByName(strings_name);

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 获得频率最高的标签及重复次数
     *
     * @param s 传入的tag数组
     * @return map
     */
    private ArrayList<Map.Entry<String, Integer>> getTagList(List s) {
        //        Map<String, Integer> tagMap = new HashMap<>();
        Map map = new HashMap();
        for (Object temp : s) {
            Integer count = (Integer) map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }

        mappingList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //通过比较器实现比较排序
        Collections.sort(mappingList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> mapping1, Map.Entry<String, Integer> mapping2) {
                return mapping2.getValue().compareTo(mapping1.getValue());
            }
        });
        return (ArrayList<Map.Entry<String, Integer>>) mappingList;
    }

    /**
     * 获得频率最高的标签及重复次数
     * 按名称排序
     *
     * @param s 传入的tag数组
     * @return map
     */
    private ArrayList<Map.Entry<String, Integer>> getTagListByName(List s) {
        Map map = new HashMap();
        for (Object temp : s) {
            Integer count = (Integer) map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }

        mappingList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        //通过比较器实现比较排序
        Collections.sort(mappingList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> mapping1, Map.Entry<String, Integer> mapping2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(mapping1.getKey(), mapping2.getKey());
            }
        });

        return (ArrayList<Map.Entry<String, Integer>>) mappingList;
    }

    private void addHistory(String value) {
        String historyLists = (String) SPUtils.get("historyLists", "");
        if (historyLists == "") {
            SPUtils.put("historyLists", " " + value);
        } else {
            historyLists = value + " " + historyLists.trim();
            SPUtils.put("historyLists", historyLists);
        }
    }

    private List<String> getHistory() {
        String strings = ((String) SPUtils.get("historyLists", "")).trim();
        historys = java.util.Arrays.asList(strings.split(" "));
        return historys;
    }

}
