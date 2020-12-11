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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = FragmentEditorShapeBinding.inflate(layoutInflater, container, false)
        val items = arrayListOf(
            ShapeItem(
                false
            ) {
                background.cornerRadius = 360F
            },
            ShapeItem(
                false
            ) {
                background.numberOfSides = 4
                background.cornerRadius = 0F
            },
            ShapeItem(
                false
            ) {
                background.numberOfSides = 4
                background.cornerRadius = 50F
            },
            ShapeItem(
                false
            ) {
                background.numberOfSides = 8
                background.cornerRadius = 50F
            },
        )
        layout.rvShapes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layout.rvShapes.adapter = ShapeSelectAdapter(items)

        layout.sliderShapeRotation.addOnChangeListener { _, value, _ ->
            background.rotation = value
        }

        return layout.root
    }
}