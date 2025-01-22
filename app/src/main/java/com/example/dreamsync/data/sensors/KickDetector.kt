package com.example.dreamsync.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun KickDetector(
    context: Context,
    leavingLayer: Boolean,
    onKickDetected: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var kickDetected by remember { mutableStateOf(false) }

    DisposableEffect(leavingLayer) {
        if (leavingLayer) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            val sensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]

                        val magnitude = sqrt(x * x + y * y + z * z)

                        if (magnitude > 15) { // Adjust this threshold as needed
                            kickDetected = true
                            onKickDetected()
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(
                sensorListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            onDispose {
                sensorManager.unregisterListener(sensorListener)
            }
        } else {
            onDispose {}
        }
    }

    // React to kick detection
    LaunchedEffect(kickDetected) {
        if (kickDetected) {
            scope.launch {
                // Reset the kick detection for future use
                kickDetected = false
            }
        }
    }
}

suspend fun detectKick(context: Context, onResult: (Boolean) -> Unit) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    if (accelerometer == null) {
        Log.e("KickDetection", "No accelerometer available!")
        onResult(false)
        return
    }

    val threshold = 15f // Adjust kick detection threshold as needed
    val kickDetected = CompletableDeferred<Boolean>()

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val magnitude = sqrt(
                    event.values[0].pow(2) +
                            event.values[1].pow(2) +
                            event.values[2].pow(2)
                )
                if (magnitude > threshold) {
                    Log.d("KickDetection", "Kick detected: magnitude=$magnitude")
                    sensorManager.unregisterListener(this) // Ensure listener is removed
                    kickDetected.complete(true)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    // Register the listener
    sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

    // Use a try-finally block to ensure cleanup
    try {
        // Wait for kick or timeout
        val result = withTimeoutOrNull(5000L) { // Timeout in milliseconds
            kickDetected.await()
        }
        if (result == true) {
            onResult(true)
        } else {
            Log.e("KickDetection", "Kick not detected within timeout.")
            onResult(false)
        }
    } finally {
        sensorManager.unregisterListener(listener)
    }
}

