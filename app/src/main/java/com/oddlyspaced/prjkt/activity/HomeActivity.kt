package com.oddlyspaced.prjkt.activity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.EyeCandyAdapter
import com.oddlyspaced.prjkt.databinding.ActivityHomeBinding
import kotlin.math.abs

class HomeActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var adapt: EyeCandyAdapter
    private lateinit var lm: LinearLayoutManager
    private val list = arrayListOf<Int>()

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gyroscope: Sensor

    private val icons = arrayListOf(
        "chrome",
        "spotify",
        "camera",
        "calendar",
        "contacts",
        "clock",
        "phone",
        "netflix",
        "playstore",
        "message",
        "whatsapp",
        "telegram",
        "instagram",
        "gmail",
        "google",
        "settings"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(TYPE_GYROSCOPE)

        lm = GridLayoutManager(applicationContext, 9)

        generateList()
        adapt = EyeCandyAdapter(list)

        binding.rvInfinite.apply {
            setHasFixedSize(true)
            layoutManager = lm
            adapter = adapt
        }

        // infiniteScroll()
    }

    private fun generateList() {
        repeat(500) {
            list.add(resources.getIdentifier(icons.random(), "drawable", packageName))
        }
    }


    private var current = 0

    private fun infiniteScroll() {
        Handler(Looper.getMainLooper()).postDelayed({
            lm.scrollToPosition(
              when {
                  (current == list.size) -> {
                      current = 0
                      0
                  }
                  else -> {
                      current * 4
                  }
              }
            )
            current++
            infiniteScroll()
        }, 100)
    }

    private var rot = 0F

    override fun onSensorChanged(event: SensorEvent?) {
        if (binding.rvInfinite.rotation > 45 || binding.rvInfinite.rotation < -45) {
            return
        }

        rot = Math.toDegrees(event!!.values[0].toDouble()).toFloat() / 25
        if (abs(rot).toInt() < 3) {
            return
        }

        Log.d("Sensor", rot.toString())
        ValueAnimator.ofFloat(binding.rvInfinite.rotation, rot).apply {
            duration = 100
            addUpdateListener {
                binding.rvInfinite.rotation = it.animatedValue as Float
            }
        }
        binding.rvInfinite.rotation = rot
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW)
        // sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}