package com.clayton.produtividademaxima.itemlista

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.clayton.produtividademaxima.R
import com.clayton.produtividademaxima.model.Tarefa
import com.clayton.produtividademaxima.ui.theme.ShapeCardPrioridades

@Composable
fun TarefaItem(
    position: Int,
    listaTarefas: MutableList<Tarefa>

) {

    val tarefaTitulo = listaTarefas[position].tarefa
    val descricaoTarefa = listaTarefas[position].descricao
    val prioridadeTarefa = listaTarefas[position].prioridade

    var nivelDePrioridade: String = when(prioridadeTarefa){
        0 -> { "Sem Prioridade" }
        1 -> { "Prioridade Baixa" }
        2 -> { "Prioridade Média" }
        else -> {
            "Prioridade Alta"
        }
    }

    val color = when(prioridadeTarefa){
        0 -> { Color.Gray }
        1 -> { Color.Green }
        2 -> { Color.Yellow }
        else -> {
            Color.Red

        }

    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val (tituloTexto, descricaoTexto, cardPrioridade, prioridadeTexto, btDeletar) = createRefs()

            Text(
                text = tarefaTitulo.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.constrainAs(tituloTexto) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            )

            Text(
                text = descricaoTarefa.toString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.constrainAs(descricaoTexto) {
                    top.linkTo(tituloTexto.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = nivelDePrioridade,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.constrainAs(prioridadeTexto) {
                    top.linkTo(descricaoTexto.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
            )

            Card(
                modifier = Modifier
                    .size(30.dp)
                    .constrainAs(cardPrioridade) {
                        top.linkTo(descricaoTexto.bottom, margin = 16.dp)
                        start.linkTo(prioridadeTexto.end, margin = 8.dp)
                        bottom.linkTo(parent.bottom)
                    },
                shape = ShapeCardPrioridades.small,
                colors = CardDefaults.cardColors(containerColor = color)
            ) {}

            IconButton(
                onClick = { /* Ação de deletar */ },
                modifier = Modifier.constrainAs(btDeletar) {
                    top.linkTo(descricaoTexto.bottom, margin = 16.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_deletar),
                    contentDescription = "Deletar Tarefa",
                    tint = Color.Red
                )
            }
        }
    }
}