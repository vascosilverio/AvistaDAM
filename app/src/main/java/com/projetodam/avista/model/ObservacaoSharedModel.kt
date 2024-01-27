package com.projetodam.avista.model

import androidx.lifecycle.ViewModel

/*
* SharedView Model da lista de observações e lista filtrada de observações para acesso entre atividades
 */
class ObservacaoSharedModel : ViewModel() {
    var listaObservacoes: ArrayList<Observacao>? = null
    var listaObservacoesFiltro: ArrayList<Observacao>? = ArrayList<Observacao>()
}