package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

data class UtilizadorPOST (
    @SerializedName("utilizador")
    val utilizador: Utilizador
)