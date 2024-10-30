package com.clayton.produtividademaxima.model

import java.util.Date

data class Tarefa(
    var tarefa: String = "",
    var descricao: String = "",
    var prioridade: Int = 0,
    var status: Int = 0,  // Suporta o Kanban
    var dataHoraVencimento: Date = Date()  // Campo para a data e hora de vencimento
) {
    val id: String
        get() {
            TODO()
        }

    // Construtor vazio necessário para a deserialização do Firestore
    constructor() : this("", "", 0, 0, Date())
}
