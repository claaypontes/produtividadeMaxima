package com.clayton.produtividademaxima.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.clayton.produtividademaxima.ui.theme.ShapeCaixaTexto
import com.clayton.produtividademaxima.view.PrimaryColor

@Composable
fun CaixaDeTexto(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: String,
    maxLines: Int,
    keyboardType: KeyboardType
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(text = label, color = labelColor)
        },
        maxLines = maxLines,
        singleLine = maxLines == 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = textColor,
            focusedContainerColor = Color.Transparent,
            focusedLabelColor = PrimaryColor,
            cursorColor = PrimaryColor,
            unfocusedLabelColor = labelColor,
            unfocusedContainerColor = Color.Transparent,
            unfocusedTextColor = textColor
        ),
        shape = ShapeCaixaTexto.small,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}
