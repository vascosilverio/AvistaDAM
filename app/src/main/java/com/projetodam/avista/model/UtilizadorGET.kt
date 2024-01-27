package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados do Utilizador para comunicar com o verbo GET
 */
data class UtilizadorGET(
    @SerializedName("utilizador")
    val listaUtilizadores: List<Utilizador>
)