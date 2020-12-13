package com.oddlyspaced.prjkt.activity

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding
import com.oddlyspaced.prjkt.fragment.ResizeEditorFragment
import com.oddlyspaced.prjkt.fragment.ShapeEditorFragment

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    private lateinit var shapeEditorFragment: ShapeEditorFragment
    private lateinit var resizeEditorFragment: ResizeEditorFragment

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

        overlayDrawable()

//        shapeEditorFragment = ShapeEditorFragment.newInstance(binding.imgIconBackground)
//        resizeEditorFragment = ResizeEditorFragment.newInstance(binding.imgIconBackground)
//
//        val paint = Paint().apply {
//            shader = LinearGradient(
//                0F,
//                0F,
//                binding.imageView6.measuredWidth.toFloat(),
//                0f,
//                intArrayOf(Color.RED, Color.BLACK),
//                floatArrayOf(0.0F, 0.5F),
//                Shader.TileMode.CLAMP
//            )
//            color = ContextCompat.getColor(applicationContext, R.color.blue)
//        }
//
//        binding.imageView6.setColorFilter(ContextCompat.getColor(applicationContext, R.color.blue))
//
//
//
//        // binding.imageView6.setLayerPaint(paint)
//
//        binding.txEditorShape.setOnClickListener {
//            val transaction = supportFragmentManager.beginTransaction()
//            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//            transaction.addToBackStack("shape").add(binding.frag.id, shapeEditorFragment, "tagShape").commit()
//        }
//
//        binding.txEditorSize.setOnClickListener {
//            val transaction = supportFragmentManager.beginTransaction()
//            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//            transaction.addToBackStack("size").add(binding.frag.id, resizeEditorFragment, "tagSize").commit()
//        }
//
//        binding.imgIconBackground.setOnClickListener {
//            supportFragmentManager.popBackStack()
//        }

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
                    sourceOriginal.setPixel(x, y, newLayer.getPixel(x, y))
                }
            }
        }
        binding.imageView6.setImageBitmap(sourceOriginal)
    }


}