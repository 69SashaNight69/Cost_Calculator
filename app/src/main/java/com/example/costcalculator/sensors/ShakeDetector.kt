package com.example.costcalculator.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.pow
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastTime: Long = 0
    private var lastX: Float = -1.0f
    private var lastY: Float = -1.0f
    private var lastZ: Float = -1.0f

    private val shakeThreshold = 800

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastTime) > 100) {
                val diffTime = (currentTime - lastTime)
                lastTime = currentTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = sqrt((x - lastX).pow(2) + (y - lastY).pow(2) + (z - lastZ).pow(2)) / diffTime * 10000

                if (speed > shakeThreshold) {
                    onShake()
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не використовується
    }
}