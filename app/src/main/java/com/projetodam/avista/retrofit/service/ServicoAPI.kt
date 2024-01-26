package com.projetodam.avista.retrofit.service

import com.projetodam.avista.model.ObservacaoGET
import com.projetodam.avista.model.ObservacaoPOST
import com.projetodam.avista.model.ObservacaoPUT
import com.projetodam.avista.model.RespostaAPI
import com.projetodam.avista.model.UtilizadorGET
import com.projetodam.avista.model.UtilizadorPOST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServicoAPI {
    @GET("utilizador/")
    fun listarUtilizadores(): Call<UtilizadorGET>

    @POST("utilizador/")
    fun adicionarUtilizador(@Body user: UtilizadorPOST): Call<RespostaAPI>

    @DELETE("utilizador/{id}")
    fun removerUtilizador(@Path("id") id: String): Call<RespostaAPI>

    @GET("observacao/")
    fun listarObservacoes(): Call<ObservacaoGET>

    @POST("observacao/")
    fun adicionarObservacao(@Body user: ObservacaoPOST): Call<RespostaAPI>

    @PUT("observacao/{id}")
    fun editarObservacao(@Path("id") id: String, @Body observacao: ObservacaoPUT): Call<RespostaAPI>

    @DELETE("observacao/{id}")
    fun removerObservacao(@Path("id") id: String): Call<RespostaAPI>
}