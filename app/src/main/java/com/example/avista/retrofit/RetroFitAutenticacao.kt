package com.example.avista.retrofit

import com.example.avista.model.Utilizador
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetroFitAutenticacao {
    @Headers("Content-Type: application/json", "Authorization: Bearer coiso")
    @GET("aveDam/utilizador")
    fun verificarUtilizador(): Call<List<Utilizador>>

}