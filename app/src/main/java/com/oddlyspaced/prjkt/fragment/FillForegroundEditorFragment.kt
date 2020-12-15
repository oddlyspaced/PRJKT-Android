package com.oddlyspaced.prjkt.fragment

import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentFillForegroundBinding

class FillForegroundEditorFragment(val root: Int, val foreground: ImageView) : Fragment() {

    companion object {
        fun newInstance(root: Int, img: ImageView): FillForegroundEditorFragment {
            return FillForegroundEditorFragment(root, img)
        }
    }

    private var startColor: Int = Color.BLACK
    private var endColor: Int = Color.BLACK

    private lateinit var binding: FragmentFillForegroundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFillForegroundBinding.inflate(layoutInflater, container, false)

        binding.imgFillForegroundBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.cvFillForegroundColor.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorForegroundStart")?.add(root, ColorPickerFragment.newInstance { color ->
                startColor = color
                Handler(Looper.getMainLooper()).postDelayed({
                    generateGradient()
                }, -2000)
                // foreground.setColorFilter(color)
            }, "tagColorPicker")?.commit()
        }

        binding.cvFillForegroundColor2.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorForegroundEnd")?.add(root, ColorPickerFragment.newInstance { color ->
                endColor = color
                Handler(Looper.getMainLooper()).postDelayed({
                    generateGradient()
                }, -2000)
                // foreground.setColorFilter(color)
            }, "tagColorPicker")?.commit()
        }

        return binding.root
    }

    private fun generateGradient() {
        binding.cvFillForegroundColor.setCardBackgroundColor(startColor)
        binding.cvFillForegroundColor2.setCardBackgroundColor(endColor)

        val sourceOriginal = foreground.drawable.toBitmap()
        val newLayer = Bitmap.createBitmap(sourceOriginal.height, sourceOriginal.width, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        // paint.color = Color.RED
        paint.shader = LinearGradient(
            0F,
            0F,
            0F,
            sourceOriginal.height.toFloat(),
            startColor,
            endColor,
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
        foreground.setImageBitmap(sourceOriginal)
    }

}