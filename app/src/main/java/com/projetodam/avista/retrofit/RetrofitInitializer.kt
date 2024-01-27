package com.projetodam.avista.retrofit

import android.util.Log
import com.projetodam.avista.retrofit.service.ServicoAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
* Classe usada para declarar o Retrofit usado para a API do Sheety
 */
class RetrofitInitializer {

    private val gson: Gson = GsonBuilder().setLenient().create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sheety.co/2152af94e832d46a9f2534ae4e2d9073/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(buildOkHttpClient())
        .build()

    /*
    * função usada para construir o cliente HTTP com o header definido com o Bearer Token necessário para a
    * autenticação na API do Sheety
     */
    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer secretKey")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }
    fun servicoAPI(): ServicoAPI = retrofit.create(ServicoAPI::class.java)
}