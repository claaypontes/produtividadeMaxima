package com.clayton.produtividademaxima.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.clayton.produtividademaxima.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

val PrimaryColor = Color(0xFF4CAF50) // Verde principal
val SecondaryColor = Color(0xFF1E1E1E) // Preto ou cor escura

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUsuario(navController: NavController?) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Define a logo baseada no tema
    val logoRes = if (isSystemInDarkTheme()) R.drawable.logo_clara else R.drawable.logo

    Scaffold { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            val (logo, emailField, senhaField, loginButton, cadastroText, forgotPasswordText) = createRefs()

            // Logo
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "App Logo",
                modifier = Modifier
                    .constrainAs(logo) {
                        top.linkTo(parent.top, margin = 80.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .width(180.dp)
                    .aspectRatio(1f)
                    .padding(bottom = 32.dp)
            )

            // Campo de email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email", color = SecondaryColor) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF0F0F0),
                    focusedIndicatorColor = PrimaryColor,
                    unfocusedIndicatorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(emailField) {
                        top.linkTo(logo.bottom, margin = 32.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de senha
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text(text = "Senha", color = SecondaryColor) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF0F0F0),
                    focusedIndicatorColor = PrimaryColor,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(senhaField) {
                        top.linkTo(emailField.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Botão de Esqueci minha senha
            TextButton(
                onClick = {
                    if (email.isNotEmpty()) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "E-mail de recuperação de senha enviado para $email", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Erro ao enviar e-mail. Verifique o e-mail e tente novamente.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.constrainAs(forgotPasswordText) {
                    top.linkTo(senhaField.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
            ) {
                Text(text = "Esqueci minha senha", color = PrimaryColor)
            }

            // Botão de Login
            Button(
                onClick = {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
                    } else if (senha.isEmpty()) {
                        Toast.makeText(context, "Por favor, insira a senha.", Toast.LENGTH_SHORT).show()
                    } else {
                        loading = true
                        scope.launch {
                            auth.signInWithEmailAndPassword(email, senha)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                        navController?.navigate("listaTarefas")
                                    } else {
                                        val errorMessage = when (task.exception?.localizedMessage) {
                                            "INVALID_LOGIN_CREDENTIALS", "The password is invalid or the user does not have a password." ->
                                                "Email ou senha incorretos"
                                            else -> "Erro ao realizar login. Email ou senha incorretos. Tente novamente."
                                        }
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .constrainAs(loginButton) {
                        top.linkTo(forgotPasswordText.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Login", fontSize = 16.sp, color = Color.White)
                }
            }

            // Botão para redirecionar para a tela de cadastro
            TextButton(
                onClick = {
                    navController?.navigate("cadastroUsuario")
                },
                modifier = Modifier.constrainAs(cadastroText) {
                    top.linkTo(loginButton.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                Text(text = "Não tem uma conta? Cadastre-se", color = PrimaryColor)
            }
        }
    }
}

// Função para validar o email
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
