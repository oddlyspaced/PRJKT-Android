package com.oddlyspaced.prjkt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.adapter.EyeCandyAdapter
import com.oddlyspaced.prjkt.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvInfinite.apply {
            setHasFixedSize(true)
            layoutManager = LoopingLayoutManager(
                applicationContext, LoopingLayoutManager.VERTICAL,
                false,
            )
            adapter = EyeCandyAdapter(arrayListOf("fiest", "second", "third"))
        }
    }
}