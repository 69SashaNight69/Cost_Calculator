package com.example.costcalculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.costcalculator.security.BiometricAuthenticator

@Composable
fun LockScreen(
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val biometricAuthenticator = remember { BiometricAuthenticator(context) }
    var authStatus by remember { mutableStateOf("Очікування...") }

    // Перевіряємо доступність біометрії при першому запуску
    LaunchedEffect(Unit) {
        if (!biometricAuthenticator.isBiometricAuthAvailable()) {
            // Якщо біометрія не налаштована, просто пропускаємо екран
            onAuthenticated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вхід у додаток", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = "Відбиток пальця",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                biometricAuthenticator.promptForAuthentication(
                    onSuccess = {
                        authStatus = "Успішно!"
                        onAuthenticated()
                    },
                    onFailed = {
                        authStatus = "Помилка. Спробуйте ще раз."
                    },
                    onError = { code, message ->
                        authStatus = "Помилка ($code): $message"
                        // Показуємо повідомлення користувачу
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        ) {
            Text("Увійти за допомогою біометрії")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(authStatus)
    }
}