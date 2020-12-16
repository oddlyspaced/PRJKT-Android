package com.oddlyspaced.prjkt.fragment.background

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.ShapeSelectAdapter
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding
import com.oddlyspaced.prjkt.external.IconBackground
import com.oddlyspaced.prjkt.modal.ShapeItem

class ShapeEditorFragment(val background: IconBackground) : Fragment() {

    companion object {
        fun newInstance(img: IconBackground): ShapeEditorFragment {
            return ShapeEditorFragment(img)
        }
    }

    private lateinit var binding: FragmentEditorShapeBinding
    private var radius = 0F
    private var sides = 360
    private var rotation = 45F
    private var active = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditorShapeBinding.inflate(layoutInflater, container, false)
        binding.imageView.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        Log.e("Properties", "$radius, $sides, $rotation")

        applyProperties()

        val items = arrayListOf(
            ShapeItem( // circle
                false
            ) {
                active = 0
                sides = 360
                radius = 360F
                applyProperties()
                hideRadiusSection()
                hideSidesSection()
            },
            ShapeItem( // oval
                false
            ) {
                active = 1
                sides = 4
                radius = 50F
                applyProperties()
                hideSidesSection()
                showRadiusSection()
            },
            ShapeItem( // polygon
                false
            ) {
                active = 2
                sides = 8
                radius = 50F
                applyProperties()
                showRadiusSection()
                showSidesSection()
            },
        )
        items[active].isActive = true
        binding.rvShapes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvShapes.adapter = ShapeSelectAdapter(items)

        binding.imgShapeRadiusReset.setOnClickListener {
            radius = 50F
            applyProperties()
        }

        binding.imgShapeRotationReset.setOnClickListener {
            rotation = 45F
            applyProperties()
        }

        binding.imgShapeSidesReset.setOnClickListener {
            sides = 3
            applyProperties()
        }

        binding.sliderShapeRotation.addOnChangeListener { _, value, _ ->
            rotation = value
            applyProperties()
        }

        binding.sliderShapeRadius.addOnChangeListener { _, value, _ ->
            radius = value
            applyProperties()
        }

        binding.sliderShapeSides.addOnChangeListener { _, value, _ ->
            sides = value.toInt()
            applyProperties()
        }

        applyProperties()
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
        background.cornerRadius = radius
        background.numberOfSides = sides
        background.polygonRotation = rotation
    }

}