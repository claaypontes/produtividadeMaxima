package com.clayton.produtividademaxima.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.clayton.produtividademaxima.ui.theme.ShapeCaixaTexto
import com.clayton.produtividademaxima.ui.theme.vinho

@Composable
fun CaixaDeTexto(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: String,
    maxLines: Int,
    keyboardType: KeyboardType
){

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange, // Corrigido: especificar o parâmetro da função
        modifier = modifier,
        label = {
            Text(text = label)
        },
        maxLines = maxLines,
        singleLine = maxLines == 1,  // Adicionado singleLine baseado no maxLines
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            focusedContainerColor = vinho,
            focusedLabelColor = vinho,
            cursorColor = vinho,
        ),
        shape = ShapeCaixaTexto.small,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}
