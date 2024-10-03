package com.clayton.produtividademaxima

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clayton.produtividademaxima.ui.theme.ProdutividadeMáximaTheme
import com.clayton.produtividademaxima.view.ListaTarefas
import com.clayton.produtividademaxima.view.SalvarTarefa

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProdutividadeMáximaTheme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "listaTarefas"){
                    composable(
                        route = "listaTarefas"
                    ){
                        ListaTarefas(navController)
                    }
                    composable(
                        route = "salvarTarefa"
                    ){
                        SalvarTarefa(navController)
                    }
                }

                }
            }
        }
    }