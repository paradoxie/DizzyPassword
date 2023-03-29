package cf.paradoxie.dizzypassword.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cf.paradoxie.dizzypassword.base.Constans;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.bean.AppConfig;
import cf.paradoxie.dizzypassword.utils.Base64Util;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

public class HttpUtils {
    private static HttpUtils httpUtil = new HttpUtils();
    private static HttpApi mHttpApi;
    private static Retrofit retrofit;
    private Gson g = new Gson();
    private static OkHttpClient okHttpClient;
    private Request request;
    private Call call;

    public static HttpUtils getInstance() {
        if (retrofit == null) {
            retrofit = RetrofitManager.getInstance().getRetrofit();
        }
        if (mHttpApi == null) {
            mHttpApi = retrofit.create(HttpApi.class);
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return httpUtil;
    }

    private HttpUtils() {
    }

    /**
     * 大淘客数据
     * 京东 获取转链后数据
     */
    public void getAppConfigUrl(HttpListener<AppConfig> httpListener) {

        request = new Request.Builder().url(Constans.APP_CONFIG_URL).build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpListener.failed();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String string = response.body().string();
                        string = string.split("ssssssssss")[1];
                        string = Base64Util.decodeBase64(string);
                        Log.e("app配置信息", string);
                        int version_code = Integer.parseInt(string.split("&&&")[0]);
                        String url = string.split("&&&")[1];
                        String appDownLoadUrl = string.split("&&&")[1];
                        String buyUrl = string.split("&&&")[2];
                        String avatarUrl = string.split("&&&")[3];
                        String maxim = string.split("&&&")[4];
                        String witty = string.split("&&&")[5];


                        httpListener.success(new AppConfig(version_code, appDownLoadUrl, buyUrl, avatarUrl, maxim,witty));
                    } catch (Exception e) {
                        Log.e("app配置信息出错了", e.toString());
                        httpListener.success(new AppConfig(1, "", "https://buy.tantuk.cn/",
                                "http://cdn-hw-static.shanhutech.cn/bizhi/staticwp/202208/4ef2da444cc9f23a36df8a8f22cb9edb--1348394357.jpg",
                                "幽默是藏身于笑话之后的严肃。","世上无难事，只要肯放弃"));
                    }
//                    JsonObject obj = g.fromJson(string, JsonObject.class);
                }
            }
        });
    }

}
