package com.oddlyspaced.prjkt.fragment.foreground

import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentFillForegroundBinding
import com.oddlyspaced.prjkt.fragment.ColorPickerFragment
import com.oddlyspaced.prjkt.modal.IconProperties

class FillForegroundEditorFragment(val root: Int, private val foreground: ImageView, private val properties: IconProperties) : Fragment() {

    companion object {
        fun newInstance(root: Int, img: ImageView, properties: IconProperties): FillForegroundEditorFragment {
            return FillForegroundEditorFragment(root, img, properties)
        }
    }

    private lateinit var binding: FragmentFillForegroundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFillForegroundBinding.inflate(layoutInflater, container, false)

        binding.imgFillForegroundBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.cvFillForegroundColor.setCardBackgroundColor(properties.foregroundStartColor.toColorInt())
        binding.cvFillForegroundColor2.setCardBackgroundColor(properties.foregroundEndColor.toColorInt())

        binding.cvFillForegroundColor.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorForegroundStart")?.add(root, ColorPickerFragment.newInstance(properties.foregroundStartColor) { color ->
                properties.foregroundStartColor = color
                Handler(Looper.getMainLooper()).postDelayed({
                    generateGradient()
                }, -2000)
                // foreground.setColorFilter(color)
            }, "tagColorPicker")?.commit()
        }

        binding.cvFillForegroundColor2.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorForegroundEnd")?.add(root, ColorPickerFragment.newInstance(properties.foregroundEndColor) { color ->
                properties.foregroundEndColor = color
                Handler(Looper.getMainLooper()).postDelayed({
                    generateGradient()
                }, -2000)
                // foreground.setColorFilter(color)
            }, "tagColorPicker")?.commit()
        }

        return binding.root
    }

    private fun generateGradient() {
        binding.cvFillForegroundColor.setCardBackgroundColor(properties.foregroundStartColor.toColorInt())
        binding.cvFillForegroundColor2.setCardBackgroundColor(properties.foregroundEndColor.toColorInt())

        ColorPickerFragment.applyForegroundGradient(foreground, properties.foregroundStartColor, properties.foregroundEndColor)
    }

}