package cf.paradoxie.dizzypassword.data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.base.AppManager;
import cf.paradoxie.dizzypassword.base.Constants;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.AppConfig;
import cf.paradoxie.dizzypassword.bean.BaseConfig;
import cf.paradoxie.dizzypassword.bean.SortBean;
import cf.paradoxie.dizzypassword.http.Http;
import cf.paradoxie.dizzypassword.utils.AnimationUtil;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 三层数据来源：
 * 获取数据：
 * 1.本地
 * 2.bmob
 * 3.坚果云
 * 初始状态：没有坚果云，首先判断本地，然后拉取bmob：若没有数据，强制设置坚果云；若有数据，则读取bmob缓存下来，然后提示设置坚果云，开始数据转移，最后提示删除bmob数据
 * <p>
 * 升级状态：
 */
public class DataDeal {
    private static DataDeal dataDeal = new DataDeal();
    private static Context context;
    private static List<AccountBean> mBmobBeans;
    List<String> historys = new ArrayList<>();

    private AppConfig appConfig;

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private DataDeal() {
    }

    public static DataDeal getInstance(Context c) {
        if (context == null) {
            context = c;
        }
        return dataDeal;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<AccountBean> loadData() {
        if (loadLocalData().size() == 0) {
            return loadBmobData();
        } else {
            return loadLocalData();
        }
    }

    /**
     * 获取本地数据
     *
     * @return
     */
    private List<AccountBean> loadLocalData() {
        return SPUtils.getDataList("beans", AccountBean.class);
    }

    /**
     * 获取Bmob数据
     *
     * @return
     */
    private List<AccountBean> loadBmobData() {

        mBmobBeans = new ArrayList<>();

        BmobQuery<AccountBean> query = new BmobQuery<>();
        String id = MyApplication.getUser().getObjectId();
        query.addWhereEqualTo("user", id);
        query.findObjects(new FindListener<AccountBean>() {

            @Override
            public void done(List<AccountBean> objects, BmobException e) {
                if (objects != null) {
                    mBmobBeans = objects;
                } else {
                    if (e.getErrorCode() == 9016 && SPUtils.getDataList("beans", AccountBean.class).size() > 1) {
                        MyApplication.showToast(context.getString(R.string.offline));
                    }
                }
            }
        });

        return mBmobBeans;
    }

    /***************版本信息配置*****************/
    public void storeConfig(BaseConfig config) {
        String s = new Gson().toJson(config);
        SPUtils.put("config", s);
    }

    public BaseConfig getConfig() {
        String con = (String) SPUtils.get("config", "");
        if (con.equals("")) {
            return null;
        }
        BaseConfig baseConfig = new Gson().fromJson(con, BaseConfig.class);
        return baseConfig;
    }

    /***************鸡汤语录*****************/
    public void getWordsChicken(Handler handler1) {
        Http.get(Constants.WORDS_ID_CHICKEN + Utils.getData(), s -> {
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

    /***************获取版本信息*****************/
    public void getVersion(FloatingActionButton fb) {
        BmobQuery<BaseConfig> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Constants.CONFIG_ID, new QueryListener<BaseConfig>() {
            @Override
            public void done(BaseConfig baseConfig, BmobException e) {
                if (e == null) {
                    BaseConfig config = baseConfig;
                    final int newVersion = Integer.parseInt(baseConfig.getNewVersion());
                    String title = baseConfig.getTitle();
                    String details = baseConfig.getDetails();

                    if (baseConfig.isIconRotate()) {
                        AnimationUtil.getInstance().startAnim(fb, 7000);
                    }
                    storeConfig(baseConfig);

                    final String toast = baseConfig.getToast();
                    if (newVersion >
                            Integer.parseInt(String.valueOf(SPUtils.get("version", Utils.GetVersion())))
                    ) {//新版本大于本地版本
                        new AlertDialog.Builder(context)
                                .setTitle(title)
                                .setMessage(details)
                                .setCancelable(false)
                                .setPositiveButton("前往", (dialogInterface, i) -> {
                                    //前往酷安
                                    MyApplication.launchAppDetail(MyApplication.getContext().getPackageName(), "com.coolapk.market");
                                })
                                .setNeutralButton("我就不.GIF", null)
                                .setNegativeButton("憋弹了！", (dialogInterface, i) -> {
                                    //当前版本永久关闭，更新云端版本信息
                                    SPUtils.put("version", newVersion);
                                    MyToast.show(context, toast, ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));

                                }).show();
                    }
                } else {
                    Log.e("-----", e.getMessage());
                }
            }
        });
    }



    /***************搜索历史记录*****************/
    public void addHistory(String value) {
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

    public List<String> getHistory() {
        String strings = ((String) SPUtils.get("historyLists", "")).trim();
        historys = java.util.Arrays.asList(strings.split(" "));
        return historys;
    }

    //抽屉拼音填充数据
    public List<SortBean> fillData(String[] data) {
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

}
