package com.example.avista.retrofit

import com.example.avista.model.Utilizador
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetroFitInitializer {

    @Headers("Content-Type: application/json", "Authorization: Bearer coiso")
    @POST("/6c966be3aa95146fdfd60b287a41e909/aveDam/utilizador")
    fun criarUtilizador(@Body utilizador: Utilizador): Call<Void>
}