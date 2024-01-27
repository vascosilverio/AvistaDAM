package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados da Observação para comunicar com o verbo GET
 */
data class ObservacaoGET (
    @SerializedName("observacao")
    val listaObservacoes: List<Observacao>
)