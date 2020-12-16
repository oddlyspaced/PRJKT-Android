package com.oddlyspaced.prjkt.fragment

import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
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

        val sourceLayer = foreground.drawable.toBitmap()
        val overlayLayer = Bitmap.createBitmap(sourceLayer.height, sourceLayer.width, Bitmap.Config.ARGB_8888)
        val canvasOverlay = Canvas(overlayLayer)
        canvasOverlay.drawColor(Color.RED)

        val mergedLayer = Bitmap.createBitmap(sourceLayer.height, sourceLayer.width, Bitmap.Config.ARGB_8888)
        val canvasMerged = Canvas(mergedLayer)

        canvasMerged.drawBitmap(sourceLayer, 0F, 0F, null)

        val paint = Paint()
        paint.shader = LinearGradient(0F, 0F, 0F, sourceLayer.height.toFloat(), startColor, endColor, Shader.TileMode.CLAMP)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvasMerged.drawRect(0F, 0F, sourceLayer.width.toFloat(), sourceLayer.height.toFloat(), paint)

        foreground.setImageBitmap(mergedLayer)
    }

}