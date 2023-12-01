package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class Utilizador(
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("password")
    val password: String?
)