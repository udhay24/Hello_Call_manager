package com.udhay.callmanager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val accelerometerData: MutableList<Array<Float>> = mutableListOf()
    private val gyroscopeData: MutableList<Array<Float>> = mutableListOf()
    private val proximityData: MutableList<Double> = mutableListOf()

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var proximitySensor: Sensor
    private lateinit var gyroscopeSensor: Sensor

    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

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
                accelorometer_text_view.text = event.values.joinToString(",")

                accelerometerData.add(
                    arrayOf(event.values[0], event.values[1], event.values[2])
                )
            }

            Sensor.TYPE_PROXIMITY -> {
                proximity_text_view.text = event.values.joinToString(",")
                proximityData.add(event.values[0].toDouble())
            }

            Sensor.TYPE_GYROSCOPE -> {
                gyroscope_text_view.text = event.values.joinToString(",")
                gyroscopeData.add(
                    arrayOf(event.values[0], event.values[1], event.values[2])
                )
            }
        }
    }

    private fun startRecording() {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()
        accelerometerData.clear()
        gyroscopeData.clear()
        proximityData.clear()

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST)

        object: CountDownTimer(3000, 1000) {
            override fun onFinish() {
                stopRecording()
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }.start()
    }

    private fun stopRecording() {
        sensorManager.unregisterListener(this)
        publishToFirestore()
        Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
    }

    fun publishToFirestore() {
        db.collection("accelerometer")
            .add("data" to accelerometerData.joinToString { "${it.joinToString(",")} / " })
            .addOnCompleteListener {
                accelerometerData.clear()
            }

        db.collection("gyroscope")
            .add("data" to gyroscopeData.joinToString { "${it.joinToString(",")} / " })
            .addOnCompleteListener {
                gyroscopeData.clear()
            }

        db.collection("proximity")
            .add("data" to proximityData.joinToString { "$it / " })
            .addOnCompleteListener {
                gyroscopeData.clear()
            }
    }
}
