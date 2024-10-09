package com.clayton.produtividademaxima.componentes

import android.widget.Button
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.clayton.produtividademaxima.ui.theme.vinho

@Composable
fun Botao(
    onClick: () -> Unit,
    modifier: Modifier,
    texto: String
){
    Button(
        onClick,
        modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = vinho,
            contentColor = Color.White

        )
    ){
        Text(text = texto, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }


}