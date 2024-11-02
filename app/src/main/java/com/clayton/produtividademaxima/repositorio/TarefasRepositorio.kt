package com.clayton.produtividademaxima.repositorio

import com.clayton.produtividademaxima.datasource.DataSource
import com.clayton.produtividademaxima.model.Tarefa
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TarefasRepositorio {

    private val dataSource = DataSource()

    // Função para salvar a tarefa com prioridade, status e data de vencimento
    fun salvarTarefa(tarefa: String, descricao: String, prioridade: Int, status: Int, dataVencimento: Date) {
        dataSource.salvarTarefa(tarefa, descricao, prioridade, status, dataVencimento)
    }

    // Função para recuperar todas as tarefas do usuário autenticado
    fun recuperarTarefasDoUsuario(): Flow<MutableList<Tarefa>> {
        return dataSource.recuperarTarefasDoUsuario()
    }

    // Função para deletar uma tarefa pelo nome
    fun deletarTarefa(tarefa: String) {
        dataSource.deletarTarefa(tarefa)
    }

    // Função para atualizar o status de uma tarefa
    fun atualizarStatusTarefa(tarefa: Tarefa, novoStatus: Int) {
        dataSource.atualizarStatusTarefa(tarefa, novoStatus)
    }

    // Função para obter uma tarefa específica pelo ID
    suspend fun getTarefaById(tarefaId: String): Tarefa? {
        return dataSource.getTarefaById(tarefaId)
    }

    // Função para atualizar uma tarefa completa (nome, descrição, prioridade, data de vencimento, etc.)
    suspend fun atualizarTarefa(tarefa: Tarefa) {
        dataSource.atualizarTarefa(tarefa)
    }
}
