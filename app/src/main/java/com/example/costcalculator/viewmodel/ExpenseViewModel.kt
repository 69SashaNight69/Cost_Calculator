package com.example.costcalculator.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costcalculator.bluetooth.BluetoothController
import com.example.costcalculator.data.Category
import com.example.costcalculator.data.ChartData
import com.example.costcalculator.data.Expense
import com.example.costcalculator.data.ExpenseGroup
import com.example.costcalculator.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ЗМІНА 1: Конструктор тепер приймає репозиторій
class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val bluetoothAdapter: BluetoothAdapter? // ДОДАНО
) : ViewModel() {

    // ЗМІНА 2: Ми прибрали всю логіку з DAO та init {}
    private val _bluetoothController = bluetoothAdapter?.let { BluetoothController(it) }

    // Стан для знайдених пристроїв
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices.asStateFlow()

    // Стан для отриманої витрати
    private val _receivedExpense = MutableSharedFlow<Expense>()
    val receivedExpense: SharedFlow<Expense> = _receivedExpense.asSharedFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId.asStateFlow()

    private val allExpensesFlow = repository.getAllExpenses()

    val totalSpent: StateFlow<Double> = allExpensesFlow
        .map { expenses -> expenses.sumOf { it.amount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val transactionCount: StateFlow<Int> = allExpensesFlow
        .map { expenses -> expenses.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val averageTransaction: StateFlow<Double> = allExpensesFlow
        .map { expenses ->
            if (expenses.isNotEmpty()) {
                expenses.sumOf { it.amount } / expenses.size
            } else {
                0.0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val expensesWithLocation: StateFlow<List<Expense>> = allExpensesFlow
        .map { expenses ->
            expenses.filter { it.latitude != null && it.longitude != null }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val chartData: StateFlow<List<ChartData>> = allExpensesFlow
        .map { expenses ->
            // Логіка залишається такою ж, але створюємо наш власний об'єкт
            expenses
                .groupBy { it.category }
                .map { (category, expensesList) ->
                    ChartData(
                        value = expensesList.sumOf { it.amount }.toFloat(),
                        label = category
                    )
                }
                .filter { it.value > 0 }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Дані тепер надходять з репозиторію
    val expenses: StateFlow<List<Expense>> = repository.getAllExpenses()
        // ЗМІНЕНО: Тепер комбінуємо ТРИ потоки
        .combine(_selectedCategoryId) { expenses, categoryId -> Pair(expenses, categoryId) }
        .combine(_selectedGroupId) { (expenses, categoryId), groupId ->
            // Фільтруємо спочатку за категорією
            val filteredByCategory = if (categoryId == null) {
                expenses
            } else {
                val categoryName = repository.getAllCategories().first().find { it.id == categoryId }?.name
                expenses.filter { it.category == categoryName }
            }
            // Потім результат фільтруємо за групою
            if (groupId == null) {
                filteredByCategory
            } else {
                filteredByCategory.filter { it.groupId == groupId }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val groups: StateFlow<List<ExpenseGroup>> = repository.getAllGroups()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getGroupNameById(id: Long): String? {
        return repository.getAllGroups().first().find { it.id == id }?.name
    }

    fun addGroup(groupName: String, description: String?) = viewModelScope.launch {
        repository.insertGroup(ExpenseGroup(name = groupName, description = description))
    }

    fun deleteGroup(group: ExpenseGroup) = viewModelScope.launch {
        repository.deleteGroup(group)
    }

    // ЗМІНА 3: Всі функції тепер просто викликають методи репозиторію
    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun selectGroup(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return repository.getExpenseById(id)
    }

    fun addCategory(categoryName: String) = viewModelScope.launch {
        repository.insertCategory(Category(name = categoryName))
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }

    fun startDiscovery() {
        _bluetoothController?.startDiscovery()?.onEach { devices ->
            _scannedDevices.value = devices
        }?.launchIn(viewModelScope)
    }

    fun shareExpense(device: BluetoothDevice, expense: Expense) {
        viewModelScope.launch {
            _bluetoothController?.connectToServer(device, expense)
        }
    }

    fun startReceiving() {
        _bluetoothController?.startServer()?.onEach { receivedExpense ->

            // 1. Перевіряємо категорію
            val categoryExists = categories.value.any { it.name.equals(receivedExpense.category, ignoreCase = true) }
            if (!categoryExists) {
                // Якщо такої категорії немає, створюємо її
                addCategory(receivedExpense.category)
            }

            // 2. Перевіряємо групу (якщо вона була передана)
            var finalGroupId: Long? = receivedExpense.groupId
            if (receivedExpense.groupId != null) {
                // Щоб уникнути конфлікту ID, ми не можемо просто довіряти ID з іншого пристрою.
                // Ми повинні знайти назву групи і знайти її у себе або створити нову.
                // Ця логіка складніша, тому для простоти ми поки що будемо
                // просто скидати групу при передачі.
                finalGroupId = null // Спрощений варіант: ігноруємо групу при передачі
            }

            // 3. Створюємо і зберігаємо нову витрату
            val newExpense = receivedExpense.copy(
                id = 0, // Завжди 0, щоб база згенерувала новий ID
                groupId = finalGroupId // Використовуємо перевірений groupId
            )
            addExpense(newExpense)

            _receivedExpense.emit(receivedExpense)

        }?.launchIn(viewModelScope)
    }

    // --- Очистка при знищенні ViewModel ---
    override fun onCleared() {
        _bluetoothController?.stop()
        super.onCleared()
    }
}