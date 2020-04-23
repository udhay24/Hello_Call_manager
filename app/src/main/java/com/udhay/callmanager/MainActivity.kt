package com.udhay.callmanager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val accelerometerValue: MutableList<FloatArray> = mutableListOf()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        start_button.setOnClickListener {
            startRecording()
        }

        stop_button.setOnClickListener {
            stopRecording()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerValue.add(event.values)
            }
        }
    }

    private fun startRecording() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun stopRecording() {
        sensorManager.unregisterListener(this)
        publishToFirestore()
    }

    fun publishToFirestore() {
        Log.v("Accelerometer", accelerometerValue.joinToString { "${it.joinToString(",")} / " })
        accelerometerValue.clear()
    }
}
