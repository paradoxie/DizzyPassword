package cf.paradoxie.dizzypassword.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.Constans;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.TestStackAdapter;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.BaseConfig;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.db.WordsBean;
import cf.paradoxie.dizzypassword.utils.DataUtils;
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
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import km.lmy.searchview.SearchView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements CardStackView.ItemExpendListener, View.OnClickListener {
    private boolean optionMenuOn = true;  //显示optionmenu
    private Menu aMenu;         //获取optionmenu
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6, R.color.color_7, R.color.color_8,
            R.color.color_9, R.color.color_10, R.color.color_11, R.color.color_12,
            R.color.color_13, R.color.color_14, R.color.color_15, R.color.color_16,
            R.color.color_17, R.color.color_18, R.color.color_19, R.color.color_20,
            R.color.color_21, R.color.color_22, R.color.color_23, R.color.color_24,
            R.color.color_25, R.color.color_26
    };

    private CardStackView mStackView;
    private TestStackAdapter mTestStackAdapter;

    private List<AccountBean> mAccountBeans;
    private List<AccountBean> mAccountBeans_name;
    private List<AccountBean> mAccountBeans_tag;
    private List<AccountBean> currentBean;
    private TextView tip, tv_name, tv_words,tv_words_chicken;
    private SweetAlertDialog pDialog = null;
    private static Boolean isExit = false;
    private BmobUser user = new BmobUser();
    public static Toolbar toolbar = null;
    private DialogView mDialogView;
    private LinearLayout main_btn;
    private long mCurrentPlayTime;
    private ObjectAnimator animator;
    private ImageView refresh, red_package, setting, search, join_qq,iv_user_photo;
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private String[] strings;
    private String[] strings_name;
    private FloatingActionButton fab, fab_1;
    List<Map.Entry<String, Integer>> mappingList = null;
    List<String> historys = new ArrayList<>();
    private SortUtils mSortUtils;
    private DrawerLayout mDrawerLayout;
    private ListView mListNames, mListTimes;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
//        UltimateBar ultimateBar = new UltimateBar(this);
//        ultimateBar.setTransparentBar(Color.BLUE, 50);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ThemeUtils.initStatusBarColor(MainActivity.this, ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("全部");
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        tv_name = (TextView) headerView.findViewById(R.id.tv_name);
        tv_words = (TextView) headerView.findViewById(R.id.tv_words);
        tv_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tv_words.getText());
                MyApplication.showSnack(v, R.string.str_copy, ThemeUtils.getPrimaryColor(MainActivity.this));
            }
        });
        tv_words_chicken = (TextView) navigationView.findViewById(R.id.tv_words_chicken);
        tv_words_chicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tv_words_chicken.getText());
                MyApplication.showSnack(v, R.string.str_copy, ThemeUtils.getPrimaryColor(MainActivity.this));
            }
        });
        tv_words_chicken.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
//        Button button = (Button) navigationView.findViewById(R.id.button);
        mListNames = (ListView) navigationView.findViewById(R.id.list_names);
        mListTimes = (ListView) navigationView.findViewById(R.id.list_times);
//        button.setText("测试");
        mListNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchText = strings_name[position];
                toggleRightSliding();
                while (searchText.contains("(")) {
                    String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                    searchText = searchText.replace(str, "");
                }
                searchDate(searchText.trim());
                toolbar.setTitle(searchText.trim());
            }
        });
        mListTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchText = strings[position];
                toggleRightSliding();
                while (searchText.contains("(")) {
                    String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                    searchText = searchText.replace(str, "");
                }
                searchDate(searchText.trim());
                toolbar.setTitle(searchText.trim());
            }
        });

        headerView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));

        mSearchView = (SearchView) findViewById(R.id.searchView);
        refresh = (ImageView) findViewById(R.id.refresh);
        red_package = (ImageView) findViewById(R.id.red_package);
        setting = (ImageView) findViewById(R.id.setting);
        search = (ImageView) findViewById(R.id.search);
        join_qq = (ImageView) findViewById(R.id.join_qq);
        refresh.setOnClickListener(this);
        red_package.setOnClickListener(this);
        setting.setOnClickListener(this);
        search.setOnClickListener(this);
        join_qq.setOnClickListener(this);
        //长按按名称排序
        join_qq.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MyToast.show(MainActivity.this, "长按，已按条目名称排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

                if (toolbar.getTitle().toString().equals("全部")) {
                    mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
                    findOffLineDateByName(mAccountBeans);
                } else {
                    findOffLineDateByName(currentBean);
                }
                return false;
            }
        });
        main_btn = (LinearLayout) findViewById(R.id.main_btn);
        main_btn.setVisibility(View.VISIBLE);
        //检测menu操作，第二次进入app时是否显示menu
        if (!(Boolean) SPUtils.get("optionMenuOn", true)) {
            optionMenuOn = false;
            checkOptionMenu();
        }

        fab_1 = (FloatingActionButton) findViewById(R.id.fab_1);

        toolbar.setNavigationIcon(R.drawable.navigation);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRightSliding();
            }
        });
        if (SPUtils.get("name", "") != "") {
            if (MyApplication.first_check == 0) {
//                toolbar.setNavigationIcon(R.drawable.yep_selector);
                fab_1.setImageResource(R.drawable.yep_selector);
            } else {
//                toolbar.setNavigationIcon(R.drawable.yep);
                fab_1.setImageResource(R.drawable.yep);
            }

            fab_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.first_check == 0) {
                        checkActivity();
                        mDialogView.setOnPosNegClickListener(new DialogView.OnPosNegClickListener() {
                            @Override
                            public void posClickListener(String value) {
                                //校验密码
                                if (value.equals(SPUtils.get("password", "") + "")) {
                                    MyApplication.first_check = 1;
                                    hideInputWindow();
                                    //换图标，解锁
//                                    toolbar.setNavigationIcon(R.drawable.yep);
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
                                        "帐号:" + (String.valueOf(SPUtils.get("name", ""))) + "\n密码:" + (String.valueOf(SPUtils.get("password", ""))) +
                                                "\n\n确定要锁定当前操作权限么？")
                                .setConfirmText("锁定")
                                .setCancelText("算啦")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        MyApplication.first_check = 0;

                                        //换图标：加锁
//                                        toolbar.setNavigationIcon(R.drawable.yep_selector);
                                        fab_1.setImageResource(R.drawable.yep_selector);
                                        sDialog.cancel();
                                    }
                                })
                                .show();
                    }
                }
            });
        }

        tip = (TextView) findViewById(R.id.tip);

        tv_name.setText(SPUtils.get("name", "点击去登录") + "");
        fab = (FloatingActionButton) findViewById(R.id.fab);


        //初始化搜索数据操作
        searchData();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
//                                    toolbar.setNavigationIcon(R.drawable.yep);
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
            Bmob.resetDomain("http://password.usql.club/8/");
            Bmob.initialize(this, Constans.APPLICATION_ID);

        } else {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
        }
        if (!MyApplication.isSign()) {
            tip.setVisibility(View.VISIBLE);
//            toolbar.setTitle("未登录");
            fab_1.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.login);
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //缓存用户对象为空时， 可打开用户注册界面…
                    Intent intent = new Intent(MainActivity.this, SignActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            toolbar.setTitle("全部");
            fab.setImageResource(R.drawable.add);
            fab_1.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            join_qq.setVisibility(View.VISIBLE);

            //获得tag的统计数据
            //            getTags();
            new Thread() {
                public void run() {
                    getTags();
                }
            }.start();

        }

        //取缓存数据
        if (BmobUser.getCurrentUser(BmobUser.class) == null) {
            return;
        }
        if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
            findOnLineDate();
        } else {
            findOffLineDate();
        }

        mSortUtils = new SortUtils();
        RxBus.getInstance().register(RxBean.class,new Consumer<RxBean>() {
                    @Override
                    public void accept(RxBean rxBean) throws Exception {
                        if (rxBean.getMessage() != null) {
                            //按tag检索
                            searchDate(rxBean.getMessage());
                            return;
                        }
                        if (rxBean.getAction() != null) {
                            if (rxBean.getAction() == "done") {
                                //点击新建/更新时间排序
                                if (mStackView.isExpending()) {
                                    mStackView.setSelectPosition(-1);
                                    mStackView.setScrollEnable(true);
                                }
//                                findDateByTime(mSortUtils);
                            } else if (rxBean.getAction() == "name") {
                                //点击条目名称，根据名称排序
//                                MyToast.show(MainActivity.this, "长按，已按条目名称排序", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));
//
//                                findOffLineDateByName();
                            }
                            return;
                        }
                    }

                });

        if (MyApplication.isNetworkAvailable(MainActivity.this)) {
            //有网的时候判断版本信息
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getVersion();
                    getWords();
                    getWordsChicken();
                    getPic();
                }
            }, 3000);

        } else {
            tv_words.setText(SPUtils.get("text", "世上无难事，只要肯放弃") + "");
        }
    }

    private void getWordsChicken() {
        String s = MyApplication.get(Constans.WORDS_ID_CHICKEN+MyApplication.getData());
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
            JSONArray jsonArray = new JSONArray(obj.getString("data"));
            JSONObject words = new JSONObject(jsonArray.get(jsonArray.length()-1).toString());
            String text = words.getString("data");
            tv_words_chicken.setText(text);
            SPUtils.put("text_chicken", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getPic() {
        int a = (int) (1 + Math.random() * (10 - 1 + 1));
        int b = (int) (1 + Math.random() * (10 - 1 + 1));
//        int c = (int) (1 + Math.random() * (b - 1 + 1));
        int c = new Random().nextInt(b);
        String s = MyApplication.get(Constans.PIC_ID + a + "/" + b);
        try {
            JSONObject obj = new JSONObject(s);
            JSONArray jsonArray = new JSONArray(obj.getString("results"));
            JSONObject pic = new JSONObject(jsonArray.get(c).toString());
            url = pic.getString("url");
            Log.d("----pic", url);
            iv_user_photo = (ImageView) findViewById(R.id.iv_user_photo);
            MyApplication.loadImg(iv_user_photo,url,true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void smallImgClick(View v) {
        //有背景图
         //全屏显示的方法
     final Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
     ImageView imgView = getView();
     MyApplication.loadImg(imgView,url,false);
     dialog.setContentView(imgView);
     dialog.getWindow().setWindowAnimations(R.style.DialogOutAndInStyle);   //设置dialog的显示动画
     dialog.show();

        // 点击图片消失
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }


    private ImageView getView() {
        ImageView imgView = new ImageView(this);
        imgView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

        @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.mipmap.ic_logo);
        Drawable drawable = BitmapDrawable.createFromStream(is, null);
        imgView.setImageDrawable(drawable);

        return imgView;
    }

    private void getWords() {
//        WordsBean wordsBean = new WordsBean();
//        wordsBean.setFamous_saying("世上无难事，只要肯放弃");
//        wordsBean.setFamous_name("路人甲");
//        wordsBean.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    Log.e("-----","保存成功");
//                }
//            }
//        });

        BmobQuery<WordsBean> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Constans.WORDS_ID, new QueryListener<WordsBean>() {
            @Override
            public void done(WordsBean wordsBean, BmobException e) {
                if (e == null) {
                    if (!wordsBean.getFamous_saying().equals("")) {
                        String time = wordsBean.getUpdatedAt().substring(0, 11).trim();
                        String id = wordsBean.getObjectId();
                        String dateNowStr = MyApplication.getData();

                        if (time.equals(dateNowStr)) {//如果更新日期为当前日期，就直接取bmob数据
                            tv_words.setText(wordsBean.getFamous_saying() + "      ---" + wordsBean.getFamous_name());
                        } else {//如果更新日期不是当前日期，就请求接口并更新到bmob
                            getWordsByAvatar(id);
                        }
                    } else {//如果值为空，就请求接口并添加到bmob
                        getWordsByAvatar("");
                    }
                } else {
                    Log.e("-----", e.getMessage());
                    tv_words.setText(SPUtils.get("text", "世上无难事，只要肯放弃") + "");
                }
            }
        });

    }

    private void getWordsByAvatar(String id) {
        try {
            String s = MyApplication.get(Constans.WORDS);
            Log.e("-----", s);
            JSONObject obj = new JSONObject(s);
            final WordsBean wordsBean = new WordsBean();
            wordsBean.setFamous_name(obj.getJSONObject("result").getString("famous_name"));
            wordsBean.setFamous_saying(obj.getJSONObject("result").getString("famous_saying"));
            String text = wordsBean.getFamous_saying() + "      ---" + wordsBean.getFamous_name();
            tv_words.setText(text);
            SPUtils.put("text", text);
            if (id.equals("")) {//上传
                wordsBean.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {

                        }
                    }
                });
            } else {//更新
                wordsBean.update(id, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toggleRightSliding() {//该方法控制右侧边栏的显示和隐藏
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);//关闭抽屉
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);//打开抽屉
        }
    }

    private void getVersion() {
        BmobQuery<BaseConfig> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Constans.CONFIG_ID, new QueryListener<BaseConfig>() {
            @Override
            public void done(BaseConfig baseConfig, BmobException e) {
                if (e == null) {
                    final int newVersion = Integer.parseInt(baseConfig.getNewVersion());
                    String title = baseConfig.getTitle();
                    String details = baseConfig.getDetails();
                    final String toast = baseConfig.getToast();
                    if (newVersion >
                            Integer.parseInt(String.valueOf(SPUtils.get("version", MyApplication.GetVersion())))
                    ) {//新版本大于本地版本
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(title)
                                .setMessage(details)
                                .setCancelable(false)
                                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //前往酷安
                                        MyApplication.launchAppDetail(MyApplication.getContext().getPackageName(), "com.coolapk.market");
                                    }
                                })
                                .setNeutralButton("我就不.GIF", null)
                                .setNegativeButton("憋弹了！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //当前版本永久关闭，更新云端版本信息
                                        SPUtils.put("version", newVersion);
                                        MyToast.show(MainActivity.this, toast, ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

                                    }
                                }).show();
                    }
                } else {
                    Log.e("-----", e.getMessage());
                }
            }
        });
    }


    private void findOffLineDate() {

        if (mStackView.isExpending()) {
            mStackView.setSelectPosition(-1);
            mStackView.setScrollEnable(true);
        }

        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
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

    /**
     * 使用tag检索
     *
     * @param s
     */

    private void searchDate(String s) {
        //手动清除一次全部view，避免重用时的重合
        if (mStackView.isExpending()) {
            mStackView.setSelectPosition(-1);
            mStackView.setScrollEnable(true);
        }
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("查找中...");
        if (!MainActivity.this.isFinishing()) {
            pDialog.show();
        }

        toolbar.setTitle(s);
        mAccountBeans_tag = DataUtils.searchDataByTag(mAccountBeans, s);
        currentBean = mAccountBeans_tag;

        if (mAccountBeans_tag.size() == 0) {
            MyApplication.showToast(R.string.error_no_item);
            pDialog.dismiss();
            return;
        }
        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans_tag);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.notifyDataSetChanged();
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans_tag.size())));
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                }
                , 1000
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
//        Drawable drawable = getResources().getDrawable(R.drawable.bg_trans_navigation);
//        Log.e("onActivityonPause",  "蒙版");
//        cl_main.setForeground(drawable);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
//        Drawable drawable = getResources().getDrawable(R.drawable.bg_all_trans_navigation);
//        Log.e("onActivityonResume",  "蒙版消失");
//        cl_main.setForeground(drawable);

        if (SPUtils.get("key", "") + "" != "") {
            Bmob.initialize(this, SPUtils.get("key", "") + "");
        }
        if (MyApplication.first_check == 2) {//校验后台时长
//            toolbar.setNavigationIcon(R.drawable.yep_selector);
            fab_1.setImageResource(R.drawable.yep_selector);
            MyApplication.first_check = 0;
            //取缓存数据
            if (SPUtils.getDataList("beans", AccountBean.class).size() < 1) {
                findOnLineDate();
                toolbar.setTitle("全部");
            } else {
                findOffLineDate();
                toolbar.setTitle("全部");
            }
        }
    }

    private void findOnLineDate() {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("联网加载中...");
        pDialog.show();
        startAnim();
        BmobQuery<AccountBean> query = new BmobQuery<>();
        if (MyApplication.getUser() != null) {
            String id = MyApplication.getUser().getObjectId();
            user.setObjectId(id);
            query.addWhereEqualTo("user", new BmobPointer(user));
            //            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存获取数据，如果没有，再从网络获取。
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
                                stopAnim(animator);
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
                            MyApplication.showToast(getString(R.string.offline));
                            stopAnim(animator);
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

    private void findOffLineDateByName(List<AccountBean> mAccountBeans) {

        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
        pDialog.setTitleText("按名称排序中...");
        if (!isFinishing()) {
            pDialog.show();
        }

//        mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);

        if (mStackView.isExpending()) {
            mStackView.setSelectPosition(-1);
            mStackView.setScrollEnable(true);
        }

        mAccountBeans = DataUtils.getDataByName(mAccountBeans);
        mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans);
        mStackView.setAdapter(mTestStackAdapter);

        mTestStackAdapter.notifyDataSetChanged();
        final List<AccountBean> finalMAccountBeans = mAccountBeans;
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, finalMAccountBeans.size())));
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                }
                , 1000
        );
    }


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
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                        mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                }
                , 1000
        );

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



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //短按按时间排序
            case R.id.join_qq:
//                MyApplication.joinQQGroup(Constans.QQ_ID);
                //点击新建/更新时间排序
                if (mStackView.isExpending()) {
                    mStackView.setSelectPosition(-1);
                    mStackView.setScrollEnable(true);
                }
                if (toolbar.getTitle().toString().equals("全部")) {
                    mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
                    findDateByTime(mSortUtils, mAccountBeans);
                } else {
                    findDateByTime(mSortUtils, currentBean);
                }
                break;
            case R.id.search:
                mSearchView.setNewHistoryList(getHistory());
                mSearchView.autoOpenOrClose();
                fab.setVisibility(View.GONE);
                fab_1.setVisibility(View.GONE);
                break;

            case R.id.refresh:
                if (MyApplication.isSign()) {
                    if (mStackView.isExpending()) {
                        mStackView.setSelectPosition(-1);
                        mStackView.setScrollEnable(true);
                    }
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
            case R.id.red_package:
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("口令复制成功,去支付宝搜索吧")
                        .setContentText("支付宝大红包，金额随机，最高￥99喔😃\n" +
                                "\n每天都可以来领取一次哈\n大红包可以联系套现哦\n")
                        .setConfirmText("前往支付宝搜索")
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
                fab_1.setVisibility(View.VISIBLE);
            }
        });
        mSearchView.getCloseTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchView.close();
                fab.setVisibility(View.VISIBLE);
                fab_1.setVisibility(View.VISIBLE);
            }
        });
        mSearchView.setNewHistoryList(getHistory());
        mSearchView.setOnCleanHistoryClickListener(new SearchView.OnCleanHistoryClickListener() {
            @Override
            public void onClick() {
                SPUtils.remove("historyLists");
                MyApplication.showToast(getString(R.string.succes_delete));
            }
        });
        //设置软键盘搜索按钮点击事件
        mSearchView.setOnSearchActionListener(new SearchView.OnSearchActionListener() {
            @Override
            public void onSearchAction(String searchText) {
                addHistory(searchText);//历史记录存入sp
                if (searchText.contains("(")) {
                    while (searchText.contains("(")) {
                        String str = searchText.substring(searchText.indexOf("("), searchText.indexOf(")") + 1);
                        searchText = searchText.replace(str, "");
                    }
                    searchDate(searchText.trim());
                    mSearchView.close();
                    fab.setVisibility(View.VISIBLE);
                    fab_1.setVisibility(View.VISIBLE);
                    return;
                }
                if (mStackView.isExpending()) {
                    mStackView.setSelectPosition(-1);
                    mStackView.setScrollEnable(true);
                }
                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
                pDialog.setTitleText("搜索中...");
                pDialog.show();
                //                String name = DesUtil.encrypt(searchText.trim(), SPUtils.getKey());//关键词
                toolbar.setTitle(searchText.trim());
                mAccountBeans_name = DataUtils.searchDataByName(mAccountBeans, searchText.trim());
                currentBean = mAccountBeans_name;
                if (mAccountBeans_name.size() == 0) {
                    MyApplication.showToast(getString(R.string.no_item_name));
                    pDialog.dismiss();
                    return;
                }
                mTestStackAdapter = new TestStackAdapter(MainActivity.this, mAccountBeans_name);
                mStackView.setAdapter(mTestStackAdapter);
                mTestStackAdapter.notifyDataSetChanged();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                                mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans_name.size())));
                                if (pDialog != null && pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }
                            }
                        }
                        , 1000
                );

                mSearchView.close();
                fab.setVisibility(View.VISIBLE);
                fab_1.setVisibility(View.VISIBLE);
            }
        });


    }

    private void getTags() {
        final List<String> s = new ArrayList<>();
        mAccountBeans = SPUtils.getDataList("beans", AccountBean.class);
        //获取所有的tag
        for (int j = 0; j < mAccountBeans.size(); j++) {
            s.addAll(mAccountBeans.get(j).getTag());
        }
        List<Map.Entry<String, Integer>> tags;
        List<Map.Entry<String, Integer>> tags_name;
        tags = DataUtils.getTagList(mappingList, s);
        tags_name = DataUtils.getTagListByName(mappingList, s);
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchView.initFlowView(strings);
                        mSearchView.initFlowViewByName(strings_name);

                        ArrayAdapter<String> adapterNames = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings_name);
                        mListNames.setAdapter(adapterNames);
                        ArrayAdapter<String> adapterTimes = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
                        mListTimes.setAdapter(adapterTimes);
                    }
                });
            }
        }.start();


    }


    private void addHistory(String value) {
        String historyLists = (String) SPUtils.get("historyLists", "");
        if (historyLists == "") {
            SPUtils.put("historyLists", " " + value);
        } else {
            if (historyLists.contains(value)) {
                historyLists = historyLists.replace(value, "");
            }
            if (historyLists.contains("  ")) {
                historyLists = historyLists.replace("  ", " ");
            }
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
