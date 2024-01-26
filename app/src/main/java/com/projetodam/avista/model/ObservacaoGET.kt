package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

data class ObservacaoGET (
    @SerializedName("observacao")
    val listaObservacoes: List<Observacao>
)