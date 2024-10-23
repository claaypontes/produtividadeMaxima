package com.clayton.produtividademaxima.repositorio

import com.clayton.produtividademaxima.datasource.DataSource

class TarefasRepositorio() {

    private val dataSource = DataSource()

    fun salvarTarefa(tarefa: String, descricao: String, prioridade: Int){
        dataSource.salvarTarefa(tarefa,descricao,prioridade)


    }

}