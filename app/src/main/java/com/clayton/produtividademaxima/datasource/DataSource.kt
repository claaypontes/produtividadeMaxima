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

    // Função para salvar uma tarefa e obter o ID gerado automaticamente pelo Firestore
    fun salvarTarefa(tarefa: String, descricao: String, prioridade: Int, status: Int, dataVencimento: Date) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val tarefaData = hashMapOf(
            "tarefa" to tarefa,
            "descricao" to descricao,
            "status" to status,
            "prioridade" to prioridade,
            "userId" to userId,
            "dataVencimento" to dataVencimento
        )
        // Log para verificar o valor de dataVencimento antes do salvamento
        Log.d("SalvarTarefa", "Data de Vencimento ao salvar: $dataVencimento")

        // Cria o documento com ID automático
        val docRef = db.collection("tarefas").document()
        docRef.set(tarefaData)
            .addOnSuccessListener {
                Log.d("Firebase", "Tarefa salva com sucesso no Firestore com ID: ${docRef.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Erro ao salvar tarefa no Firestore: ${exception.message}")
            }
    }

    // Função para atualizar uma tarefa completa usando o ID do Firestore
    suspend fun atualizarTarefa(tarefa: Tarefa) {
        val tarefaData = mapOf(
            "tarefa" to tarefa.tarefa,
            "descricao" to tarefa.descricao,
            "status" to tarefa.status,
            "prioridade" to tarefa.prioridade,
            "dataVencimento" to tarefa.dataHoraVencimento
        )

        try {
            db.collection("tarefas").document(tarefa.id).update(tarefaData).await()
            Log.d("Firebase", "Tarefa atualizada com sucesso no Firestore.")
        } catch (e: Exception) {
            Log.e("Firebase", "Erro ao atualizar a tarefa: ${e.message}")
        }
    }

    /*// Função para obter uma tarefa específica pelo ID
    suspend fun getTarefaById(tarefaId: String): Tarefa? {
        return try {
            val document = db.collection("tarefas").document(tarefaId).get().await()
            document.toObject(Tarefa::class.java)?.apply {
                id = document.id // Atribui o ID do Firestore ao objeto Tarefa
            }
        } catch (e: Exception) {
            Log.e("DataSource", "Erro ao recuperar tarefa por ID: ${e.message}")
            null
        }
    }*/
//////////////////////////////////////////
    // Função para obter uma tarefa específica pelo ID
    suspend fun getTarefaById(tarefaId: String): Tarefa? {
        return try {
            val document = db.collection("tarefas").document(tarefaId).get().await()
            val tarefa = document.toObject(Tarefa::class.java)?.apply {
                id = document.id // Atribui o ID do Firestore ao objeto Tarefa

                // Log para verificar o valor bruto de dataVencimento
                Log.d("getTarefaById", "Data bruta recebida de dataVencimento: ${document.get("dataVencimento")}")

                // Recupera e converte dataVencimento para Date se necessário
                val dataVencimento = document.get("dataVencimento")
                dataHoraVencimento = when (dataVencimento) {
                    is com.google.firebase.Timestamp -> dataVencimento.toDate()
                    is Date -> dataVencimento
                    else -> null // Define null se não puder converter
                }

                // Log após a conversão
                Log.d("getTarefaById", "Data convertida de dataHoraVencimento: $dataHoraVencimento")
            }
            tarefa
        } catch (e: Exception) {
            Log.e("DataSource", "Erro ao recuperar tarefa por ID: ${e.message}")
            null
        }
    }

    /////////////////////////////////////




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
                tarefa.id = documento.id // Atribui o ID do documento ao campo `id` da tarefa
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

    // Função para deletar uma tarefa usando o ID do Firestore
    fun deletarTarefa(tarefaId: String) {
        db.collection("tarefas").document(tarefaId).delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Tarefa deletada com sucesso.")
            } else {
                Log.e("Firebase", "Erro ao deletar tarefa", task.exception)
            }
        }
    }

    // Função para atualizar o status de uma tarefa pelo ID
    fun atualizarStatusTarefa(tarefa: Tarefa, novoStatus: Int) {
        db.collection("tarefas").document(tarefa.id).update("status", novoStatus)
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
