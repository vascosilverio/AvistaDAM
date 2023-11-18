package com.example.avista.retrofit.service

import com.example.avista.model.APIResult
import com.example.avista.model.Utilizador2
import com.example.avista.model.UtilizadorResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {
    @GET("utilizador/")
    fun list(@Header("Authorization") authorization: String): Call<UtilizadorResponse>

    @FormUrlEncoded
    @POST("utilizador")
    fun addUser(@Field("userId") title: String?,
                @Field("password") description: String?): Call<APIResult>
}