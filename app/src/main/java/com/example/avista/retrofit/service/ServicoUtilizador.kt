package com.example.avista.retrofit.service

import com.example.avista.model.RespostaAPI
import com.example.avista.model.UtilizadorGET
import com.example.avista.model.UtilizadorPOST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServicoUtilizador {
    @GET("utilizador/")
    fun listarUtilizadores(): Call<UtilizadorGET>

    @POST("utilizador/")
    fun adicionarUtilizador(@Body user: UtilizadorPOST): Call<RespostaAPI>
}