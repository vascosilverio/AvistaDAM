package com.example.avista.retrofit.service

import com.example.avista.model.APIResult
import com.example.avista.model.UtilizadorRequest
import com.example.avista.model.UtilizadorResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @GET("utilizador/")
    fun list(): Call<UtilizadorResponse>

    @POST("utilizador/")
    fun addUser(@Body user: UtilizadorRequest): Call<APIResult>
}