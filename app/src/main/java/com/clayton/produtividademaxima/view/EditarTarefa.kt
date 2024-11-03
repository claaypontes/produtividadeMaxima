package com.clayton.produtividademaxima.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.componentes.Botao
import com.clayton.produtividademaxima.componentes.CaixaDeTexto
import com.clayton.produtividademaxima.constantes.Constantes
import com.clayton.produtividademaxima.model.Tarefa
import com.clayton.produtividademaxima.repositorio.TarefasRepositorio
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTarefa(navController: NavController, tarefaId: String) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tarefasRepositorio = TarefasRepositorio()

    var tarefa by remember { mutableStateOf<Tarefa?>(null) }

    var tituloTarefa by remember { mutableStateOf("") }
    var descricaoTarefa by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(Constantes.A_FAZER) }
    var prioridade by remember { mutableStateOf(Constantes.PRIORIDADE_BAIXA) }

    var dataVencimento by remember { mutableStateOf("") }
    var horaVencimento by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateTimeCalendar = Calendar.getInstance()

    // Carrega a tarefa e ajusta para o fuso horário local
    LaunchedEffect(tarefaId) {
        val loadedTarefa = tarefasRepositorio.getTarefaById(tarefaId)
        loadedTarefa?.let {
            tarefa = it
            tituloTarefa = it.tarefa
            descricaoTarefa = it.descricao
            status = it.status
            prioridade = it.prioridade

            // Recupera a data e hora da tarefa
            val dataHoraVencimento = when (val dataHora = it.dataHoraVencimento) {
                is Timestamp -> dataHora.toDate()
                is Date -> dataHora
                else -> null
            }
            dataHoraVencimento?.let { date ->
                dateTimeCalendar.time = date
                dataVencimento = dateFormat.format(date)
                horaVencimento = timeFormat.format(date)
            }
        }
    }

    // Configura os diálogos de data e hora
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dateTimeCalendar.set(year, month, dayOfMonth)
            dataVencimento = dateFormat.format(dateTimeCalendar.time)
        },
        dateTimeCalendar.get(Calendar.YEAR),
        dateTimeCalendar.get(Calendar.MONTH),
        dateTimeCalendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            dateTimeCalendar.set(Calendar.MINUTE, minute)
            horaVencimento = timeFormat.format(dateTimeCalendar.time)
        },
        dateTimeCalendar.get(Calendar.HOUR_OF_DAY),
        dateTimeCalendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Tarefa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            CaixaDeTexto(
                value = tituloTarefa,
                onValueChange = { tituloTarefa = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = "Título da Tarefa",
                maxLines = 1,
                keyboardType = KeyboardType.Text
            )

            CaixaDeTexto(
                value = descricaoTarefa,
                onValueChange = { descricaoTarefa = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp),
                label = "Descrição",
                maxLines = 3,
                keyboardType = KeyboardType.Text
            )

            Text(
                text = "Data de Vencimento:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (dataVencimento.isEmpty()) "Selecionar Data" else dataVencimento,
                    color = PrimaryColor,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { datePickerDialog.show() }) {
                    Text("Alterar", color = Color.Gray)
                }
            }

            Text(
                text = "Hora de Vencimento:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (horaVencimento.isEmpty()) "Selecionar Hora" else horaVencimento,
                    color = PrimaryColor,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { timePickerDialog.show() }) {
                    Text("Alterar", color = Color.Gray)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Status da Tarefa:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = status == Constantes.A_FAZER, onClick = { status = Constantes.A_FAZER })
                    Text(text = "A Fazer", color = MaterialTheme.colorScheme.onBackground)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = status == Constantes.EM_PROGRESSO, onClick = { status = Constantes.EM_PROGRESSO })
                    Text(text = "Em Progresso", color = MaterialTheme.colorScheme.onBackground)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = status == Constantes.CONCLUIDO, onClick = { status = Constantes.CONCLUIDO })
                    Text(text = "Concluído", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Prioridade da Tarefa:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = prioridade == Constantes.PRIORIDADE_BAIXA, onClick = { prioridade = Constantes.PRIORIDADE_BAIXA })
                    Text(text = "Baixa", color = MaterialTheme.colorScheme.onBackground)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = prioridade == Constantes.PRIORIDADE_MEDIA, onClick = { prioridade = Constantes.PRIORIDADE_MEDIA })
                    Text(text = "Média", color = MaterialTheme.colorScheme.onBackground)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = prioridade == Constantes.PRIORIDADE_ALTA, onClick = { prioridade = Constantes.PRIORIDADE_ALTA })
                    Text(text = "Alta", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Botao(
                onClick = {
                    if (tituloTarefa.isEmpty()) {
                        Toast.makeText(context, "Preencha o título da tarefa!", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch(Dispatchers.IO) {
                            tarefa?.let {
                                it.tarefa = tituloTarefa
                                it.descricao = descricaoTarefa
                                it.status = status
                                it.prioridade = prioridade
                                it.dataHoraVencimento = if (dataVencimento.isNotEmpty() && horaVencimento.isNotEmpty()) dateTimeCalendar.time else null
                                tarefasRepositorio.atualizarTarefa(it)

                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(20.dp),
                texto = "Atualizar Tarefa"
            )
        }
    }
}
