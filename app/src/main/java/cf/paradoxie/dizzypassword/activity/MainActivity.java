package cf.paradoxie.dizzypassword.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.os.Message;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.loopeer.cardstack.CardStackView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.BaseConfig;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.db.SortBean;
import cf.paradoxie.dizzypassword.db.WordsBean;
import cf.paradoxie.dizzypassword.http.Http;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.SortUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.DialogView;
import cf.paradoxie.dizzypassword.view.MyLetterSortView;
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
import km.lmy.searchview.SearchView;

@SuppressLint("RestrictedApi")
public class MainActivity extends BaseActivity implements CardStackView.ItemExpendListener, View.OnClickListener {
    private boolean optionMenuOn = true;  //显示optionmenu
    private Menu aMenu;         //获取optionmenu
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
    private List<AccountBean> mAccountBeans_name;
    private List<AccountBean> mAccountBeans_tag;
    private List<AccountBean> currentBean;
    private BaseConfig config;

    private TextView tip, tv_name, tv_words, tv_words_chicken;
    private SweetAlertDialog pDialog = null;
    private static Boolean isExit = false;
    private BmobUser user = new BmobUser();
    public static Toolbar toolbar = null;
    private DialogView mDialogView;
    private LinearLayout main_btn;
    private long mCurrentPlayTime;
    private ObjectAnimator animator;
    private ImageView refresh, setting, search, join_qq, iv_user_photo;
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private String[] strings;
    private String[] strings_name;
    private FloatingActionButton fab, fab_1, fab_2;
    List<Map.Entry<String, Integer>> mappingList = null;
    List<String> historys = new ArrayList<>();
    private SortUtils mSortUtils;
    private DrawerLayout mDrawerLayout;
    private ListView mListNames, mListTimes;
    private MyLetterSortView left_letter;
    private String url;
    private NameAdapter nameAdapter;
    private List<SortBean> mFriends;
    private TextView mTvDialog;

    private ImageView imgView;
    String path = Environment.getExternalStorageDirectory().getPath()
            + "/头像/";

    final Handler handler1 = new Handler() {
        @SuppressLint("HandlerLeak")
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
                    MyApplication.loadImg(iv_user_photo, url, true);
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

        ;
    };

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
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tv_words.getText());
                MyApplication.showSnack(v, R.string.str_copy, ThemeUtils.getPrimaryColor(MainActivity.this));
            }
        });
        tv_words_chicken = (TextView) navigationView.findViewById(R.id.tv_words_chicken);
        tv_words_chicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tv_words_chicken.getText());
                MyApplication.showSnack(v, R.string.str_copy, ThemeUtils.getPrimaryColor(MainActivity.this));
            }
        });
        tv_words_chicken.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(MainActivity.this));
//        Button button = (Button) navigationView.findViewById(R.id.button);
        mListNames = (ListView) navigationView.findViewById(R.id.list_names);
        left_letter = (MyLetterSortView) navigationView.findViewById(R.id.left_letter);
        mListTimes = (ListView) navigationView.findViewById(R.id.list_times);
//        button.setText("测试");
        mTvDialog = (TextView) findViewById(R.id.tv_dialog);
        left_letter.setTvDialog(mTvDialog);
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

        mSearchView = findViewById(R.id.searchView);
        refresh = findViewById(R.id.refresh);
//        final String isRotate = SPUtils.get("iconRotate", true) + "";

        config = getConfig();
        if (config == null) {
            config = new BaseConfig(MyApplication.GetVersion() + "",
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

        setting = (ImageView) findViewById(R.id.setting);
        search = (ImageView) findViewById(R.id.search);
        join_qq = (ImageView) findViewById(R.id.join_qq);
        refresh.setOnClickListener(this);
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
        fab_2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab_2.setOnClickListener(this);

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
            //            getTags();
            new Thread() {
                public void run() {
                    getTags();
                }
            }.start();
        }

        mSortUtils = new SortUtils();
        RxBus.getInstance().register(RxBean.class, new Consumer<RxBean>() {
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
            handler.postDelayed(() -> {
                getVersion();
                getWords();
                getWordsChicken();

                String isHeadImage = SPUtils.get("isHeadImage", "false") + "";
                if (isHeadImage.equals("true")) {
                    getPic();
                }
                //吃饭动画

            }, 3000);

        } else {
            tv_words.setText(SPUtils.get("text", "世上无难事，只要肯放弃") + "");
        }
    }

    private void getWordsChicken() {
        Http.get(Constans.WORDS_ID_CHICKEN + MyApplication.getData(), s -> {
//            Log.d("----毒鸡汤", s);
            JSONObject obj = null;
            try {
                obj = new JSONObject(s);
                JSONArray jsonArray = new JSONArray(obj.getString("data"));
                JSONObject words = new JSONObject(jsonArray.get(jsonArray.length() - 1).toString());
                String text = words.getString("data");
                Message msg = new Message();
                msg.what = 1;
                msg.obj = text;
                handler1.sendMessage(msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });


    }

    private void getPic() {

        Http.get(Constans.PIC_ID, s -> {
            try {
                JSONObject obj = new JSONObject(s);
                url = obj.getString("pic_url");
                Log.d("----pic", url);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = url;
                handler1.sendMessage(msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


    }

    public void smallImgClick(View v) {
        //有背景图
        //全屏显示的方法
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        imgView = getView();
        MyApplication.loadImg(imgView, url, false);
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
        imgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                requestAllPower();
                popSave();
                return false;
            }
        });
    }

    private void popSave() {

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("保存图片")
                .setContentText("保存路径：" + path)
                .setConfirmText("保存")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        saveBitmap(imgView, path);
                        sDialog.cancel();
                    }
                })
                .show();

    }

    public void saveBitmap(View view, String filePath) {

        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        //存储
        FileOutputStream outStream = null;

        isFolderExists(filePath);

        File file = new File(filePath + Calendar.getInstance().getTimeInMillis() + ".png");
        if (file.isDirectory()) {//如果是目录不允许保存
            Toast.makeText(MainActivity.this, "该路径为目录路径", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error", e.getMessage() + "#");
            Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                bitmap.recycle();
                if (outStream != null) {
                    outStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);

        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else
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

        Http.get(Constans.WORDS, s -> {
            Log.e("-----", s);
            try {
                JSONObject obj = new JSONObject(s);
                JSONArray jsonArray = obj.getJSONArray("newslist");

                obj = jsonArray.getJSONObject(0);

                final WordsBean wordsBean = new WordsBean();
                wordsBean.setFamous_name(obj.getString("mrname"));
                wordsBean.setFamous_saying(obj.getString("content"));
                String text = wordsBean.getFamous_saying() + "      ---" + wordsBean.getFamous_name();
                Log.e("---mrname--", wordsBean.getFamous_name());
                Message msg = new Message();
                msg.what = 3;
                msg.obj = text;
                handler1.sendMessage(msg);

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
        });

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
                    config = baseConfig;
                    final int newVersion = Integer.parseInt(baseConfig.getNewVersion());
                    String title = baseConfig.getTitle();
                    String details = baseConfig.getDetails();

                    if (baseConfig.isIconRotate()) {
                        startAnim(fab_2, 7000);
                    }
                    storeConfig(baseConfig);

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

    private void storeConfig(BaseConfig config) {
        String s = new Gson().toJson(config);
        SPUtils.put("config", s);
    }

    private BaseConfig getConfig() {
        String con = (String) SPUtils.get("config", "");
        if (con.equals("")) {
            return null;
        }
        BaseConfig baseConfig = new Gson().fromJson(con, BaseConfig.class);
        return baseConfig;
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
        startAnim(refresh, 500);
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
                        new Handler().postDelayed(() -> {
                                    //为什么不能把TEST_DATA拿出来单独处理一次，会出现ANR
                                    mTestStackAdapter.updateData(Arrays.asList(DesUtil.getRandomFromArray(TEST_DATAS, mAccountBeans.size())));
                                    // hideAnimate();
                                    stopAnim(animator);
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
                fab_2.setVisibility(View.GONE);
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
            case R.id.fab_2:
                if (!MyApplication.isNetworkAvailable(MainActivity.this)) {
                    MyApplication.showToast("网络不可用喔");
                    return;
                }

                if (config == null) {
                    MyApplication.showToast("还没有准备好喔，等会儿再点击才行");
                    return;
                }

                if (config.getRiceUrl() == null || config.getRiceUrl().equals("")) {
//                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                            .setTitleText(config.getWindowTitle())
//                            .setContentText(config.getWindowDetailsContent())
//                            .setConfirmText(config.getWindowConfirm())
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    ClipboardManager cm = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
//                                    cm.setText(config.getWindowCopyContent());
//                                    try {
//                                        MyApplication.openAppByPackageName(MainActivity.this, config.getWindowJumpPackage());
//                                    } catch (PackageManager.NameNotFoundException e) {
//                                        e.printStackTrace();
//                                    }
//                                    sDialog.cancel();
//                                }
//                            })
//                            .show();
                    Intent intent = new Intent(AppManager.getAppManager().currentActivity(), EatRiceActivity.class);
                    intent.putExtra("url", "http://suo.im/6iPEP4");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AppManager.getAppManager().currentActivity(), EatRiceActivity.class);
                    intent.putExtra("url", config.getRiceUrl());
                    startActivity(intent);
                }
                break;
            case R.id.setting:

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                if (config.getWindowCopyContent() != null || !config.getWindowCopyContent().equals("")) {
                    intent.putExtra("copyCode", config.getWindowCopyContent());
                    Log.d("23333", config.getWindowCopyContent());
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void startAnim(ImageView refresh, long time) {
        animator = ObjectAnimator.ofFloat(refresh, "rotation", 0f, 360.0f);
        animator.setDuration(time);
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
                fab_2.setVisibility(View.VISIBLE);
            }
        });
        mSearchView.getCloseTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchView.close();
                fab.setVisibility(View.VISIBLE);
                fab_1.setVisibility(View.VISIBLE);
                fab_2.setVisibility(View.VISIBLE);
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
                    fab_2.setVisibility(View.VISIBLE);
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
                fab_2.setVisibility(View.VISIBLE);
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

        mFriends = fillData(strings_name);
        //更新搜索框内的标签
        new Thread() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchView.initFlowView(strings);
                        mSearchView.initFlowViewByName(strings_name);

//                        final ArrayAdapter<String> adapterNames = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings_name);
                        nameAdapter = new NameAdapter(MainActivity.this, mFriends);


                        left_letter.setOnTouchLetterChangeListener(new MyLetterSortView.OnTouchLetterChangeListener() {
                            @Override
                            public void letterChange(String s) {
                                // 该字母首次出现的位置
                                int position = nameAdapter.getPositionForSection(s.charAt(0));
                                if (position != -1) {
                                    mListNames.setSelection(position);
                                }
                                Log.d("dianji", position + "");
                            }
                        });

                        mListNames.setAdapter(nameAdapter);

                        ArrayAdapter<String> adapterTimes = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
                        mListTimes.setAdapter(adapterTimes);
                    }
                });
            }
        }.start();


    }

    //填充数据
    private List<SortBean> fillData(String[] data) {
        List<SortBean> entities = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            SortBean sortEntity = new SortBean();
            sortEntity.setName(data[i]);

            String alphabet = data[i].substring(0, 1);
            /*判断首字符是否为中文，如果是中文便将首字符拼音的首字母拿出来*/
            if (alphabet.matches("[\\u4e00-\\u9fa5]+")) {
                sortEntity.setSortLetters(DataUtils.getAlphabet(data[i]));
            } else {
                sortEntity.setSortLetters(data[i].substring(0, 1).toUpperCase());
            }
            entities.add(sortEntity);
        }
        return entities;
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
