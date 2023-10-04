package com.c2.arenafinder.api.retrofit;

import androidx.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.c2.arenafinder.data.local.LogApp;
import com.c2.arenafinder.data.local.LogTag;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

//    public static final String BASE_URL = "http://192.168.155.152/arenafinder-web/mobile/"; // Local
    public static final String BASE_URL = "http://172.16.106.119/arenafinder-web/mobile/"; // Lab Mobile
//      public static final String BASE_URL = "http://172.16.106.186/arenafinder-web/mobile/"; // Lab KSI
//      public static final String BASE_URL = "http://172.16.103.152/arenafinder-web/mobile/"; // Lab MMC
//      public static final String BASE_URL = "http://172.16.104.101/arenafinder-web/mobile/"; // Classroom A

    public static final String SUCCESSFUL_RESPONSE = "success";

    @NonNull
    public static RetrofitEndPoint getInstance(){
        return getConnection().create(RetrofitEndPoint.class);
    }

    /**
     * connect to the rest server
     */
    public static Retrofit getConnection() {
        LogApp.info(RetrofitClient.class, LogTag.RETROFIT_CONNECTION, "create connection to " + BASE_URL);
        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

}
