package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class UtilizadorResponse(
    @SerializedName("utilizador")
    val utilizadorList: List<Utilizador2>
)