package com.oddlyspaced.prjkt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.EyeCandyAdapter
import com.oddlyspaced.prjkt.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var adapt: EyeCandyAdapter
    private lateinit var lm: LinearLayoutManager
    private val list = arrayListOf("1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5","1", "2,", "3,", "4", "5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lm = LinearLayoutManager(applicationContext)

        adapt = EyeCandyAdapter(list)

        binding.rvInfinite.apply {
            setHasFixedSize(true)
            layoutManager = lm
            adapter = adapt
        }

        infiniteScroll()
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
                      current
                  }
              }
            )
            current++
            infiniteScroll()
        }, 100)
    }
}