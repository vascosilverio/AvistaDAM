package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados do Utilizador para comunicar com o verbo POST
 */
data class UtilizadorPOST (
    @SerializedName("utilizador")
    val utilizador: Utilizador
)