package cf.paradoxie.dizzypassword.http;

import android.util.Log;



import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.base.Constans;
import cf.paradoxie.dizzypassword.bean.AppConfig;
import cf.paradoxie.dizzypassword.bean.CommonEntity;
import cf.paradoxie.dizzypassword.utils.Base64Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
     * APP配置信息
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


                        httpListener.success(new AppConfig(version_code, appDownLoadUrl, buyUrl, avatarUrl, maxim, witty));
                    } catch (Exception e) {
                        Log.e("app配置信息出错了", e.toString());
                        httpListener.success(new AppConfig(1, "", "https://buy.tantuk.cn/",
                                "http://cdn-hw-static.shanhutech.cn/bizhi/staticwp/202208/4ef2da444cc9f23a36df8a8f22cb9edb--1348394357.jpg",
                                "幽默是藏身于笑话之后的严肃。", "世上无难事，只要肯放弃"));
                    }
//                    JsonObject obj = g.fromJson(string, JsonObject.class);
                }
            }
        });
    }

    /**
     * APP配置信息
     */
    public void getAds(HttpListener<List<CommonEntity>> httpListener) {

        request = new Request.Builder().url(Constans.AD).build();
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
                        Log.e("推广信息json", string);
                        JsonObject jobj = new Gson().fromJson(string, JsonObject.class);
                        String result = jobj.get("data").toString();
                        List<CommonEntity> adBeans = new Gson().fromJson(result, new TypeToken<List<CommonEntity>>() {
                        }.getType());
                        Log.e("推广信息", adBeans.toString());
                        httpListener.success(adBeans);
                    } catch (Exception e) {
                        httpListener.success(new ArrayList<>());
                    }
//                    JsonObject obj = g.fromJson(string, JsonObject.class);
                }
            }
        });
    }

}
