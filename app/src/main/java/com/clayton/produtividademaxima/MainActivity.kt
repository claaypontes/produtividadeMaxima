package com.clayton.produtividademaxima

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.clayton.produtividademaxima.datasource.TarefaNotifierWorker
import com.clayton.produtividademaxima.ui.theme.ProdutividadeMáximaTheme
import com.clayton.produtividademaxima.view.CadastroUsuario
import com.clayton.produtividademaxima.view.ListaTarefas
import com.clayton.produtividademaxima.view.LoginUsuario
import com.clayton.produtividademaxima.view.SalvarTarefa
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Solicita permissão de notificação no Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
        }

        setContent {
            // Aplica as barras de sistema transparentes
            TransparentSystemBars()

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

        // Agenda a notificação para verificar periodicamente
        agendarNotificacaoTarefas()

        // Executa uma verificação única para teste
        executarTesteNotificacao()
    }

    private fun agendarNotificacaoTarefas() {
        val tarefaWorkRequest = PeriodicWorkRequestBuilder<TarefaNotifierWorker>(60, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "notificacao_tarefas_vencidas",
            ExistingPeriodicWorkPolicy.REPLACE,
            tarefaWorkRequest
        )
    }

    // Executa uma verificação única imediata para fins de teste
    private fun executarTesteNotificacao() {
        val tarefaWorkRequest = OneTimeWorkRequestBuilder<TarefaNotifierWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(tarefaWorkRequest)
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
            CadastroUsuario(navController)
        }
        composable("login") {
            LoginUsuario(navController)
        }
    }
}

@Composable
fun TransparentSystemBars() {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = false // true para ícones escuros, false para claros
    }
}
