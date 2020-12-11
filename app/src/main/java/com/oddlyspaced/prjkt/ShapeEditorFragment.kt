package com.oddlyspaced.prjkt

import android.graphics.Canvas
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding
import com.stkent.polygondrawingutil.PolygonDrawingUtil

class ShapeEditorFragment(val background: IconBackground) : Fragment() {

    companion object {
        fun newInstance(img: IconBackground): ShapeEditorFragment {
            return ShapeEditorFragment(img)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = FragmentEditorShapeBinding.inflate(layoutInflater, container, false)
        val items = arrayListOf(false, false, true, true)
        layout.rvShapes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layout.rvShapes.adapter = ItemSelectAdapter(items)

        layout.sliderShapeRotation.addOnChangeListener { _, value, _ ->
            background.cornerRadius = value
            background.scale = 1.25F
        }

        background.numberOfSides = 4
        background.cornerRadius = 10F

        return layout.root
    }
}