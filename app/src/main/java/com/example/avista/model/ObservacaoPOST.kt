package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class ObservacaoPOST (
    @SerializedName("observacao")
    val observacao: Observacao
)