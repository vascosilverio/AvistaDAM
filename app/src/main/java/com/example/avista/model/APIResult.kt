package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class APIResult (
    @SerializedName("code") val code: String?,
    @SerializedName("description") val description: String?
)