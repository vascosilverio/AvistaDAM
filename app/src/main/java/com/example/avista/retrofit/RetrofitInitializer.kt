package com.example.avista.retrofit

import android.util.Log
import com.example.avista.retrofit.service.ServicoAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sheety.co/d94a99414cb040e846785cf0eb8fa23c/avistaApi/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(buildOkHttpClient())
        .build()

    private fun buildOkHttpClient(): OkHttpClient {

        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer secretKey")
                .build()
            Log.d("RetrofitInitializer", "Corpo do pedido à API: ${request.body()?.toString()}")
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun servicoAPI(): ServicoAPI = retrofit.create(ServicoAPI::class.java)
}