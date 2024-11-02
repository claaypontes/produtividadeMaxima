package com.clayton.produtividademaxima.datasource

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.clayton.produtividademaxima.MainActivity
import com.clayton.produtividademaxima.R
import com.clayton.produtividademaxima.constantes.Constantes
import com.clayton.produtividademaxima.model.Tarefa
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class TarefaNotifierWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d("TarefaNotifierWorker", "Usuário não autenticado. Worker encerrado.")
            return Result.failure()
        }

        Log.d("TarefaNotifierWorker", "Usuário autenticado com UID: ${user.uid}")

        // Configura o canal de notificação
        createNotificationChannel()

        // Recupera as tarefas diretamente do Firestore
        val tarefas = recuperarTarefasDiretamente(user.uid)
        val dataAtual = Date()

        Log.d("TarefaNotifierWorker", "Data e hora atual: $dataAtual")
        Log.d("TarefaNotifierWorker", "Número de tarefas recuperadas: ${tarefas.size}")

        // Filtra as tarefas vencidas
        val tarefasVencidas = tarefas.filter { tarefa ->
            val dataVencimento = when (val vencimento = tarefa.dataHoraVencimento) {
                is Timestamp -> vencimento.toDate()
                is Date -> vencimento
                else -> null
            }
            dataVencimento?.before(dataAtual) == true && tarefa.status != Constantes.CONCLUIDO
        }

        Log.d("TarefaNotifierWorker", "Número de tarefas vencidas encontradas: ${tarefasVencidas.size}")

        // Envia a notificação se houver tarefas vencidas
        if (tarefasVencidas.isNotEmpty()) {
            Log.d("TarefaNotifierWorker", "Enviando notificação para ${tarefasVencidas.size} tarefas vencidas.")
            sendNotification(tarefasVencidas.size)
        } else {
            Log.d("TarefaNotifierWorker", "Nenhuma tarefa vencida encontrada.")
        }

        return Result.success()
    }

    private suspend fun recuperarTarefasDiretamente(userId: String): List<Tarefa> {
        return try {
            val snapshot = db.collection("tarefas")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val tarefas = snapshot.documents.mapNotNull { document ->
                val tarefa = document.toObject(Tarefa::class.java)
                if (tarefa != null) {
                    // Converter `dataVencimento` para Date se for Timestamp
                    val dataVencimento = document.get("dataVencimento")
                    tarefa.dataHoraVencimento = when (dataVencimento) {
                        is Timestamp -> dataVencimento.toDate()
                        is Date -> dataVencimento
                        else -> Date() // Define uma data padrão em caso de erro
                    }
                }
                tarefa
            }
            Log.d("TarefaNotifierWorker", "Tarefas recuperadas diretamente: ${tarefas.size}")
            tarefas
        } catch (e: Exception) {
            Log.e("TarefaNotifierWorker", "Erro ao recuperar tarefas: ${e.message}")
            emptyList()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tarefas Vencidas"
            val descriptionText = "Notificações para tarefas vencidas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("tarefas_vencidas", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(count: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TarefaNotifierWorker", "Permissão de notificação não concedida.")
            return
        }

        // Intent para abrir a MainActivity ao clicar na notificação
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Configuração do PendingIntent para abrir o app ao clicar na notificação
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configuração da notificação com o PendingIntent
        val builder = NotificationCompat.Builder(context, "tarefas_vencidas")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Coloque um ícone relevante
            .setContentTitle("Tarefas Vencidas")
            .setContentText("Você tem $count tarefa(s) vencida(s).")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Define o PendingIntent para a notificação
            .setAutoCancel(true) // A notificação será removida quando o usuário clicar nela

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }

        Log.d("TarefaNotifierWorker", "Notificação enviada para $count tarefa(s) vencida(s).")
    }
}
