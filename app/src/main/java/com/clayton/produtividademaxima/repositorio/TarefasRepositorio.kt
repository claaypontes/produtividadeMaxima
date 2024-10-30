package com.clayton.produtividademaxima.repositorio

import com.clayton.produtividademaxima.datasource.DataSource
import com.clayton.produtividademaxima.model.Tarefa
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TarefasRepositorio {

    private val dataSource = DataSource()

    // Agora incluindo o status e a data de vencimento ao salvar a tarefa
    fun salvarTarefa(tarefa: String, descricao: String, prioridade: Int, status: Int, dataVencimento: Date) {
        dataSource.salvarTarefa(tarefa, descricao, prioridade, status, dataVencimento)
    }

    // Corrigido: não passamos o userId aqui, pois ele já é obtido no DataSource
    fun recuperarTarefasDoUsuario(): Flow<MutableList<Tarefa>> {
        return dataSource.recuperarTarefasDoUsuario()
    }

    fun deletarTarefa(tarefa: String) {
        dataSource.deletarTarefa(tarefa)
    }

    // Novo método para atualizar o status de uma tarefa
    fun atualizarStatusTarefa(tarefa: Tarefa, novoStatus: Int) {
        dataSource.atualizarStatusTarefa(tarefa, novoStatus)
    }
}
