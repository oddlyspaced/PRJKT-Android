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
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.EyeCandyAdapter
import com.oddlyspaced.prjkt.databinding.ActivityHomeBinding

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

    private val iconColors = arrayOf("#71feff", "#a5f777", "#f94f2e")
    private val bgColors = arrayListOf("#050714".toColorInt(), "#181818".toColorInt(), "#181818".toColorInt())
    private var currentColor = 0

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

        infiniteScroll()

        switchColor()
    }

    private fun generateList() {
        icons.forEach { icon ->
            list.add(resources.getIdentifier(icon, "drawable", packageName))
        }
    }

    private fun infiniteScroll() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.rvInfinite.smoothScrollBy(0, 25)
            infiniteScroll()
        }, 100)
    }

    private fun switchColor() {
        Handler(Looper.getMainLooper()).postDelayed({
            var prev = currentColor - 1
            if (prev == -1)
                prev = iconColors.size - 1

            ValueAnimator.ofArgb(bgColors[prev], bgColors[currentColor]).apply {
                duration = 1000
                doOnStart {
                    adapt.applyIconColor(iconColors[currentColor].toColorInt())
                }
                addUpdateListener {
                    binding.rvInfinite.setBackgroundColor(it.animatedValue as Int)
                }
                doOnEnd {
                    currentColor++
                    if (currentColor == iconColors.size)
                        currentColor = 0
                }
            }.start()
            switchColor()
        }, 5000)
    }

    override fun onSensorChanged(event: SensorEvent?) {
//        if (binding.rvInfinite.rotation > 45 || binding.rvInfinite.rotation < -45) {
//            return
//        }
//
//        val rot = Math.toDegrees(event!!.values[0].toDouble()).toFloat() / 25
//        if (abs(rot).toInt() < 3) {
//            return
//        }
//
//        Log.d("Sensor", rot.toString())
//        ValueAnimator.ofFloat(binding.rvInfinite.rotation, rot).apply {
//            duration = 100
//            addUpdateListener {
//                binding.rvInfinite.rotation = it.animatedValue as Float
//            }
//        }
//        binding.rvInfinite.rotation = rot
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