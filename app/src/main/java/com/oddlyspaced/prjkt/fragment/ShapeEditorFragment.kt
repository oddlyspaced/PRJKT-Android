package com.oddlyspaced.prjkt.fragment

import android.os.Bundle
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditorShapeBinding.inflate(layoutInflater, container, false)
        val items = arrayListOf(
            ShapeItem( // circle
                false
            ) {
                hideRadiusSection()
                hideSidesSection()
                background.cornerRadius = 360F
            },
            ShapeItem( // square
                false
            ) {
                hideRadiusSection()
                hideSidesSection()
                background.numberOfSides = 4
                background.cornerRadius = 0F
            },
            ShapeItem( // oval
                false
            ) {
                hideSidesSection()
                showRadiusSection()
                background.numberOfSides = 4
                background.cornerRadius = 50F
            },
            ShapeItem( // polygon
                false
            ) {
                showRadiusSection()
                showSidesSection()
                background.numberOfSides = 8
                background.cornerRadius = 50F
            },
        )
        binding.rvShapes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvShapes.adapter = ShapeSelectAdapter(items)

        binding.sliderShapeRotation.addOnChangeListener { _, value, _ ->
            background.rotation = value
        }

        binding.sliderShapeRadius.addOnChangeListener { _, value, _ ->
            background.cornerRadius = value
        }

        binding.sliderShapeSides.addOnChangeListener { _, value, _ ->
            background.numberOfSides = value.toInt()
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

}