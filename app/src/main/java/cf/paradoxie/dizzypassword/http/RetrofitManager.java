package cf.paradoxie.dizzypassword.http;


import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;

public class RetrofitManager {

    private Retrofit mRetrofit;
    private OkHttpClient.Builder builder;
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }

    private RetrofitManager() {
        create();
    }

    public static RetrofitManager getInstance() {
        return SingletonHolder.instance;
    }

    //静态内部类
    private static class SingletonHolder {
        private static final RetrofitManager instance = new RetrofitManager();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    private void create() {

        String url = "https://docs.qq.com";

        builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);

        LoggingInterceptor httpLoggingInterceptor = new LoggingInterceptor.Builder()
                .loggable(true)
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .request("请求")
                .response("响应")
                .build();
//        builder.addInterceptor(new HeaderRequestInterceptor());
        builder.addInterceptor(httpLoggingInterceptor);


        mRetrofit = new Retrofit.Builder().baseUrl(url)
                .client(builder.build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                .build();

    }

}
