package com.oddlyspaced.prjkt.activity

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.*
import androidx.core.graphics.drawable.toBitmap
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding
import com.oddlyspaced.prjkt.fragment.*

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    private lateinit var shapeEditorFragment: ShapeEditorFragment
    private lateinit var resizeEditorFragment: ResizeEditorFragment
    private lateinit var designEditorFragment: DesignEditorFragment
    private lateinit var moveEditorFragment: MoveEditorFragment
    private lateinit var fillForegroundEditorFragment: FillForegroundEditorFragment

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
        designEditorFragment = DesignEditorFragment.newInstance(binding.imageView6)
        moveEditorFragment = MoveEditorFragment.newInstance(binding.imageView6)
        fillForegroundEditorFragment = FillForegroundEditorFragment.newInstance(binding.frag.id, binding.imageView6)

        supportFragmentManager.beginTransaction().addToBackStack("fillForeground")
            .add(binding.frag.id, FillForegroundEditorFragment.newInstance(binding.frag.id, binding.imageView6), "tagForegroundFill").commit()

        binding.txEditorShape.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.addToBackStack("shape").add(binding.frag.id, shapeEditorFragment, "tagShape").commit()
        }

        binding.txEditorSize.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.addToBackStack("size").add(binding.frag.id, resizeEditorFragment, "tagSize").commit()
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

        binding.imgIconBackground.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

    }

    private fun overlayDrawable() {
        val sourceOriginal = binding.imageView6.drawable.toBitmap()
        val newLayer = Bitmap.createBitmap(sourceOriginal.height, sourceOriginal.width, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        // paint.color = Color.RED
        paint.shader = LinearGradient(
            0F,
            0F,
            0F,
            sourceOriginal.height.toFloat(),
            Color.RED,
            Color.GREEN,
            Shader.TileMode.CLAMP
        )
        val canvas = Canvas(newLayer)
        //canvas.drawColor(Color.RED)
        canvas.drawPaint(paint)
        // binding.imageView6.setImageBitmap(newLayer)
        // At this point newLayer has the base map from which we need to cutout sourceOriginal path
        for (y in 0 until sourceOriginal.height) {
            for (x in 0 until sourceOriginal.width) {
                val pixelColorOriginal = sourceOriginal.getPixel(x, y)
                // check if the pixel is not transparent
                if (Color.alpha(pixelColorOriginal) != 0) {
                    val newPixel = newLayer.getPixel(x, y)
                    sourceOriginal.setPixel(x, y, Color.argb(pixelColorOriginal.alpha, newPixel.red, newPixel.green, newPixel.blue))
                }
            }
        }
        binding.imageView6.setImageBitmap(sourceOriginal)
    }


}