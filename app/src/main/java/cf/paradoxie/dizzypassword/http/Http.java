package cf.paradoxie.dizzypassword.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Http {
   public interface ResponseListener {
        void callback(String res);
    }

    public static void get(final String url, ResponseListener listener) {

        //准备网络请求
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();


        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                listener.callback(response.body().string());

            }
        });


    }
}
