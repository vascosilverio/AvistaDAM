package com.example.avista.retrofit.service

import com.example.avista.model.ObservacaoGET
import com.example.avista.model.ObservacaoPOST
import com.example.avista.model.RespostaAPI
import com.example.avista.model.UtilizadorGET
import com.example.avista.model.UtilizadorPOST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServicoAPI {
    @GET("utilizador/")
    fun listarUtilizadores(): Call<UtilizadorGET>

    @POST("utilizador/")
    fun adicionarUtilizador(@Body user: UtilizadorPOST): Call<RespostaAPI>

    @GET("observacao/")
    fun listarObservacoes(): Call<ObservacaoGET>

    @POST("observacao/")
    fun adicionarObservacao(@Body user: ObservacaoPOST): Call<RespostaAPI>

    @DELETE("observacao/{id}")
    fun removerObservacao(@Path("id") id: String): Call<RespostaAPI>
}