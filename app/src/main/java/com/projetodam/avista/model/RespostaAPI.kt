package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

data class RespostaAPI (
    @SerializedName("code") val code: String?,
    @SerializedName("description") val description: String?
)