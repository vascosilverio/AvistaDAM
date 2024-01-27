package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados da Observação para comunicar com o verbo PUT
 */
data class ObservacaoPUT (
    @SerializedName("observacao")
    val observacao: Observacao
)