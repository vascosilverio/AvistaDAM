package com.example.avista.model

import androidx.lifecycle.ViewModel

class ObservacaoSharedModel : ViewModel() {
    var listaObservacoes: ArrayList<Observacao>? = null
    var listaObservacoesFiltro: ArrayList<Observacao>? = ArrayList<Observacao>()
}