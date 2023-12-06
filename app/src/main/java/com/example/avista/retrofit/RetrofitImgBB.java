package com.example.avista.retrofit;

import android.content.Context;

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
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}