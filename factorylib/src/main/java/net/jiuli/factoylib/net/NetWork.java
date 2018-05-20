package net.jiuli.factoylib.net;

import android.text.TextUtils;

import net.jiuli.common.Common;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.persistence.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jiuli on 17-9-14.
 */

public class NetWork {

    private static NetWork instance;

    private Retrofit retrofit;

    static {
        instance = new NetWork();
    }

    private NetWork() {

    }


    public static Retrofit getRetrofit() {
        if (instance.retrofit != null) {
            return instance.retrofit;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request.Builder builder = originalRequest.newBuilder();
                        if (!TextUtils.isEmpty(Account.getToken())) {
                            builder.addHeader("token", Account.getToken());
                        }
                        builder.addHeader("Content-Type", "application/json");
                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                }).build();

        return instance.retrofit = new Retrofit.Builder()
                .baseUrl(Common.Constance.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
    }


    public static RemoteService remote() {
        return NetWork.getRetrofit().create(RemoteService.class);
    }
}
