package com.clayton.produtividademaxima.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.ui.theme.azul
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
        }
    ) { paddingValues ->
        // Conteúdo principal da tela
        Text(
            text = "Conteúdo da lista de tarefas",
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewListaTarefas() {
    ListaTarefas(navController = null)
}
