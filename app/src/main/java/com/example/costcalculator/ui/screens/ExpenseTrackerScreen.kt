package com.example.costcalculator.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.costcalculator.data.Category
import com.example.costcalculator.data.ExpenseGroup
import com.example.costcalculator.ui.components.ExpenseList
import com.example.costcalculator.viewmodel.ExpenseViewModel
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.rememberBottomSheetScaffoldState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel = viewModel(),
    onExpenseClick: (Long) -> Unit,
    onAddExpenseClick: () -> Unit,
    onManageCategoriesClick: () -> Unit,
    onManageGroupsClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onMapClick: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() } // Для показу повідомлень
    val coroutineScope = rememberCoroutineScope()

    // Підписуємось на отримані витрати, щоб показати повідомлення
    LaunchedEffect(key1 = viewModel) {
        viewModel.receivedExpense.collect { expense ->
            snackbarHostState.showSnackbar("Отримано нову витрату: ${expense.amount} грн")
        }
    }

    // Лаунчер для дозволів (аналогічно до екрану деталей)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.values.all { it }) {
                viewModel.startReceiving()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Режим отримання увімкнено. Очікування...")
                }
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Калькулятор витрат") },
                actions = {
                    IconButton(onClick = {
                        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            arrayOf(
                                Manifest.permission.BLUETOOTH_ADVERTISE,
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        } else {
                            arrayOf(
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN
                            )
                        }
                        permissionLauncher.launch(permissionsToRequest)
                    }) {
                        Icon(Icons.Default.Bluetooth, contentDescription = "Отримати витрату")
                    }
                    IconButton(onClick = onMapClick) {
                        Icon(Icons.Default.Map, contentDescription = "Карта")
                    }
                    // Кнопка для аналітики
                    IconButton(onClick = onAnalyticsClick) {
                        Icon(Icons.Default.Analytics, contentDescription = "Аналітика")
                    }
                    // Кнопка для груп
                    IconButton(onClick = onManageGroupsClick) {
                        Icon(Icons.Default.Group, contentDescription = "Налаштування груп")
                    }
                    // Кнопка для категорій
                    IconButton(onClick = onManageCategoriesClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Налаштування категорій")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Filled.Add, contentDescription = "Додати витрату")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Фільтри
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdown(
                    modifier = Modifier.weight(1f),
                    label = "Категорія",
                    items = categories.map { it.name to it.id },
                    selectedItemId = selectedCategoryId,
                    onItemSelected = { viewModel.selectCategory(it) }
                )
                FilterDropdown(
                    modifier = Modifier.weight(1f),
                    label = "Група",
                    items = groups.map { it.name to it.id },
                    selectedItemId = selectedGroupId,
                    onItemSelected = { viewModel.selectGroup(it) }
                )
            }

            ExpenseList(
                expenses = expenses,
                groups = groups,
                onExpenseClick = onExpenseClick,
                onExpenseSwiped = { expense -> viewModel.deleteExpense(expense) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    modifier: Modifier = Modifier,
    label: String,
    items: List<Pair<String, Long>>,
    selectedItemId: Long?,
    onItemSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItemName = items.find { it.second == selectedItemId }?.first ?: "Всі"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItemName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Всі") },
                onClick = {
                    onItemSelected(null)
                    expanded = false
                }
            )
            items.forEach { (name, id) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onItemSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}