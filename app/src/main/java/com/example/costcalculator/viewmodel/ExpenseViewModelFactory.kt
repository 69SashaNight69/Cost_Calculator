package com.example.costcalculator.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.costcalculator.data.repository.ExpenseRepository
import java.lang.IllegalArgumentException
import android.bluetooth.BluetoothManager


class ExpenseViewModelFactory(
    private val repository: ExpenseRepository,
    private val context: Context // ДОДАНО
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            // Отримуємо системний сервіс Bluetooth
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            @Suppress("UNCHECKED_CAST")
            // Передаємо адаптер у ViewModel
            return ExpenseViewModel(repository, bluetoothManager.adapter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}