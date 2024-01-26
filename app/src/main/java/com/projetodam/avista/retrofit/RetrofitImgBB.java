package com.projetodam.avista.retrofit;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// código com recurso ao artigo do StackOverflow: https://stackoverflow.com/questions/59252255/how-to-upload-photo-via-retrofit-from-android-device
public class RetrofitImgBB {

    public static final String KEY_API="1243f229ab03f8e16387eb2679e8a005";
    private static final String BASE_URL = "https://api.imgbb.com/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(
                            new OkHttpClient.Builder()
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .writeTimeout(30, TimeUnit.SECONDS)
                                    .build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}