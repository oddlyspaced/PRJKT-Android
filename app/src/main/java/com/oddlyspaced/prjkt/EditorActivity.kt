package com.oddlyspaced.prjkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(binding.frag.id, ShapeEditorFragment.newInstance(binding.textView), "hi").commit()
    }
}