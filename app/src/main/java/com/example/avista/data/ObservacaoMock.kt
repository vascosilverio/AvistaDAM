package com.example.avista.data

import com.example.avista.model.Observacao

class ObservacaoMock {
    var listaObservacoes = ArrayList<Observacao>()

    init {
        for (i in 0..10){
            listaObservacoes.add(
                Observacao(
                i.toString(),
                "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",
                i.toString(), i.toString(), i.toDouble(), i.toDouble(), i.toString(),)
            )
        }
    }
}