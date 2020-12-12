package com.oddlyspaced.prjkt.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding
import com.oddlyspaced.prjkt.fragment.ResizeEditorFragment
import com.oddlyspaced.prjkt.fragment.ShapeEditorFragment

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        binding.txEditorShape.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.addToBackStack("shape").add(binding.frag.id, ShapeEditorFragment.newInstance(binding.imgIconBackground), "tagShape").commit()
        }

        binding.txEditorSize.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.addToBackStack("size").add(binding.frag.id, ResizeEditorFragment.newInstance(binding.imgIconBackground), "tagSize").commit()
        }

        binding.imgIconBackground.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

    }
}