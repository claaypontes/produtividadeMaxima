package com.clayton.produtividademaxima.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.constantes.Constantes
import com.clayton.produtividademaxima.itemlista.TarefaItem
import com.clayton.produtividademaxima.model.Tarefa
import com.clayton.produtividademaxima.repositorio.TarefasRepositorio
import com.google.accompanist.pager.*
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
    var showDialogoFiltro by remember { mutableStateOf(false) }

    val columnTitles = listOf("A Fazer", "Em Progresso", "Concluído")

    fun atualizarLista() {
        isRefreshing.value = true
        scope.launch {
            tarefasRepositorio.recuperarTarefasDoUsuario().collect { tarefas ->
                listaTarefas = tarefas.filter { tarefa ->
                    filtroData?.let { dataFiltro ->
                        val dataTarefaCal = Calendar.getInstance().apply {
                            if (tarefa.dataHoraVencimento is Date) {
                                time = tarefa.dataHoraVencimento as Date
                            }
                        }
                        val dataFiltroCal = Calendar.getInstance().apply {
                            time = dataFiltro
                        }
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
                title = { Text("Lista de Tarefas", fontSize = 20.sp, color = Color.White) },
                actions = {
                    IconButton(onClick = { showDialogoFiltro = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Filtrar por data",
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
                        // Exibe o nome do usuário no topo do menu
                        DropdownMenuItem(
                            onClick = { /* No action, apenas exibe o nome */ },
                            enabled = false,
                            text = { Text("Olá, $nomeUsuario") }
                        )
                        Divider() // Linha divisória para separar o nome do resto das opções
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
        Column(modifier = Modifier.padding(paddingValues)) {
            val columns = listOf(
                listaTarefas.filter { it.status == Constantes.A_FAZER },
                listaTarefas.filter { it.status == Constantes.EM_PROGRESSO },
                listaTarefas.filter { it.status == Constantes.CONCLUIDO }
            )

            val coroutineScope = rememberCoroutineScope()

            // TabRow para as abas
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = PrimaryColor,
                contentColor = Color.White
            ) {
                columnTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }

            // HorizontalPager para as páginas
            HorizontalPager(
                count = columns.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                KanbanColumn(
                    tarefas = columns[page],
                    navController = navController,
                    atualizarLista = ::atualizarLista
                )
            }
        }

        if (showDialogoFiltro) {
            FiltroDataDialog(
                context = context,
                onDateSelected = { date ->
                    filtroData = date
                    atualizarLista()
                    showDialogoFiltro = false
                },
                onClearFilter = {
                    filtroData = null
                    atualizarLista()
                    showDialogoFiltro = false
                },
                filtroDataAtiva = filtroData != null
            )
        }
    }
}

@Composable
fun FiltroDataDialog(
    context: android.content.Context,
    onDateSelected: (Date) -> Unit,
    onClearFilter: () -> Unit,
    filtroDataAtiva: Boolean
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Filtrar por Data") },
        text = {
            Column {
                Text("Selecione uma data para filtrar ou limpar o filtro atual.")
            }
        },
        confirmButton = {
            TextButton(onClick = {
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
            }) {
                Text("Escolher Data")
            }
        },
        dismissButton = {
            if (filtroDataAtiva) {
                TextButton(onClick = onClearFilter) {
                    Text("Limpar Filtro")
                }
            }
        }
    )
}

@Composable
fun KanbanColumn(
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
