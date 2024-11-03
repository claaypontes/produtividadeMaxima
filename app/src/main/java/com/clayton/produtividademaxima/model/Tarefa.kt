package com.clayton.produtividademaxima.model

import java.util.Date

data class Tarefa(
    var tarefa: String = "",
    var id : String = "",
    var descricao: String = "",
    var prioridade: Int = 0,
    var status: Int = 0,  // Suporta o Kanban
    var dataHoraVencimento: Any? = null// Campo para a data e hora de vencimento
) {
    // Construtor vazio necessário para a deserialização do Firestore
    constructor() : this("", "", "", 0,0, Date())
}
