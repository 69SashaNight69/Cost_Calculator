package com.example.costcalculator.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
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
    fun startServer(): Flow<ExpenseDTO> = flow {
        serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("CostCalculator", uuid)
        val socket = serverSocket?.accept()
        if (socket != null) {
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)
            val bytes = inputStream.read(buffer)
            val jsonString = String(buffer, 0, bytes)
            // ЗМІНЕНО: GSON тепер створює ExpenseDTO
            val expenseDto = Gson().fromJson(jsonString, ExpenseDTO::class.java)
            emit(expenseDto)
            socket.close()
        }
    }.flowOn(Dispatchers.IO)

    // ЗМІНЕНО: тепер функція приймає ExpenseDTO
    suspend fun connectToServer(device: BluetoothDevice, expenseDto: ExpenseDTO) {
        withContext(Dispatchers.IO) {
            try {
                clientSocket = device.createRfcommSocketToServiceRecord(uuid)
                clientSocket?.connect()
                val outputStream = clientSocket?.outputStream
                // ЗМІНЕНО: GSON перетворює на JSON об'єкт ExpenseDTO
                val jsonString = Gson().toJson(expenseDto)
                outputStream?.write(jsonString.toByteArray())
            } catch (e: IOException) {
                Log.e("BluetoothClient", "Помилка під час відправки даних: ${e.message}", e)

                // Можеш показати користувачу Toast
                // Toast.makeText(context, "Не вдалося підключитись до пристрою", Toast.LENGTH_SHORT).show()

                try {
                    clientSocket?.close()
                } catch (closeException: IOException) {
                    Log.e("BluetoothClient", "Помилка при закритті сокету: ${closeException.message}", closeException)
                }
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