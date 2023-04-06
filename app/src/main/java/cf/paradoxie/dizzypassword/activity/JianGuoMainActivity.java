package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.loopeer.cardstack.CardStackView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.NameAdapter;
import cf.paradoxie.dizzypassword.adapter.TestStackAdapter;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.AppConfig;
import cf.paradoxie.dizzypassword.bean.BaseConfig;
import cf.paradoxie.dizzypassword.bean.RxBean;
import cf.paradoxie.dizzypassword.data.DataDeal;
import cf.paradoxie.dizzypassword.utils.AvatarUtil;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.SortUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cf.paradoxie.dizzypassword.view.MyDialog;
import cf.paradoxie.dizzypassword.view.MyLetterSortView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import km.lmy.searchview.SearchView;

@SuppressLint("RestrictedApi")
public class JianGuoMainActivity extends BaseActivity implements CardStackView.ItemExpendListener, View.OnClickListener {
    private boolean optionMenuOn = true;
    private Menu aMenu;
    public Toolbar toolbar = null;
    private DialogView mDialogView;
    private Boolean isExit = false;
    private CardStackView mStackView;
    private TestStackAdapter mTestStackAdapter;
    private List<AccountBean> mAccountBeans;
    private BaseConfig config;
    private TextView tip, tv_words, tv_words_chicken;
    private ImageView refresh, iv_user_photo;
    private final Handler handler = new Handler();
    private SearchView mSearchView;
    private String[] strings, strings_name;
    private FloatingActionButton fab, fab_1, fab_2;
    private SortUtils mSortUtils;
    private DrawerLayout mDrawerLayout;
    private ListView mListNames, mListTimes;
    private MyLetterSortView left_letter;
    private String url;
    private NameAdapter nameAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);


        ThemeUtils.initStatusBarColor(JianGuoMainActivity.this, ThemeUtils.getPrimaryDarkColor(JianGuoMainActivity.this));


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("全部");
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        TextView tv_name = headerView.findViewById(R.id.tv_name);
        tv_words = headerView.findViewById(R.id.tv_words);
        tv_words.setOnClickListener(v -> {
            Utils.copy(JianGuoMainActivity.this, v, tv_words.getText());
        });
        tv_words_chicken = navigationView.findViewById(R.id.tv_words_chicken);
        tv_words_chicken.setOnClickListener(v -> {
            Utils.copy(JianGuoMainActivity.this, v, tv_words_chicken.getText());
        });
        tv_words_chicken.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(JianGuoMainActivity.this));
        mListNames = navigationView.findViewById(R.id.list_names);
        left_letter = navigationView.findViewById(R.id.left_letter);
        mListTimes = navigationView.findViewById(R.id.list_times);
        TextView mTvDialog = findViewById(R.id.tv_dialog);
        left_letter.setTvDialog(mTvDialog);
        mListNames.setOnItemClickListener((parent, view, position, id) -> {
            String searchText = strings_name[position];
            toggleRightSliding();
            while (searchText.contains("(")) {
                String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                searchText = searchText.replace(str, "");
            }
            searchDate(searchText.trim());
            toolbar.setTitle(searchText.trim());
        });


        mListTimes.setOnItemClickListener((parent, view, position, id) -> {
            String searchText = strings[position];
            toggleRightSliding();
            while (searchText.contains("(")) {
                String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                searchText = searchText.replace(str, "");
            }
            searchDate(searchText.trim());
            toolbar.setTitle(searchText.trim());
        });

        headerView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(JianGuoMainActivity.this));

        mSearchView = findViewById(R.id.searchView);
        refresh = findViewById(R.id.refresh);

        config = DataDeal.getInstance(JianGuoMainActivity.this).getConfig();
        if (config == null) {
            config = new BaseConfig(Utils.GetVersion() + "",
                    "震惊！新版！GKD！！",
                    "好像有新版本发布了哦~快去醋安看看吧！",
                    "你等着！",
                    "com.eg.android.AlipayGphone",
                    "支付宝发生活费啦，金额最高￥99😃\n现在玩的人不多，赶紧去支付宝粘贴搜索叭\n",
                    "生活费85193",
                    "复制成功,去支付宝吧",
                    "前往搜索",
                    "",
                    false);
        }

        ImageView setting = findViewById(R.id.setting);
        ImageView search = findViewById(R.id.search);
        ImageView sort = findViewById(R.id.sort);
        refresh.setOnClickListener(this);
        setting.setOnClickListener(this);

        search.setOnClickListener(this);
        sort.setOnClickListener(this);
        //长按按名称排序
        sort.setOnLongClickListener(view -> {
            MyToast.show(JianGuoMainActivity.this, "长按，已按条目名称排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

            if (toolbar.getTitle().toString().equals("全部")) {
                mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
            }
            findOffLineDateByName(mAccountBeans);
            return false;
        });
        LinearLayout main_btn = findViewById(R.id.main_btn);
        main_btn.setVisibility(View.VISIBLE);
        //检测menu操作，第二次进入app时是否显示menu
        if (!(Boolean) SPUtils.get("optionMenuOn", true)) {
            optionMenuOn = false;
            checkOptionMenu();
        }

        fab_1 = findViewById(R.id.fab_1);
        fab_2 = findViewById(R.id.fab_2);
        fab_2.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.navigation);
        toolbar.setNavigationOnClickListener(v -> toggleRightSliding());
        if (SPUtils.get("name", "") != "") {
            if (MyApplication.first_check == 0) {
                fab_1.setImageResource(R.drawable.yep_selector);
            } else {
                fab_1.setImageResource(R.drawable.yep);
            }

            fab_1.setOnClickListener(view -> {
                if (MyApplication.first_check == 0) {
                    checkActivity();
                    mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                        @Override
                        public void posClickListener(String value) {
                            //校验密码
                            if (value.equals(SPUtils.get("password", "") + "")) {
                                MyApplication.first_check = 1;
                                hideInputWindow();
                                fab_1.setImageResource(R.drawable.yep);
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
                } else {
                    new SweetAlertDialog(JianGuoMainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("锁定数据操作")
                            .setContentText(
                                    "帐号:" + (SPUtils.get("name", "")) + "\n密码:" + (SPUtils.get("password", "")) +
                                            "\n\n确定要锁定当前操作权限么？")
                            .setConfirmText("锁定")
                            .setCancelText("算啦")
                            .setConfirmClickListener(sDialog -> {
                                MyApplication.first_check = 0;
                                fab_1.setImageResource(R.drawable.yep_selector);
                                sDialog.cancel();
                            })
                            .show();
                }
            });
        }

        tip = findViewById(R.id.tip);

        tv_name.setText(SPUtils.get("name", "点击去登录") + "");
        fab = findViewById(R.id.fab);

        //初始化搜索数据操作
        searchData();

        fab.setOnClickListener(view -> {

            if (MyApplication.isSign()) {
//                if (!MyApplication.isNetworkAvailable(JianGuoMainActivity.this)) {
//                    MyApplication.showToast(R.string.error_offline);
//                    return;
//                }
                //不允许修改，直接返回
//                if (!MyApplication.allowChange()) return;
//                if (MyApplication.first_check == 0) {
//                    checkActivity();
//                    mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
//                        @Override
//                        public void posClickListener(String value) {
//                            //校验密码
//                            if (value.equals(SPUtils.get("password", "") + "")) {
//                                Intent intent = new Intent(JianGuoMainActivity.this, AddActivity.class);
//                                startActivity(intent);
//                                MyApplication.first_check = 1;
//                                hideInputWindow();
//                                mDialogView.dismiss();
//                                fab_1.setImageResource(R.drawable.yep);
//                            } else {
//                                MyApplication.showToast(R.string.error_pwd);
//                            }
//                        }
//
//                        @Override
//                        public void negCliclListener(String value) {
//                            //取消查看
//                        }
//                    });
//                } else {
//                    Intent intent = new Intent(JianGuoMainActivity.this, AddActivity.class);
//                    startActivity(intent);
//                }

                Intent intent = new Intent(JianGuoMainActivity.this, AddActivity.class);
                startActivity(intent);
            } else {
                //缓存用户对象为空时， 可打开用户注册界面…
                Intent intent = new Intent(JianGuoMainActivity.this, SignActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mStackView = findViewById(R.id.stackview_main);
        mStackView.setItemExpendListener(this);


        if (!MyApplication.isSign()) {
            tip.setVisibility(View.VISIBLE);
            fab_1.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.login);
            tv_name.setOnClickListener(v -> {
                //缓存用户对象为空时， 可打开用户注册界面…
                Intent intent = new Intent(JianGuoMainActivity.this, SignActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            toolbar.setTitle("全部");
            fab.setImageResource(R.drawable.add);
            fab_1.setVisibility(View.VISIBLE);
            fab_2.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
//            refresh.setVisibility(View.VISIBLE);
            sort.setVisibility(View.VISIBLE);
        }


        List<AccountBean> beans = SPUtils.getDataList("beans", AccountBean.class);
        if (beans.size() < 1) {
            //获取在线数据
            findOnLineDate();
        } else {
            Log.e("读取缓存数据", beans.size() + "条");
            findOffLineDate();
            //获得tag的统计数据
            new Thread() {
                public void run() {
                    getTags();
                }
            }.start();
        }

        mSortUtils = new SortUtils();
        RxBus.getInstance().register(RxBean.class, rxBean -> {
            if (rxBean.getMessage() != null) {
                //按tag检索
                searchDate(rxBean.getMessage());
                return;
            }
            if (rxBean.getAction() != null) {
                if (rxBean.getAction().equals("done")) {
                    //点击新建/更新时间排序
                    closeStackView();
                } else if (rxBean.getAction().equals("refresh")) {
//                    if (MyApplication.isSign()) {
//                        closeStackView();
//                        findOffLineDate();
//                        toolbar.setTitle("全部");
//                    } else {
                    MyApplication.showToast("本地数据有更新，请点击右上角刷新");
//                    }
                }
            }
        });
        iv_user_photo = findViewById(R.id.iv_user_photo);
        AppConfig appConfig = DataDeal.getInstance(this).getAppConfig();
        if (appConfig != null) {
            Utils.loadImg(iv_user_photo, appConfig.getAvatarUrl(), true);
            tv_words.setText(appConfig.getMaxim());
            tv_words_chicken.setText(appConfig.getWitty());
        }

    }

    //收起StackView
    private void closeStackView() {
        if (mStackView.isExpending()) {
            mStackView.setSelectPosition(-1);
            mStackView.setScrollEnable(true);
        }
    }

    //点击头像
    public void smallImgClick(View v) {
        AvatarUtil.getInstance(JianGuoMainActivity.this).showImage(url);
    }

    //该方法控制右侧边栏的显示和隐藏
    private void toggleRightSliding() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);//关闭抽屉
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);//打开抽屉
        }
    }


    private void findOffLineDate() {

        closeStackView();

        MyDialog.getInstance().show(this, "离线数据加载中...");
        mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);

        mTestStackAdapter = new TestStackAdapter(JianGuoMainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(mAccountBeans.size())));
            MyDialog.getInstance().dismiss();
        }, 1500);
    }

    private void checkActivity() {
        mDialogView = new DialogView(JianGuoMainActivity.this);
        mDialogView.setAccount(SPUtils.get("name", "") + "");
        try {
            if (!JianGuoMainActivity.this.isFinishing()) {
                mDialogView.show();
                hideInputWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用tag检索
     *
     * @param s 搜索
     */

    private void searchDate(String s) {
        //手动清除一次全部view，避免重用时的重合
        closeStackView();
        if (!JianGuoMainActivity.this.isFinishing()) {
            MyDialog.getInstance().show(this, "查找中...");
        }

        toolbar.setTitle(s);
        mAccountBeans = DataUtils.searchDataByTag(mAccountBeans, s);

        if (mAccountBeans.size() == 0) {
            MyApplication.showToast(R.string.error_no_item);
            MyDialog.getInstance().dismiss();
            return;
        }
        mTestStackAdapter = new TestStackAdapter(JianGuoMainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(mAccountBeans.size())));
            MyDialog.getInstance().dismiss();
        }, 1000);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        if (MyApplication.first_check == 2) {//校验后台时长
            fab_1.setImageResource(R.drawable.yep_selector);
            MyApplication.first_check = 0;
            //取缓存数据
            if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
//                findOnLineDate();
            } else {
                findOffLineDate();
            }
            toolbar.setTitle("全部");
        }
    }

    //弹窗去设置坚果云
    private void findOnLineDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String pwd = SPUtils.get("jianguo_pwd", "") + "";
        if (TextUtils.isEmpty(pwd)) {
            builder.setTitle("去配置坚果云");
        } else {
            builder.setTitle("去恢复坚果云数据");
        }
        //设置对话框标题
        builder.setIcon(R.drawable.jian_0);   //设置对话框标题前的图标

        builder.setPositiveButton("确认", null);
        AlertDialog mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positionButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positionButton.setOnClickListener(v -> {
                    mDialog.dismiss();
                    Intent intent3 = new Intent(JianGuoMainActivity.this, JianGuoActivity.class);
                    startActivity(intent3);
                });
                negativeButton.setOnClickListener(v -> {
                    MyApplication.showToast("取消");
                    mDialog.dismiss();
                });
            }
        });
        mDialog.show();
    }

    //按名称排序
    private void findOffLineDateByName(List<AccountBean> mAccountBeans) {

        if (!isFinishing()) {
            MyDialog.getInstance().show(this, "按名称排序中...");
        }

        closeStackView();

        mAccountBeans = DataUtils.getDataByName(mAccountBeans);
        mTestStackAdapter = new TestStackAdapter(JianGuoMainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);

        mTestStackAdapter.notifyDataSetChanged();
        final List<AccountBean> finalMAccountBeans = mAccountBeans;
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(finalMAccountBeans.size())));
            MyDialog.getInstance().dismiss();
        }, 1000);
    }


    //按最近更新时间排序
    private void findDateByTime(final SortUtils sortUtils, final List<AccountBean> mAccountBeans) {
        try {
            if (!JianGuoMainActivity.this.isFinishing()) {
                MyDialog.getInstance().show(this, "加载中...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(mAccountBeans, Collections.reverseOrder(sortUtils));
        MyToast.show(JianGuoMainActivity.this, "短按，已按最近更新时间排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

        mTestStackAdapter = new TestStackAdapter(JianGuoMainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(mAccountBeans.size())));
            MyDialog.getInstance().dismiss();

        }, 1000);

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //短按按时间排序
            case R.id.sort:
                //点击新建/更新时间排序
                closeStackView();
                if (toolbar.getTitle().toString().equals("全部")) {
                    mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
                }
                findDateByTime(mSortUtils, mAccountBeans);
                break;
            case R.id.search:
                mSearchView.setNewHistoryList(DataDeal.getInstance(JianGuoMainActivity.this).getHistory());
                mSearchView.autoOpenOrClose();
                fab.setVisibility(View.GONE);
                fab_1.setVisibility(View.GONE);
                break;

            case R.id.refresh:
                if (MyApplication.isSign()) {
                    closeStackView();
                    findOffLineDate();
                    toolbar.setTitle("全部");
                } else {
                    MyApplication.showToast(getString(R.string.error_login));
                }
                break;
            case R.id.fab_2:
                gotoActivity(EatRiceActivity.class);
                break;
            case R.id.setting:
                Intent i = new Intent(JianGuoMainActivity.this, SettingActivity.class);
                if (config.getWindowCopyContent() != null || !config.getWindowCopyContent().equals("")) {
                    i.putExtra("copyCode", config.getWindowCopyContent());
                    Log.d("23333", config.getWindowCopyContent());
                }
                startActivity(i);
                break;
            default:
                break;
        }
    }

    //搜索组件
    private void searchData() {
        //设置历史记录点击事件
        mSearchView.setHistoryItemClickListener((historyStr, position) -> mSearchView.getEditTextView().setText(historyStr));

        mSearchView.getBackIV().setOnClickListener(view -> {
            closeSearchView();
        });
        mSearchView.getCloseTv().setOnClickListener(view -> {
            closeSearchView();
        });
        mSearchView.setNewHistoryList(DataDeal.getInstance(JianGuoMainActivity.this).getHistory());
        mSearchView.setOnCleanHistoryClickListener(() -> {
            SPUtils.remove("historyLists");
            MyApplication.showToast(getString(R.string.succes_delete));
        });
        //设置软键盘搜索按钮点击事件
        mSearchView.setOnSearchActionListener(searchText -> {
            DataDeal.getInstance(JianGuoMainActivity.this).addHistory(searchText);//历史记录存入sp
            if (searchText.contains("(")) {
                while (searchText.contains("(")) {
                    String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                    searchText = searchText.replace(str, "");
                }
                searchDate(searchText.trim());
                closeSearchView();
                return;
            }
            closeStackView();
            MyDialog.getInstance().show(this, "搜索中...");
            toolbar.setTitle(searchText.trim());
            mAccountBeans = DataUtils.searchDataByName(mAccountBeans, searchText.trim());
            if (mAccountBeans.size() == 0) {
                MyApplication.showToast(getString(R.string.no_item_name));
                MyDialog.getInstance().dismiss();
                return;
            }
            mTestStackAdapter = new TestStackAdapter(JianGuoMainActivity.this, mAccountBeans);
            mStackView.setAdapter(mTestStackAdapter);
            mTestStackAdapter.notifyDataSetChanged();
            new Handler().postDelayed(() -> {
                //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(mAccountBeans.size())));
                MyDialog.getInstance().dismiss();
            }, 1000);

            closeSearchView();
        });


    }

    //关闭搜索view
    private void closeSearchView() {
        mSearchView.close();
        fab.setVisibility(View.VISIBLE);
        fab_1.setVisibility(View.VISIBLE);
    }

    //标签
    private void getTags() {
        final List<String> s = new ArrayList<>();
        mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
        //获取所有的tag
        for (int j = 0; j < mAccountBeans.size(); j++) {
            s.addAll(mAccountBeans.get(j).getTag());
        }
        List<Map.Entry<String, Integer>> tags;
        List<Map.Entry<String, Integer>> tags_name;

        tags = DataUtils.getTagList(s);

        tags_name = DataUtils.getTagListByName(s);
        strings = new String[tags.size()];
        strings_name = new String[tags_name.size()];
        for (int i = 0; i < tags.size(); i++) {
            strings[i] = tags.get(i).getKey() + "(" + tags.get(i).getValue() + ")";
        }
        for (int i = 0; i < tags_name.size(); i++) {
            strings_name[i] = tags_name.get(i).getKey() + "(" + tags_name.get(i).getValue() + ")";
        }

        //更新搜索框内的标签
        new Thread() {
            @Override
            public void run() {
                handler.post(() -> {
                    mSearchView.initFlowView(strings);
                    mSearchView.initFlowViewByName(strings_name);
                    nameAdapter = new NameAdapter(JianGuoMainActivity.this, DataDeal.getInstance(JianGuoMainActivity.this).fillData(strings_name));

                    left_letter.setOnTouchLetterChangeListener(s1 -> {
                        // 该字母首次出现的位置
                        int position = nameAdapter.getPositionForSection(s1.charAt(0));
                        if (position != -1) {
                            mListNames.setSelection(position);
                        }
                        Log.d("点击", position + "");
                    });

                    mListNames.setAdapter(nameAdapter);
                    ArrayAdapter<String> adapterTimes = new ArrayAdapter<>(JianGuoMainActivity.this, android.R.layout.simple_list_item_1, strings);
                    mListTimes.setAdapter(adapterTimes);
                });
            }
        }.start();

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
        Timer tExit;
        if (!isExit) {
            isExit = true;
            // 准备退出
            MyApplication.showToast(getString(R.string.exit_app));
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


    @Override
    public void finish() {
        super.finish();
        RxBus.getInstance().unSubscribe(this);
    }


}
