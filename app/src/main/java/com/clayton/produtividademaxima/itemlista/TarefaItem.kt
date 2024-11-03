package com.clayton.produtividademaxima.itemlista

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.constantes.Constantes
import com.clayton.produtividademaxima.model.Tarefa
import com.clayton.produtividademaxima.repositorio.TarefasRepositorio
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefaItem(
    tarefa: Tarefa,
    context: Context,
    navController: NavController,
    onTaskStatusChanged: (Tarefa, Int) -> Unit,
    atualizarLista: () -> Unit
) {
    val tarefasRepositorio = TarefasRepositorio()
    var showStatusDialog by remember { mutableStateOf(false) }

    // Formata a data de vencimento
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dataVencimentoTexto = dateFormat.format(tarefa.dataHoraVencimento)

    // Verifica se a tarefa está vencida considerando Timestamp e Date
    val dataAtual = Date()
    val dataVencimento = when (val vencimento = tarefa.dataHoraVencimento) {
        is Timestamp -> vencimento.toDate()
        is Date -> vencimento
        else -> null
    }
    val tarefaVencida = dataVencimento?.before(dataAtual) == true && tarefa.status != Constantes.CONCLUIDO

    // Define o estilo de texto com riscado se a tarefa estiver concluída
    val textoEstilo = if (tarefa.status == Constantes.CONCLUIDO) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { showStatusDialog = true })
            }
            .border(2.dp, if (tarefaVencida) Color.Red else Color.Transparent, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = if (tarefa.status == Constantes.CONCLUIDO) 0.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Centraliza o badge "Vencida" no topo se a tarefa estiver vencida
            if (tarefaVencida) {
                AssistChip(
                    onClick = { /* Ação opcional */ },
                    label = { Text("Vencida") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Red,
                        labelColor = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp)) // Espaço abaixo do badge
            }

            // Linha superior com título e botão de edição
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tarefa.tarefa,
                    style = MaterialTheme.typography.titleLarge.copy( // Aumenta o tamanho da fonte para titleLarge
                        fontWeight = FontWeight.Bold,
                        textDecoration = textoEstilo,
                        color = if (tarefaVencida) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descrição da tarefa
            Text(
                text = tarefa.descricao,
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = textoEstilo),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = if (showStatusDialog) Int.MAX_VALUE else 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rodapé com prioridade e data de vencimento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prioridade
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val priorityColor = when (tarefa.prioridade) {
                        Constantes.PRIORIDADE_BAIXA -> Color(0xFF4CAF50)
                        Constantes.PRIORIDADE_MEDIA -> Color(0xFFFFC107)
                        Constantes.PRIORIDADE_ALTA -> Color(0xFFF44336)
                        else -> Color.Gray
                    }
                    Icon(
                        imageVector = Icons.Filled.PriorityHigh,
                        contentDescription = "Prioridade",
                        tint = priorityColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (tarefa.prioridade) {
                            Constantes.PRIORIDADE_BAIXA -> "Baixa"
                            Constantes.PRIORIDADE_MEDIA -> "Média"
                            Constantes.PRIORIDADE_ALTA -> "Alta"
                            else -> "Sem Prioridade"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = textoEstilo),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Data de Vencimento
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Data de Vencimento",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dataVencimentoTexto,
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = textoEstilo),
                        color = if (tarefaVencida) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text(text = "Alterar Tarefa") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selecione o novo status da tarefa:")
                    Spacer(modifier = Modifier.height(12.dp))
                    ElevatedButton(
                        onClick = {
                            updateStatus(
                                context,
                                tarefasRepositorio,
                                tarefa,
                                Constantes.A_FAZER,
                                onTaskStatusChanged,
                                atualizarLista
                            )
                            showStatusDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("A Fazer")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ElevatedButton(
                        onClick = {
                            updateStatus(
                                context,
                                tarefasRepositorio,
                                tarefa,
                                Constantes.EM_PROGRESSO,
                                onTaskStatusChanged,
                                atualizarLista
                            )
                            showStatusDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Em Progresso")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ElevatedButton(
                        onClick = {
                            updateStatus(
                                context,
                                tarefasRepositorio,
                                tarefa,
                                Constantes.CONCLUIDO,
                                onTaskStatusChanged,
                                atualizarLista
                            )
                            showStatusDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Concluído")
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Divider()

                    // Botão de Editar Tarefa
                    TextButton(
                        onClick = {
                            navController.navigate("editarTarefa/${tarefa.id}") // Passa o ID da tarefa para a tela de edição
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Editar Tarefa")
                    }


                    // Botão de Deletar Tarefa
                    TextButton(
                        onClick = {
                            tarefasRepositorio.deletarTarefa(tarefa.id) // Deleta a tarefa pelo ID
                            Toast.makeText(context, "Tarefa deletada com sucesso", Toast.LENGTH_SHORT).show()
                            atualizarLista()
                            showStatusDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Deletar Tarefa")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Função para atualizar o status da tarefa
private fun updateStatus(
    context: Context,
    tarefasRepositorio: TarefasRepositorio,
    tarefa: Tarefa,
    novoStatus: Int,
    onTaskStatusChanged: (Tarefa, Int) -> Unit,
    atualizarLista: () -> Unit
) {
    onTaskStatusChanged(tarefa, novoStatus)
    tarefa.status = novoStatus
    tarefasRepositorio.atualizarStatusTarefa(tarefa, novoStatus)
    Toast.makeText(
        context,
        "Status atualizado para ${statusToString(novoStatus)}",
        Toast.LENGTH_SHORT
    ).show()
    atualizarLista()
}

// Converte o status para uma string para exibição
fun statusToString(status: Int): String {
    return when (status) {
        Constantes.A_FAZER -> "A Fazer"
        Constantes.EM_PROGRESSO -> "Em Progresso"
        Constantes.CONCLUIDO -> "Concluído"
        else -> "Desconhecido"
    }
}
