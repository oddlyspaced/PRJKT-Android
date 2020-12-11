package com.oddlyspaced.prjkt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding

class ShapeEditorFragment(val textView: TextView): Fragment() {

    companion object {
        fun newInstance(txt: TextView): ShapeEditorFragment {
            return ShapeEditorFragment(txt)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = FragmentEditorShapeBinding.inflate(layoutInflater)
        layout.button.text = "Wow this works"
        layout.button.setOnClickListener {
            textView.text = "Dang"
        }
        return layout.root
    }
}