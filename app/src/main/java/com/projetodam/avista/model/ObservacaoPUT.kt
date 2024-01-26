package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

data class ObservacaoPUT (
    @SerializedName("observacao")
    val observacao: Observacao
)