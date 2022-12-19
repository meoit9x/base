package com.example.base.retrofit;

import android.content.Context;

import com.example.base.App;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create by Luyenphong
 * luyenphong00@gmail.com
 */
public class RetrofitClient {

    private static Retrofit ourInstance;

    public static OkHttpClient okHttpClient(Context context, long time) {
        Interceptor interceptor;

        interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(time, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(time, TimeUnit.SECONDS)
                .writeTimeout(time, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    public static Retrofit getInstance(String url) {
        ourInstance = new Retrofit.Builder()
                .client(okHttpClient(App.getInstance(), 30))
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return ourInstance;
    }

    private RetrofitClient() {
    }

    public static void setOurInstance(Retrofit ourInstance) {
        RetrofitClient.ourInstance = ourInstance;
    }

}
