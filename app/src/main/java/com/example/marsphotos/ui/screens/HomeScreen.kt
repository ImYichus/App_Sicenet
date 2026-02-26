package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    snUiState: SNUiState,
    onLoginClick: (String, String, String) -> Unit,
    onKardexClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (snUiState) {
        is SNUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SNUiState.Success -> {
            // Ya no llamamos a ProfileScreen aquí porque el NavHost de AppSicenet se encarga
            // Pero lo dejamos por si quieres una vista previa rápida
        }
        else -> {
            LoginContent(
                isError = snUiState is SNUiState.Error,
                onLoginClick = onLoginClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoginContent(
    isError: Boolean,
    onLoginClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var matricula by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    val tipoUsuario = "ALUMNO"

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "SICENET TECNM", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it.uppercase().trim() },
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginClick(matricula, contrasenia, tipoUsuario) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        if (isError) {
            Text(
                text = "Credenciales incorrectas",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}