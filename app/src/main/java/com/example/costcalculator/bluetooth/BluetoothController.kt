package com.example.costcalculator.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import com.example.costcalculator.data.Expense
import com.google.gson.Gson // Використовуємо GSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission") // Ми будемо перевіряти дозволи в UI
class BluetoothController(private val bluetoothAdapter: BluetoothAdapter) {
    // Унікальний ідентифікатор нашого сервісу
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null

    // Пошук пристроїв
    fun startDiscovery(): Flow<List<BluetoothDevice>> = flow {
        emit(bluetoothAdapter.bondedDevices.toList())
        // Тут можна додати логіку пошуку нових пристроїв
    }.flowOn(Dispatchers.IO)

    // Режим "Сервера" (очікування даних)
    fun startServer(): Flow<Expense> = flow {
        serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("CostCalculator", uuid)
        val socket = serverSocket?.accept() // Чекаємо, поки хтось підключиться
        if (socket != null) {
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)
            val bytes = inputStream.read(buffer)
            val jsonString = String(buffer, 0, bytes)
            // Використовуємо GSON для перетворення тексту в об'єкт
            val expense = Gson().fromJson(jsonString, Expense::class.java)
            emit(expense)
            socket.close()
        }
    }.flowOn(Dispatchers.IO)

    // Режим "Клієнта" (відправка даних)
    suspend fun connectToServer(device: BluetoothDevice, expense: Expense) {
        withContext(Dispatchers.IO) {
            try {
                clientSocket = device.createRfcommSocketToServiceRecord(uuid)
                clientSocket?.connect()
                val outputStream = clientSocket?.outputStream
                // Використовуємо GSON для перетворення об'єкта в текст
                val jsonString = Gson().toJson(expense)
                outputStream?.write(jsonString.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                clientSocket?.close()
            }
        }
    }

    // Зупинка всіх з'єднань
    fun stop() {
        try {
            serverSocket?.close()
            clientSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}