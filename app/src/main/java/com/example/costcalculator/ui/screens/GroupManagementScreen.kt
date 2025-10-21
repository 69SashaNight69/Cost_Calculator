package com.example.costcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.costcalculator.data.ExpenseGroup // Змінено
import com.example.costcalculator.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagementScreen( // Змінено
    viewModel: ExpenseViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val groups by viewModel.groups.collectAsState() // Змінено
    var newGroupName by remember { mutableStateOf("") } // Змінено
    var newGroupDescription by remember { mutableStateOf("") } // Можна додати опис

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управління групами") }, // Змінено
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Поля для додавання нової групи
            OutlinedTextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                label = { Text("Назва нової групи") }, // Змінено
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newGroupDescription,
                onValueChange = { newGroupDescription = it },
                label = { Text("Опис (опціонально)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newGroupName.isNotBlank()) {
                        viewModel.addGroup(newGroupName, newGroupDescription.takeIf { it.isNotBlank() })
                        newGroupName = "" // Очищуємо поля
                        newGroupDescription = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Додати")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Додати групу")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Список існуючих груп
            LazyColumn {
                items(groups) { group -> // Змінено
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = group.name, style = MaterialTheme.typography.bodyLarge)
                            group.description?.let {
                                Text(text = it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteGroup(group) }) { // Змінено
                            Icon(Icons.Default.Delete, contentDescription = "Видалити")
                        }
                    }
                    Divider()
                }
            }
        }
    }
}