package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados da Observação para comunicar com o verbo POST
 */
data class ObservacaoPOST (
    @SerializedName("observacao")
    val observacao: Observacao
)