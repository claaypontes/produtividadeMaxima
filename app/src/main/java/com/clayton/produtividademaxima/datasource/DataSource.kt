package com.clayton.produtividademaxima.datasource

import android.util.Log
import com.clayton.produtividademaxima.model.Tarefa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class DataSource {

    private val db = FirebaseFirestore.getInstance()
    private val _todastarefas = MutableStateFlow<MutableList<Tarefa>>(mutableListOf())
    val todastarefas: StateFlow<MutableList<Tarefa>> = _todastarefas

    // Função para salvar a tarefa associada ao userId e status, agora incluindo a data de vencimento como Date
    fun salvarTarefa(tarefa: String, descricao: String, prioridade: Int, status: Int, dataVencimento: Date) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val tarefaMap = hashMapOf(
            "tarefa" to tarefa,
            "descricao" to descricao,
            "status" to status,
            "prioridade" to prioridade,
            "userId" to userId,
            "dataVencimento" to dataVencimento
        )

        db.collection("tarefas")
            .document(tarefa)
            .set(tarefaMap)
            .addOnSuccessListener {
                Log.d("Firebase", "Tarefa salva com sucesso no Firestore.")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Erro ao salvar tarefa no Firestore: ${exception.message}")
            }
    }
    // Função para atualizar uma tarefa completa (nome, descrição, prioridade)
    suspend fun atualizarTarefa(tarefa: Tarefa) {
        val tarefaMap = mapOf(
            "tarefa" to tarefa.tarefa,
            "descricao" to tarefa.descricao,
            "status" to tarefa.status,
            "prioridade" to tarefa.prioridade,
            "dataVencimento" to tarefa.dataHoraVencimento
        )
        try {
            db.collection("tarefas").document(tarefa.tarefa).update(tarefaMap).await()
            Log.d("Firebase", "Tarefa atualizada com sucesso no Firestore.")
        } catch (e: Exception) {
            Log.e("Firebase", "Erro ao atualizar a tarefa: ${e.message}")
        }
    }
    // k
    suspend fun getTarefaById(tarefaId: String): Tarefa? {
        return try {
            val document = db.collection("tarefas").document(tarefaId).get().await()
            document.toObject(Tarefa::class.java)
        } catch (e: Exception) {
            Log.e("DataSource", "Erro ao recuperar tarefa por ID: ${e.message}")
            null
        }
    }

    // Função para recuperar as tarefas em tempo real do usuário autenticado
    fun recuperarTarefasDoUsuario(): Flow<MutableList<Tarefa>> {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            db.collection("tarefas")
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("Firebase", "Erro ao ouvir mudanças nas tarefas: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        atualizarListaTarefas(snapshot)
                    }
                }
        } else {
            Log.e("Firebase", "Usuário não autenticado.")
        }
        return todastarefas
    }

    // Função auxiliar para atualizar a lista de tarefas no fluxo
    private fun atualizarListaTarefas(snapshot: QuerySnapshot) {
        val listaAtualizada = mutableListOf<Tarefa>()
        for (documento in snapshot.documents) {
            val tarefa = documento.toObject(Tarefa::class.java)
            if (tarefa != null) {
                // Verifica se `dataVencimento` é Timestamp ou Date e converte para Date conforme necessário
                val dataVencimento = documento.get("dataVencimento")
                tarefa.dataHoraVencimento = when (dataVencimento) {
                    is com.google.firebase.Timestamp -> dataVencimento.toDate()
                    is Date -> dataVencimento
                    else -> Date() // Define uma data padrão caso não esteja presente ou seja de tipo desconhecido
                }
                listaAtualizada.add(tarefa)
            }
        }
        _todastarefas.value = listaAtualizada // Força uma nova referência para disparar a atualização
    }

    // Função para deletar uma tarefa
    fun deletarTarefa(tarefa: String) {
        db.collection("tarefas").document(tarefa).delete().addOnCompleteListener {
            Log.d("Firebase", "Tarefa deletada com sucesso.")
        }
    }

    // Função para atualizar o status de uma tarefa com atualização no fluxo
    fun atualizarStatusTarefa(tarefa: Tarefa, novoStatus: Int) {
        val tarefaRef = db.collection("tarefas").document(tarefa.tarefa)
        tarefaRef.update("status", novoStatus)
            .addOnSuccessListener {
                Log.d("Firebase", "Status da tarefa atualizado com sucesso.")
                db.collection("tarefas")
                    .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("Firebase", "Erro ao ouvir mudanças nas tarefas: ${error.message}")
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            atualizarListaTarefas(snapshot)
                        }
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Erro ao atualizar status da tarefa: ${exception.message}")
            }
    }
}
