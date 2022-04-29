package cf.paradoxie.dizzypassword.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.Constans;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.NameAdapter;
import cf.paradoxie.dizzypassword.adapter.TestStackAdapter;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.BaseConfig;
import cf.paradoxie.dizzypassword.bean.RxBean;
import cf.paradoxie.dizzypassword.data.DataDeal;
import cf.paradoxie.dizzypassword.utils.AnimationUtil;
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
import cf.paradoxie.dizzypassword.view.MyLetterSortView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import km.lmy.searchview.SearchView;

@SuppressLint("RestrictedApi")
public class MainActivity extends BaseActivity implements CardStackView.ItemExpendListener, View.OnClickListener {
    private boolean optionMenuOn = true;  //显示optionmenu
    private Menu aMenu;         //获取optionmenu
    public static Toolbar toolbar = null;
    private SweetAlertDialog pDialog = null;
    private DialogView mDialogView;
    private static Boolean isExit = false;
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6, R.color.color_7, R.color.color_8, R.color.color_9, R.color.color_10,
            R.color.color_11, R.color.color_12, R.color.color_13, R.color.color_14, R.color.color_15, R.color.color_16, R.color.color_17, R.color.color_18, R.color.color_19, R.color.color_20,
            R.color.color_21, R.color.color_22, R.color.color_23, R.color.color_24, R.color.color_25, R.color.color_26, R.color.color_27, R.color.color_28, R.color.color_29, R.color.color_30,
            R.color.color_31, R.color.color_32, R.color.color_33, R.color.color_34, R.color.color_35, R.color.color_36, R.color.color_37, R.color.color_38, R.color.color_39, R.color.color_40,
            R.color.color_41, R.color.color_42, R.color.color_43, R.color.color_44, R.color.color_45, R.color.color_46, R.color.color_47, R.color.color_48, R.color.color_49, R.color.color_50,
            R.color.color_51, R.color.color_52, R.color.color_53, R.color.color_54, R.color.color_55, R.color.color_56, R.color.color_57, R.color.color_58, R.color.color_59, R.color.color_60,
            R.color.color_61, R.color.color_62, R.color.color_63, R.color.color_64, R.color.color_65, R.color.color_66, R.color.color_67, R.color.color_68, R.color.color_69, R.color.color_70,
            R.color.color_71, R.color.color_72, R.color.color_73
    };

    private CardStackView mStackView;
    private TestStackAdapter mTestStackAdapter;

    private List<AccountBean> mAccountBeans;
    private BaseConfig config;

    private TextView tip, tv_name, tv_words, tv_words_chicken;

    private BmobUser user = new BmobUser();
    private LinearLayout main_btn;
    private ImageView refresh, setting, search, sort, iv_user_photo;
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private String[] strings;
    private String[] strings_name;
    private FloatingActionButton fab, fab_1, fab_2;
    private SortUtils mSortUtils;
    private DrawerLayout mDrawerLayout;
    private ListView mListNames, mListTimes;
    private MyLetterSortView left_letter;
    private String url;
    private NameAdapter nameAdapter;
    private TextView mTvDialog;


    @SuppressLint("HandlerLeak")
    final Handler handler1 = new Handler() {
        public void handleMessage(Message msg) throws IllegalStateException {
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    tv_words_chicken.setText(str);
                    SPUtils.put("text_chicken", str);
                    break;

                case 2:
                    String url = (String) msg.obj;
                    iv_user_photo = findViewById(R.id.iv_user_photo);
                    Utils.loadImg(iv_user_photo, url, true);
                    break;

                case 3:
                    String text = (String) msg.obj;
                    tv_words.setText(text);
                    SPUtils.put("text", text);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ThemeUtils.initStatusBarColor(MainActivity.this, ThemeUtils.getPrimaryDarkColor(MainActivity.this));


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("全部");
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        tv_name = headerView.findViewById(R.id.tv_name);
        tv_words = headerView.findViewById(R.id.tv_words);
        tv_words.setOnClickListener(v -> {
            Utils.copy(MainActivity.this, v, tv_words.getText());
        });
        tv_words_chicken = navigationView.findViewById(R.id.tv_words_chicken);
        tv_words_chicken.setOnClickListener(v -> {
            Utils.copy(MainActivity.this, v, tv_words_chicken.getText());
        });
        tv_words_chicken.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        mListNames = navigationView.findViewById(R.id.list_names);
        left_letter = navigationView.findViewById(R.id.left_letter);
        mListTimes = navigationView.findViewById(R.id.list_times);
        mTvDialog = findViewById(R.id.tv_dialog);
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

        headerView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));

        mSearchView = findViewById(R.id.searchView);
        refresh = findViewById(R.id.refresh);

        config = DataDeal.getInstance(MainActivity.this).getConfig();
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

        setting = findViewById(R.id.setting);
        search = findViewById(R.id.search);
        sort = findViewById(R.id.sort);
        refresh.setOnClickListener(this);
        setting.setOnClickListener(this);
        search.setOnClickListener(this);
        sort.setOnClickListener(this);
        //长按按名称排序
        sort.setOnLongClickListener(view -> {
            MyToast.show(MainActivity.this, "长按，已按条目名称排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

            if (toolbar.getTitle().toString().equals("全部")) {
                mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
            }
            findOffLineDateByName(mAccountBeans);
            return false;
        });
        main_btn = findViewById(R.id.main_btn);
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
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                if (!MyApplication.isNetworkAvailable(MainActivity.this)) {
                    MyApplication.showToast(R.string.error_offline);
                    return;
                }
                if (MyApplication.first_check == 0) {
                    checkActivity();
                    mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                        @Override
                        public void posClickListener(String value) {
                            //校验密码
                            if (value.equals(SPUtils.get("password", "") + "")) {
                                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                                startActivity(intent);
                                MyApplication.first_check = 1;
                                hideInputWindow();
                                mDialogView.dismiss();
                                fab_1.setImageResource(R.drawable.yep);
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
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                }
            } else {
                //缓存用户对象为空时， 可打开用户注册界面…
                Intent intent = new Intent(MainActivity.this, SignActivity.class);
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
                Intent intent = new Intent(MainActivity.this, SignActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            toolbar.setTitle("全部");
            fab.setImageResource(R.drawable.add);
            fab_1.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            sort.setVisibility(View.VISIBLE);
        }

        //取缓存数据
        if (BmobUser.getCurrentUser(BmobUser.class) == null) {
            return;
        }
        if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
            findOnLineDate();
        } else {
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
                if (rxBean.getAction() == "done") {
                    //点击新建/更新时间排序
                    closeStackView();
                }
                return;
            }
        });

        if (MyApplication.isNetworkAvailable(MainActivity.this)) {
            //有网的时候判断版本信息
            handler.postDelayed(() -> {
                DataDeal.getInstance(MainActivity.this).getVersion(fab_2);
                DataDeal.getInstance(MainActivity.this).getWords(handler1);
                DataDeal.getInstance(MainActivity.this).getWordsChicken(handler1);

                String isHeadImage = SPUtils.get("isHeadImage", "false") + "";
                if (isHeadImage.equals("true")) {
                    if (iv_user_photo == null) {
                        iv_user_photo = findViewById(R.id.iv_user_photo);
                    }
                    url = Constans.getUrl();
                    Log.d("----pic", url);
                    Utils.loadImg(iv_user_photo, url, true);
                }
            }, 3000);

        } else {
            tv_words.setText(SPUtils.get("text", "世上无难事，只要肯放弃") + "");
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
        AvatarUtil.getInstance(MainActivity.this).showImage(url);

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

        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("离线数据加载中...");
        pDialog.show();
        mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);

        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
            pDialog.dismiss();
        }, 1500);
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

    /**
     * 使用tag检索
     *
     * @param s
     */

    private void searchDate(String s) {
        //手动清除一次全部view，避免重用时的重合
        closeStackView();
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("查找中...");
        if (!MainActivity.this.isFinishing()) {
            pDialog.show();
        }

        toolbar.setTitle(s);
        mAccountBeans = DataUtils.searchDataByTag(mAccountBeans, s);

        if (mAccountBeans.size() == 0) {
            MyApplication.showToast(R.string.error_no_item);
            pDialog.dismiss();
            return;
        }
        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }, 1000);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.get("key", "") + "" != "") {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
        }
        if (MyApplication.first_check == 2) {//校验后台时长
            fab_1.setImageResource(R.drawable.yep_selector);
            MyApplication.first_check = 0;
            //取缓存数据
            if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
                findOnLineDate();
            } else {
                findOffLineDate();
            }
            toolbar.setTitle("全部");
        }
    }

    //获取bmob上的数据
    private void findOnLineDate() {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("联网加载中...");
        pDialog.show();
        AnimationUtil.getInstance().startAnim(refresh, 500);
        BmobQuery<AccountBean> query = new BmobQuery<>();
        if (MyApplication.getUser() != null) {
            String id = MyApplication.getUser().getObjectId();
            user.setObjectId(id);
            query.addWhereEqualTo("user", id);
            query.findObjects(new FindListener<AccountBean>() {
                @Override
                public void done(List<AccountBean> objects, BmobException e) {
                    if (objects != null) {
                        mAccountBeans = objects;
                        if (mAccountBeans.size() < 1) {
                            if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
                                tip.setText(R.string.on_item);
                                tip.setVisibility(View.VISIBLE);
                                pDialog.dismiss();
                                AnimationUtil.getInstance().stopAnim();
                                return;
                            }
                        }
                        tip.setVisibility(View.GONE);
                        //缓存
                        SPUtils.setDataList("beans", mAccountBeans);
                        getTags();
                        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
                        mStackView.setAdapter(mTestStackAdapter);
                        mTestStackAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(() -> {
                            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                            // hideAnimate();
                            AnimationUtil.getInstance().stopAnim();
                        }, 100);
                    } else {
                        if (e.getErrorCode() == 9016 && SPUtils.getDataList("beans", AccountBean.class).size() > 1) {
                            MyApplication.showToast(getString(R.string.offline));
                            AnimationUtil.getInstance().stopAnim();
                            pDialog.dismiss();
                            return;
                        } else {
                            tip.setText(R.string.on_item);
                            tip.setVisibility(View.VISIBLE);
                        }
                    }
                    pDialog.dismiss();
                }
            });
        }

    }

    //按名称排序
    private void findOffLineDateByName(List<AccountBean> mAccountBeans) {

        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("按名称排序中...");
        if (!isFinishing()) {
            pDialog.show();
        }

        closeStackView();

        mAccountBeans = DataUtils.getDataByName(mAccountBeans);
        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);

        mTestStackAdapter.notifyDataSetChanged();
        final List<AccountBean> finalMAccountBeans = mAccountBeans;
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, finalMAccountBeans.size())));
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }, 1000);
    }


    //按最近更新时间排序
    private void findDateByTime(final SortUtils sortUtils, final List<AccountBean> mAccountBeans) {
        try {//233，这个地方单独判断isFinish还是会崩，得再捕捉一次
            if (!MainActivity.this.isFinishing()) {
                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
                pDialog.setTitleText("加载中");
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(mAccountBeans, Collections.reverseOrder(sortUtils));
        MyToast.show(MainActivity.this, "短按，已按最近更新时间排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
            mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }, 1000);

    }


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
                mSearchView.setNewHistoryList(DataDeal.getInstance(MainActivity.this).getHistory());
                mSearchView.autoOpenOrClose();
                fab.setVisibility(View.GONE);
                fab_1.setVisibility(View.GONE);
                fab_2.setVisibility(View.GONE);
                break;

            case R.id.refresh:
                if (MyApplication.isSign()) {
                    closeStackView();
                    if (MyApplication.isNetworkAvailable(MainActivity.this)) {
                        findOnLineDate();
                    } else {
                        findOffLineDate();
                    }
                    toolbar.setTitle("全部");

                } else {
                    MyApplication.showToast(getString(R.string.error_login));

                }
                break;
            case R.id.fab_2:
                if (!MyApplication.isNetworkAvailable(MainActivity.this)) {
                    MyApplication.showToast("网络不可用喔");
                    return;
                }
                if (config == null) {
                    MyApplication.showToast("还没有准备好喔，等会儿再点击才行");
                    return;
                }
                Intent intent = new Intent(AppManager.getAppManager().currentActivity(), EatRiceActivity.class);
                if (config.getRiceUrl() == null || config.getRiceUrl().equals("")) {
                    intent.putExtra("url", "http://suo.im/6iPEP4");
                } else {
                    intent.putExtra("url", config.getRiceUrl());
                }
                startActivity(intent);
                break;
            case R.id.setting:

                Intent i = new Intent(MainActivity.this, SettingActivity.class);
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
        mSearchView.setNewHistoryList(DataDeal.getInstance(MainActivity.this).getHistory());
        mSearchView.setOnCleanHistoryClickListener(() -> {
            SPUtils.remove("historyLists");
            MyApplication.showToast(getString(R.string.succes_delete));
        });
        //设置软键盘搜索按钮点击事件
        mSearchView.setOnSearchActionListener(searchText -> {
            DataDeal.getInstance(MainActivity.this).addHistory(searchText);//历史记录存入sp
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
            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
            pDialog.setTitleText("搜索中...");
            pDialog.show();
            toolbar.setTitle(searchText.trim());
            mAccountBeans = DataUtils.searchDataByName(mAccountBeans, searchText.trim());
            if (mAccountBeans.size() == 0) {
                MyApplication.showToast(getString(R.string.no_item_name));
                pDialog.dismiss();
                return;
            }
            mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
            mStackView.setAdapter(mTestStackAdapter);
            mTestStackAdapter.notifyDataSetChanged();
            new Handler().postDelayed(() -> {
                //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }, 1000);

            closeSearchView();
        });


    }

    //关闭搜索view
    private void closeSearchView() {
        mSearchView.close();
        fab.setVisibility(View.VISIBLE);
        fab_1.setVisibility(View.VISIBLE);
        fab_2.setVisibility(View.VISIBLE);
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
                    nameAdapter = new NameAdapter(MainActivity.this, DataDeal.getInstance(MainActivity.this).fillData(strings_name));

                    left_letter.setOnTouchLetterChangeListener(s1 -> {
                        // 该字母首次出现的位置
                        int position = nameAdapter.getPositionForSection(s1.charAt(0));
                        if (position != -1) {
                            mListNames.setSelection(position);
                        }
                        Log.d("点击", position + "");
                    });

                    mListNames.setAdapter(nameAdapter);
                    ArrayAdapter<String> adapterTimes = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
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
        Timer tExit = null;
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
