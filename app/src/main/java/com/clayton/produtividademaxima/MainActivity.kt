package com.clayton.produtividademaxima

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clayton.produtividademaxima.ui.theme.ProdutividadeMáximaTheme
import com.clayton.produtividademaxima.view.CadastroUsuario
import com.clayton.produtividademaxima.view.ListaTarefas
import com.clayton.produtividademaxima.view.LoginUsuario
import com.clayton.produtividademaxima.view.SalvarTarefa
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProdutividadeMáximaTheme {

                val navController = rememberNavController()
                var isUserLoggedIn by remember { mutableStateOf(false) }

                // Verifica se o usuário está logado
                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    isUserLoggedIn = user != null
                }

                // Navegação baseada no estado de login
                if (isUserLoggedIn) {
                    NavGraph(navController = navController, startDestination = "listaTarefas")
                } else {
                    NavGraph(navController = navController, startDestination = "login")
                }
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("listaTarefas") {
            ListaTarefas(navController)
        }
        composable("salvarTarefa") {
            SalvarTarefa(navController)
        }
        composable("cadastroUsuario") {
            CadastroUsuario(navController)  // Tela de cadastro que será criada
        }
        composable("login") {
            LoginUsuario(navController)
        }
    }
}
