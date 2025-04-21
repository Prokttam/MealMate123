package com.example.mealmate.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val context: Context,
    private val onShakeListener: () -> Unit
) : SensorEventListener {
    private val shakeThresholdGravity = 2.7f
    private val shakeSlopTimeMs = 500
    private val shakeCountResetTimeMs = 3000

    private var shakeTimestamp: Long = 0
    private var shakeCount: Int = 0

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // gForce will be close to 1 when there is no movement.
            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

            if (gForce > shakeThresholdGravity) {
                val now = System.currentTimeMillis()

                // Ignore shake events too close to each other
                if (shakeTimestamp + shakeSlopTimeMs > now) {
                    return
                }

                // Reset shake count after 3 seconds of no shakes
                if (shakeTimestamp + shakeCountResetTimeMs < now) {
                    shakeCount = 0
                }

                shakeTimestamp = now
                shakeCount++

                // Trigger listener on shake
                if (shakeCount >= 2) {
                    onShakeListener.invoke()
                    shakeCount = 0
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not used
    }
}
