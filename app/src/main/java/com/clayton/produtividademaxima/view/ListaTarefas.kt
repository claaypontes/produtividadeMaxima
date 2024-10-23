package com.clayton.produtividademaxima.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.R
import com.clayton.produtividademaxima.itemlista.TarefaItem
import com.clayton.produtividademaxima.model.Tarefa
import com.clayton.produtividademaxima.ui.theme.vinho
import com.google.firebase.ktx.Firebase

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTarefas(navController: NavController?) {



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lista de Tarefas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = vinho)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = vinho,
                onClick = {
                    navController?.navigate("salvarTarefa")
                },
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_botao),
                    contentDescription = "Icone de adicionar tarefa"
                )
            }
        }
    ) { paddingValues ->  // Usar paddingValues fornecido pelo Scaffold
        val listaTarefas: MutableList<Tarefa> = mutableListOf(
            Tarefa(
                tarefa = "Criar um RPA",
                descricao = "Preciso criar até o dia 15",
                prioridade = 0
            ),
            Tarefa(
                tarefa = "Passear com a esposa",
                descricao = "Urgente",
                prioridade = 1
            ),
            Tarefa(
                tarefa = "Levar o cachorro ao vet",
                descricao = "Marcado para as 4 de hoje",
                prioridade = 3
            )
        )

        // Aplicar paddingValues dentro do LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),  // Aplicar o padding do Scaffold aqui
            verticalArrangement = Arrangement.spacedBy(8.dp),  // Espaçamento entre os itens
            contentPadding = PaddingValues(bottom = 16.dp)  // Espaçamento no final
        ) {
            itemsIndexed(listaTarefas) { position, _ ->
                TarefaItem(position, listaTarefas)
            }
        }
    }
}
