package com.example.avista.retrofit

import com.example.avista.retrofit.service.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer2 {

    private val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sheety.co/6c966be3aa95146fdfd60b287a41e909/aveDam/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun userService() = retrofit.create(UserService::class.java)
}