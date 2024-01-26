package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

data class UtilizadorGET(
    @SerializedName("utilizador")
    val listaUtilizadores: List<Utilizador>
)