package com.oddlyspaced.prjkt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.EyeCandyAdapter
import com.oddlyspaced.prjkt.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var adapt: EyeCandyAdapter
    private lateinit var lm: LinearLayoutManager
    private val list = arrayListOf<Int>()

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
}