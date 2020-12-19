package com.oddlyspaced.prjkt.fragment.background

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.R
import com.oddlyspaced.prjkt.adapter.ShapeSelectAdapter
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding
import com.oddlyspaced.prjkt.external.IconBackground
import com.oddlyspaced.prjkt.modal.IconProperties
import com.oddlyspaced.prjkt.modal.ShapeItem

class ShapeEditorFragment(val background: IconBackground, private val properties: IconProperties) : Fragment() {

    companion object {
        fun newInstance(img: IconBackground, properties: IconProperties): ShapeEditorFragment {
            return ShapeEditorFragment(img, properties)
        }
    }

    private lateinit var binding: FragmentEditorShapeBinding
    private var active = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditorShapeBinding.inflate(layoutInflater, container, false)
        binding.imageView.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val items = arrayListOf(
            ShapeItem( // circle
                R.drawable.circle,
                false
            ) {
                active = 0
                properties.backgroundSides = 360
                properties.backgroundRadius = 360F
                applyProperties()
                hideRadiusSection()
                hideSidesSection()
            },
            ShapeItem( // rounded square
                R.drawable.ic_rounded_square,
                false
            ) {
                active = 1
                properties.backgroundSides = 4
                properties.backgroundRadius = 50F
                applyProperties()
                hideSidesSection()
                showRadiusSection()
            },
            ShapeItem( // polygon
                R.drawable.ic_hex,
                false
            ) {
                active = 2
                properties.backgroundSides = 8
                properties.backgroundRadius = 50F
                applyProperties()
                showRadiusSection()
                showSidesSection()
            },
        )
        items[active].isActive = true
        binding.rvShapes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvShapes.adapter = ShapeSelectAdapter(items)

        binding.imgShapeRadiusReset.setOnClickListener {
            properties.backgroundRadius = 50F
            applyProperties()
        }

        binding.imgShapeRotationReset.setOnClickListener {
            properties.backgroundRotation = 45F
            applyProperties()
        }

        binding.imgShapeSidesReset.setOnClickListener {
            properties.backgroundSides = 3
            applyProperties()
        }

        binding.sliderShapeRotation.addOnChangeListener { _, value, fromUser ->
            if (!fromUser)
                return@addOnChangeListener
            properties.backgroundRotation = value
            applyProperties()
        }

        binding.sliderShapeRadius.addOnChangeListener { _, value, fromUser ->
            if (!fromUser)
                return@addOnChangeListener
            properties.backgroundRadius = value
            applyProperties()
        }

        binding.sliderShapeSides.addOnChangeListener { _, value, fromUser ->
            if (!fromUser)
                return@addOnChangeListener
            properties.backgroundSides = value.toInt()
            applyProperties()
        }

        when {
            items[0].isActive -> {
                hideRadiusSection()
                hideSidesSection()
            }
            items[1].isActive -> {
                hideSidesSection()
                showRadiusSection()
            }
            items[2].isActive -> {
                showRadiusSection()
                showSidesSection()
            }
        }

        return binding.root
    }

    private fun showRadiusSection() {
        binding.consShapeRadius.visibility = View.VISIBLE
    }

    private fun showSidesSection() {
        binding.consShapeSides.visibility = View.VISIBLE
    }

    private fun hideRadiusSection() {
        binding.consShapeRadius.visibility = View.GONE
    }

    private fun hideSidesSection() {
        binding.consShapeSides.visibility = View.GONE
    }

    private fun applyProperties() {
        background.cornerRadius = properties.backgroundRadius
        background.numberOfSides = properties.backgroundSides
        background.polygonRotation = properties.backgroundRotation
    }

}