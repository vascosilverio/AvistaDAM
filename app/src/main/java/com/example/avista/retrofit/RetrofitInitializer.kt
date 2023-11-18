package com.example.avista.retrofit

import android.util.Log
import com.example.avista.retrofit.service.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sheety.co/6c966be3aa95146fdfd60b287a41e909/aveDam/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(buildOkHttpClient())
        .build()

    private fun buildOkHttpClient(): OkHttpClient {

        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer coiso")
                .build()
            Log.d("RetrofitInitializer", "Corpo da solicitação: ${request.body()?.toString()}")
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun userService(): UserService = retrofit.create(UserService::class.java)
}