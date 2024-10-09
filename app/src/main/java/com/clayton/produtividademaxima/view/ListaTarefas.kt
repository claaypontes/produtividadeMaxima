package com.clayton.produtividademaxima.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
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
import com.clayton.produtividademaxima.ui.theme.vinho

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTarefas(navController: NavController?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tarefas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White

                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = vinho
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = vinho,
                onClick = {
                    if (navController != null) {
                        navController.navigate("salvarTarefa")
                    }
                },
            ) { Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_botao),
                contentDescription = "Icone de adicionar tarefa"

            )


            }

        }
    ) { paddingValues ->
        // Conteúdo principal da tela
        Text(
            text = "Conteúdo da lista de tarefas",
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        )
    }
}