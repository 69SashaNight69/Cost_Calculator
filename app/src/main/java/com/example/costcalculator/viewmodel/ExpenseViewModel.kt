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
import com.example.costcalculator.bluetooth.ExpenseDTO
import kotlinx.coroutines.Dispatchers

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

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)

            launch(Dispatchers.IO) {
                // 1. Перевірка категорії
                // Отримуємо всі витрати, що ще залишились з цією категорією
                val otherExpensesInCategory = repository.getAllExpenses().first()
                    .count { it.category.equals(expense.category, ignoreCase = true) }

                // Якщо інших витрат з цією категорією немає (count == 0)
                if (otherExpensesInCategory == 0) {
                    // Знаходимо саму категорію в нашому списку і видаляємо її
                    repository.getAllCategories().first()
                        .find { it.name.equals(expense.category, ignoreCase = true) }
                        ?.let { categoryToDelete ->
                            repository.deleteCategory(categoryToDelete)
                        }
                }

                // 2. Перевірка групи (аналогічно)
                expense.groupId?.let { groupId ->
                    val otherExpensesInGroup = repository.getAllExpenses().first()
                        .count { it.groupId == groupId }

                    if (otherExpensesInGroup == 0) {
                        repository.getAllGroups().first()
                            .find { it.id == groupId }
                            ?.let { groupToDelete ->
                                repository.deleteGroup(groupToDelete)
                            }
                    }
                }
            }
        }
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
            // 1. Знаходимо назву групи
            val groupName = expense.groupId?.let { id ->
                groups.value.find { it.id == id }?.name
            }
            // 2. Створюємо DTO з усіма потрібними даними
            val expenseDto = ExpenseDTO(
                amount = expense.amount,
                category = expense.category,
                description = expense.description,
                groupName = groupName,
                latitude = expense.latitude,
                longitude = expense.longitude
            )
            // 3. Відправляємо DTO
            _bluetoothController?.connectToServer(device, expenseDto)
        }
    }

    fun startReceiving() {
        _bluetoothController?.startServer()?.onEach { receivedDto ->
            // --- НАДІЙНА ЛОГІКА ---
            viewModelScope.launch {
                // Крок 1: Переконуємось, що категорія існує, і отримуємо її об'єкт з ID
                // Нам потрібна лише назва для збереження у витраті, але цей крок гарантує,
                // що категорія буде у фільтрах.
                val category = repository.findOrCreateCategory(receivedDto.category)

                // Крок 2: Переконуємось, що група існує, і отримуємо її об'єкт з ID
                var finalGroupId: Long? = null
                if (receivedDto.groupName != null) {
                    val group = repository.findOrCreateGroup(receivedDto.groupName)
                    finalGroupId = group.id
                }

                // Крок 3: Тепер, коли всі довідники готові, створюємо і зберігаємо витрату
                val newExpense = Expense(
                    id = 0,
                    amount = receivedDto.amount,
                    category = category.name, // Беремо назву з отриманого/створеного об'єкта
                    description = receivedDto.description, // Опис передається напряму
                    groupId = finalGroupId, // Тепер у нас є гарантований ID
                    latitude = receivedDto.latitude,
                    longitude = receivedDto.longitude
                )
                repository.insertExpense(newExpense)

                // _receivedExpense.emit(newExpense) // Повідомляємо UI (опціонально)
            }
        }?.launchIn(viewModelScope)
    }

    // --- Очистка при знищенні ViewModel ---
    override fun onCleared() {
        _bluetoothController?.stop()
        super.onCleared()
    }
}