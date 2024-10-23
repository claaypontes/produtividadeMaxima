package com.clayton.produtividademaxima.view

import android.provider.MediaStore.Audio.Radio
import android.widget.RadioButton
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clayton.produtividademaxima.R
import com.clayton.produtividademaxima.componentes.Botao
import com.clayton.produtividademaxima.componentes.CaixaDeTexto
import com.clayton.produtividademaxima.constantes.Constantes
import com.clayton.produtividademaxima.repositorio.TarefasRepositorio
import com.clayton.produtividademaxima.ui.theme.vinho
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = vinho
                )
            )
        },
    ) { paddingValues ->

        var tituloTarefa by remember {
            mutableStateOf(value = "")
        }

        var descricaoTarefa by remember {
            mutableStateOf(value = "")
        }

        var semPrioridadeTarefa by remember {
            mutableStateOf(value = false)
        }
        var baixaPrioridadeTarefa by remember {
            mutableStateOf(value = false)
        }
        var altaPrioridadeTarefa by remember {
            mutableStateOf(value = false)
        }


        // Conteúdo principal da tela
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)  // Adiciona o paddingValues aqui
        ) {
            CaixaDeTexto(
                value = tituloTarefa,
                onValueChange = {
                    tituloTarefa = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 0.dp),
                label = "Titulo da Tarefa",
                maxLines = 1,
                keyboardType = KeyboardType.Text
            )
            CaixaDeTexto(
                value = descricaoTarefa,
                onValueChange = {
                    descricaoTarefa = it
                },
                modifier = Modifier
                    .fillMaxWidth().height(150.dp)
                    .padding(20.dp, 20.dp, 20.dp, 0.dp),
                label = "Descrição",
                maxLines = 3,
                keyboardType = KeyboardType.Text
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Prioridade da tarefa: ")

                // Botao sem prioridade (Cinza - Neutro)
                RadioButton(
                    selected = semPrioridadeTarefa,
                    onClick = {
                        semPrioridadeTarefa = !semPrioridadeTarefa
                        baixaPrioridadeTarefa = false
                        altaPrioridadeTarefa = false
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Gray,
                        unselectedColor = Color.LightGray
                    )
                )

                // Botao baixa Prioridade (Verde)
                RadioButton(
                    selected = baixaPrioridadeTarefa,
                    onClick = {
                        baixaPrioridadeTarefa = !baixaPrioridadeTarefa
                        semPrioridadeTarefa = false
                        altaPrioridadeTarefa = false
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green,
                        unselectedColor = Color.LightGray
                    )
                )

                // Botao alta Prioridade (Vermelho)
                RadioButton(
                    selected = altaPrioridadeTarefa,
                    onClick = {
                        altaPrioridadeTarefa = !altaPrioridadeTarefa
                        semPrioridadeTarefa = false
                        baixaPrioridadeTarefa = false
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.LightGray
                    )
                )
            }

            Botao(
                onClick = {
                    // O que o botao vai fazer
                    var mensagem = true
                    scope.launch (Dispatchers.IO){
                        if (tituloTarefa.isEmpty()) {
                            mensagem = false
                        }else if (tituloTarefa.isNotEmpty() && descricaoTarefa.isNotEmpty() && baixaPrioridadeTarefa){
                            tarefasRepositorio.salvarTarefa(tituloTarefa, descricaoTarefa,Constantes.PRIORIDADE_BAIXA)
                            mensagem = true
                        }
                    }
                    scope.launch (Dispatchers.Main){
                        if (mensagem){
                            Toast.makeText(context, "Sucesso ao salvar a tarefa!", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "Titulo da tarefa é obrigatorio!", Toast.LENGTH_SHORT).show()

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
