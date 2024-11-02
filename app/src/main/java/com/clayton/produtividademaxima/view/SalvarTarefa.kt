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
import com.clayton.produtividademaxima.repositorio.TarefasRepositorio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalvarTarefa(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tarefasRepositorio = TarefasRepositorio()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Adicionar Tarefa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
    ) { paddingValues ->

        var tituloTarefa by remember { mutableStateOf("") }
        var descricaoTarefa by remember { mutableStateOf("") }
        var status by remember { mutableStateOf(Constantes.A_FAZER) }
        var prioridade by remember { mutableStateOf(Constantes.PRIORIDADE_BAIXA) }

        var dataVencimento by remember { mutableStateOf("") }
        var horaVencimento by remember { mutableStateOf("") }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Date Picker dialog
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dataVencimento = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Time Picker dialog
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                horaVencimento = timeFormat.format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        val textColor = MaterialTheme.colorScheme.onBackground

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
                color = textColor,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = if (dataVencimento.isEmpty()) "Selecionar Data" else dataVencimento, color = PrimaryColor)
            }

            Text(
                text = "Hora de Vencimento:",
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            OutlinedButton(
                onClick = { timePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = if (horaVencimento.isEmpty()) "Selecionar Hora" else horaVencimento, color = PrimaryColor)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Status da Tarefa:", fontWeight = FontWeight.Bold, color = textColor)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = status == Constantes.A_FAZER, onClick = { status = Constantes.A_FAZER })
                    Text(text = "A Fazer", color = textColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = status == Constantes.EM_PROGRESSO, onClick = { status = Constantes.EM_PROGRESSO })
                    Text(text = "Em Progresso", color = textColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = status == Constantes.CONCLUIDO, onClick = { status = Constantes.CONCLUIDO })
                    Text(text = "Concluído", color = textColor)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Prioridade da Tarefa:", fontWeight = FontWeight.Bold, color = textColor)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = prioridade == Constantes.PRIORIDADE_BAIXA, onClick = { prioridade = Constantes.PRIORIDADE_BAIXA })
                    Text(text = "Baixa", color = textColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = prioridade == Constantes.PRIORIDADE_MEDIA, onClick = { prioridade = Constantes.PRIORIDADE_MEDIA })
                    Text(text = "Média", color = textColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = prioridade == Constantes.PRIORIDADE_ALTA, onClick = { prioridade = Constantes.PRIORIDADE_ALTA })
                    Text(text = "Alta", color = textColor)
                }
            }

            Botao(
                onClick = {
                    if (tituloTarefa.isEmpty() || dataVencimento.isEmpty() || horaVencimento.isEmpty()) {
                        Toast.makeText(context, "Preencha o título, data e hora de vencimento!", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch(Dispatchers.IO) {
                            val dataHoraVencimentoTimestamp = calendar.time
                            tarefasRepositorio.salvarTarefa(
                                tituloTarefa,
                                descricaoTarefa,
                                prioridade,
                                status,
                                dataHoraVencimentoTimestamp
                            )
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Sucesso ao salvar a tarefa!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(20.dp),
                texto = "Salvar Tarefa"
            )
        }
    }
}
