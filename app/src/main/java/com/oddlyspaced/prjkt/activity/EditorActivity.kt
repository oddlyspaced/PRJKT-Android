package com.oddlyspaced.prjkt.activity

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding
import com.oddlyspaced.prjkt.fragment.background.FillBackgroundEditorFragment
import com.oddlyspaced.prjkt.fragment.background.ResizeEditorFragment
import com.oddlyspaced.prjkt.fragment.background.ShapeEditorFragment
import com.oddlyspaced.prjkt.fragment.foreground.DesignEditorFragment
import com.oddlyspaced.prjkt.fragment.foreground.FillForegroundEditorFragment
import com.oddlyspaced.prjkt.fragment.foreground.MoveEditorFragment

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    private lateinit var shapeEditorFragment: ShapeEditorFragment
    private lateinit var resizeEditorFragment: ResizeEditorFragment
    private lateinit var designEditorFragment: DesignEditorFragment
    private lateinit var moveEditorFragment: MoveEditorFragment
    private lateinit var fillForegroundEditorFragment: FillForegroundEditorFragment
    private lateinit var fillBackgroundEditorFragment: FillBackgroundEditorFragment

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

        shapeEditorFragment = ShapeEditorFragment.newInstance(binding.imgIconBackground)
        resizeEditorFragment = ResizeEditorFragment.newInstance(binding.imgIconBackground)
        designEditorFragment = DesignEditorFragment.newInstance(binding.imgIconForeground)
        moveEditorFragment = MoveEditorFragment.newInstance(binding.imgIconForeground)
        fillForegroundEditorFragment = FillForegroundEditorFragment.newInstance(binding.frag.id, binding.imgIconForeground)
        fillBackgroundEditorFragment = FillBackgroundEditorFragment.newInstance(binding.frag.id, binding.imgIconBackground)

        binding.txEditorShape.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("shape").add(binding.frag.id, shapeEditorFragment, "tagShape").commit()
        }

        binding.txEditorSize.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("size").add(binding.frag.id, resizeEditorFragment, "tagSize").commit()
        }

        binding.txEditorDesign.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("design").add(binding.frag.id, designEditorFragment, "tagDesign").commit()
        }

        binding.txEditorMove.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("move").add(binding.frag.id, moveEditorFragment, "tagMove").commit()
        }

        binding.txEditorForegroundFill.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("foregroundFill").add(binding.frag.id, fillForegroundEditorFragment, "tagForegroundFill").commit()
        }

        binding.txEditorBackgroundFill.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("backgroundFill").add(binding.frag.id, fillBackgroundEditorFragment, "tagBackgroundFill").commit()
        }

        binding.imgIconBackground.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

    }

}