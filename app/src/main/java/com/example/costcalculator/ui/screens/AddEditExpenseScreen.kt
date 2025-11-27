package com.example.costcalculator.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.costcalculator.data.Category
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.ExpenseGroup
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expense: Expense?,
    categories: List<Category>,
    groups: List<ExpenseGroup>,
    onSave: (Expense) -> Unit,
    onNavigateBack: () -> Unit
) {
    // --- Логіка для геолокації ---
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var location by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            if (isGranted) {
                // Дозвіл отримано, запитуємо місцезнаходження
                getCurrentLocation(fusedLocationClient) { latLng ->
                    if (latLng != null) {
                        location = latLng
                        Toast.makeText(context, "Місцезнаходження отримано!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Не вдалося отримати місцезнаходження.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Користувач відхилив дозвіл
                Toast.makeText(context, "Дозвіл на доступ до геолокації відхилено.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // --- Стани для полів вводу ---
    var amount by remember { mutableStateOf(expense?.amount?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(categories.find { it.name == expense?.category } ?: categories.firstOrNull()) }
    var selectedGroup by remember { mutableStateOf(groups.find { it.id == expense?.groupId }) }
    var description by remember { mutableStateOf(expense?.description ?: "") }

    LaunchedEffect(expense) {
        if (expense?.latitude != null && expense.longitude != null) {
            location = LatLng(expense.latitude, expense.longitude)
        }
    }

    val title = if (expense == null) "Додати витрату" else "Редагувати витрату"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        // Змінили кнопку Зберегти на FloatingActionButton для кращого UX
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (amountDouble != null && selectedCategory != null) {
                        val expenseToSave = expense?.copy(
                            amount = amountDouble,
                            category = selectedCategory!!.name,
                            description = description,
                            groupId = selectedGroup?.id,
                            latitude = location?.latitude,
                            longitude = location?.longitude
                        ) ?: Expense(
                            amount = amountDouble,
                            category = selectedCategory!!.name,
                            description = description,
                            groupId = selectedGroup?.id,
                            latitude = location?.latitude,
                            longitude = location?.longitude
                        )
                        onSave(expenseToSave)
                    }
                }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Зберегти")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Додаємо прокрутку
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сума*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Список для Категорій
            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Оберіть категорію*",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категорія*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Список для Груп
            var groupExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = !groupExpanded }) {
                OutlinedTextField(
                    value = selectedGroup?.name ?: "Без групи",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Група (опціонально)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                    DropdownMenuItem(text = { Text("Без групи") }, onClick = {
                        selectedGroup = null
                        groupExpanded = false
                    })
                    groups.forEach { group ->
                        DropdownMenuItem(text = { Text(group.name) }, onClick = {
                            selectedGroup = group
                            groupExpanded = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Опис") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Блок для геолокації
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (location != null) "Місцезнаходження додано" else "Додати місцезнаходження?",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = {
                        // Запускаємо запит дозволів
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Додати місцезнаходження",
                        tint = if (location != null) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            }
        }
    }
}

// Допоміжна функція для отримання координат (поза @Composable функцією)
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationFetched: (LatLng?) -> Unit
) {
    // Використовуємо getCurrentLocation замість lastLocation для примусового оновлення
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
    ).addOnSuccessListener { loc ->
        if (loc != null) {
            onLocationFetched(LatLng(loc.latitude, loc.longitude))
        } else {
            onLocationFetched(null)
        }
    }.addOnFailureListener {
        onLocationFetched(null)
    }
}