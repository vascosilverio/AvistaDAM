package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class Utilizador2 (
    @SerializedName("userId") val userName: String?,
    @SerializedName("password") val password: String?)