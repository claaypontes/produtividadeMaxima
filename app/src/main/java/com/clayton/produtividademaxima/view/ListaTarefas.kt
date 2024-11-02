package com.clayton.produtividademaxima.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.constantes.Constantes
import com.clayton.produtividademaxima.itemlista.TarefaItem
import com.clayton.produtividademaxima.model.Tarefa
import com.clayton.produtividademaxima.repositorio.TarefasRepositorio
import com.google.accompanist.pager.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ListaTarefas(navController: NavController?) {
    val tarefasRepositorio = TarefasRepositorio()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var nomeUsuario by remember { mutableStateOf("Usuário") }
    var showMenu by remember { mutableStateOf(false) }
    var listaTarefas by remember { mutableStateOf<List<Tarefa>>(emptyList()) }
    val isRefreshing = remember { mutableStateOf(false) }
    var filtroData by remember { mutableStateOf<Date?>(null) }

    val columnTitles = listOf("A Fazer", "Em Progresso", "Concluído")

    // Função para atualizar a lista de tarefas com base no filtro de data
    fun atualizarLista() {
        isRefreshing.value = true
        scope.launch {
            tarefasRepositorio.recuperarTarefasDoUsuario().collect { tarefas ->
                listaTarefas = tarefas.filter { tarefa ->
                    filtroData?.let { dataFiltro ->
                        // Verifica se `dataHoraVencimento` é do tipo `Date` e configura `Calendar` adequadamente
                        val dataTarefaCal = Calendar.getInstance().apply {
                            if (tarefa.dataHoraVencimento is Date) {
                                time = tarefa.dataHoraVencimento as Date
                            }
                        }
                        val dataFiltroCal = Calendar.getInstance().apply {
                            time = dataFiltro
                        }
                        // Compara ano, mês e dia para verificar se coincidem com o filtro de data
                        tarefa.dataHoraVencimento is Date &&
                                dataTarefaCal.get(Calendar.YEAR) == dataFiltroCal.get(Calendar.YEAR) &&
                                dataTarefaCal.get(Calendar.MONTH) == dataFiltroCal.get(Calendar.MONTH) &&
                                dataTarefaCal.get(Calendar.DAY_OF_MONTH) == dataFiltroCal.get(Calendar.DAY_OF_MONTH)
                    } ?: true
                }
                isRefreshing.value = false
            }
        }
    }

    LaunchedEffect(user) {
        user?.let {
            db.collection("usuarios").document(it.uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    nomeUsuario = document.getString("nome") ?: "Usuário"
                }
            }
        }
        atualizarLista()
    }

    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lista de Tarefas",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Olá, $nomeUsuario",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        abrirDialogoFiltro(context) { date ->
                            filtroData = date
                            atualizarLista()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Filtrar por data",
                            tint = Color.White
                        )
                    }
                    // Botão para limpar o filtro de data
                    IconButton(onClick = {
                        filtroData = null
                        atualizarLista()
                    }) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle, // Escolha um ícone adequado
                            contentDescription = "Limpar Filtro",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                auth.signOut()
                                Toast.makeText(
                                    context,
                                    "Desconectado com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController?.navigate("login")
                            },
                            text = { Text("Sair") }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = PrimaryColor,
                onClick = { navController?.navigate("salvarTarefa") },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Tarefa",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing.value),
            onRefresh = {
                atualizarLista()
            }
        ) {
            val columns = listOf(
                listaTarefas.filter { it.status == Constantes.A_FAZER },
                listaTarefas.filter { it.status == Constantes.EM_PROGRESSO },
                listaTarefas.filter { it.status == Constantes.CONCLUIDO }
            )

            HorizontalPager(
                count = columns.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalAlignment = Alignment.Top
            ) { page ->
                KanbanColumn(
                    title = columnTitles[page],
                    tarefas = columns[page],
                    navController = navController,
                    atualizarLista = ::atualizarLista
                )
            }
        }
    }
}

fun abrirDialogoFiltro(context: android.content.Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun KanbanColumn(
    title: String,
    tarefas: List<Tarefa>,
    navController: NavController?,
    atualizarLista: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (tarefas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sem tarefas",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tarefas) { tarefa ->
                    TarefaItem(
                        tarefa = tarefa,
                        context = LocalContext.current,
                        navController = navController!!,
                        onTaskStatusChanged = { tarefaAlterada, novoStatus ->
                            tarefaAlterada.status = novoStatus
                            atualizarLista()
                        },
                        atualizarLista = atualizarLista
                    )
                }
            }
        }
    }
}
