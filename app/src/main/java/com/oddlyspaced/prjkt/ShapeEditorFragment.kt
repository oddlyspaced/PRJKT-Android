package com.oddlyspaced.prjkt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding

class ShapeEditorFragment(val textView: TextView): Fragment() {

    companion object {
        fun newInstance(txt: TextView): ShapeEditorFragment {
            return ShapeEditorFragment(txt)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = FragmentEditorShapeBinding.inflate(layoutInflater, container, false)
        val items = arrayListOf(false, false, true, true)
        layout.rvShapes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layout.rvShapes.adapter = ItemSelectAdapter(items)
        return layout.root
    }
}