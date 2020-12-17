package com.oddlyspaced.prjkt.fragment.background

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentFillBackgroundBinding
import com.oddlyspaced.prjkt.external.IconBackground
import com.oddlyspaced.prjkt.fragment.ColorPickerFragment
import com.oddlyspaced.prjkt.modal.IconProperties

class FillBackgroundEditorFragment(val root: Int, val background: IconBackground, private val properties: IconProperties) : Fragment() {

    companion object {
        fun newInstance(root: Int, img: IconBackground, properties: IconProperties): FillBackgroundEditorFragment {
            return FillBackgroundEditorFragment(root, img, properties)
        }
    }

    private var startColor: String = "#FF000000"
    private var endColor: String = "#FF000000"

    private lateinit var binding: FragmentFillBackgroundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFillBackgroundBinding.inflate(layoutInflater, container, false)

        binding.imgFillBackgroundBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.cvFillBackgroundColor.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorBackgroundStart")?.add(root, ColorPickerFragment.newInstance { color ->
                binding.cvFillBackgroundColor.setCardBackgroundColor(color.toColorInt())
                startColor = color
                setGradient()
            }, "tagColorPicker")?.commit()
        }

        binding.cvFillBackgroundColor2.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorBackgroundEnd")?.add(root, ColorPickerFragment.newInstance { color ->
                binding.cvFillBackgroundColor2.setCardBackgroundColor(color.toColorInt())
                endColor = color
                setGradient()
            }, "tagColorPicker")?.commit()
        }

        return binding.root
    }

    // sets gradient to icon background
    private fun setGradient() {
        val paint = Paint()
        paint.shader = LinearGradient(
            0F,
            0F,
            0F,
            background.height.toFloat(),
            startColor.toColorInt(),
            endColor.toColorInt(),
            Shader.TileMode.CLAMP
        )
        background.polygonFillPaint = paint
    }

}