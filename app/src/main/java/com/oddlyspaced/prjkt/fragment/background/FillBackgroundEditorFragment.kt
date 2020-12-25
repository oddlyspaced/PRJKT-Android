package com.oddlyspaced.prjkt.fragment.background

import android.graphics.*
import android.os.Bundle
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

    private lateinit var binding: FragmentFillBackgroundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFillBackgroundBinding.inflate(layoutInflater, container, false)

        // having these lines here is necessary otherwise w and h are 0
        val w = background.width.toFloat()
        val h = background.height.toFloat()

        val angles = arrayOf(0, 45, 90, 135, 180, 225, 270, 360)

        // -180 -135 -90 -45 0 45 90 135 180
        val startPointers = arrayOf(
            PointF(w/2F, 0F),
            PointF(w, 0F),
            PointF(w, h/2F),
            PointF(w, h),
            PointF(w/2F, h),
            PointF(0F, h),
            PointF(0F, h/2F),
            PointF(0F, 0F),
        )
        val endPointers  = arrayOf(
            PointF(w/2F, h),
            PointF(0F, h),
            PointF(0F, h/2F),
            PointF(0F, 0F),
            PointF(w/2F, 0F),
            PointF(w, 0F),
            PointF(w, h/2F),
            PointF(w, h),
        )


        binding.imgFillBackgroundBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.cvFillBackgroundColor.setCardBackgroundColor(properties.backgroundStartColor.toColorInt())
        binding.cvFillBackgroundColor2.setCardBackgroundColor(properties.backgroundEndColor.toColorInt())

        binding.cvFillBackgroundColor.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorBackgroundStart")?.add(root, ColorPickerFragment.newInstance(properties.backgroundStartColor) { color ->
                binding.cvFillBackgroundColor.setCardBackgroundColor(color.toColorInt())
                properties.backgroundStartColor = color
                setGradient(startPointers[properties.backgroundAngle], endPointers[properties.backgroundAngle])
            }, "tagColorPicker")?.commit()
        }

        binding.cvFillBackgroundColor2.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("colorBackgroundEnd")?.add(root, ColorPickerFragment.newInstance(properties.backgroundEndColor) { color ->
                binding.cvFillBackgroundColor2.setCardBackgroundColor(color.toColorInt())
                properties.backgroundEndColor = color
                setGradient(startPointers[properties.backgroundAngle], endPointers[properties.backgroundAngle])
            }, "tagColorPicker")?.commit()
        }

        binding.sliderFillBackgroundAngle.addOnChangeListener { _, value, _ ->
            properties.backgroundAngle = value.toInt()
            binding.txFillBackgroundAngle.text = angles[properties.backgroundAngle].toString()
            setGradient(startPointers[properties.backgroundAngle], endPointers[properties.backgroundAngle])
        }

        binding.txFillBackgroundAngle.text = angles[properties.backgroundAngle].toString()

        return binding.root
    }

    // sets gradient to icon background
    private fun setGradient(start: PointF, end: PointF) {
        val paint = Paint()
        paint.shader = LinearGradient(
            start.x,
            start.y,
            end.x,
            end.y,
            properties.backgroundStartColor.toColorInt(),
            properties.backgroundEndColor.toColorInt(),
            Shader.TileMode.CLAMP
        )
        background.polygonFillPaint = paint
    }

}