package com.example.hanah.flyingmuchofunfireworks.view

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hanah.flyingmuchofunfireworks.R

class ShackActivity : SensorEventListener {

    private lateinit var manager : SensorManager

    init {
    }

    override fun onAccuracyChanged(sensor: Sensor?, position: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    interface callback{
        fun onFinish()
    }
}
