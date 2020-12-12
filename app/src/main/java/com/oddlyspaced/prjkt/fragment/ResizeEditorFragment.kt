package com.oddlyspaced.prjkt.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.ShapeSelectAdapter
import com.oddlyspaced.prjkt.databinding.FragmentEditorResizeBinding
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding
import com.oddlyspaced.prjkt.external.IconBackground
import com.oddlyspaced.prjkt.modal.ShapeItem

class ResizeEditorFragment(val background: IconBackground) : Fragment() {

    companion object {
        fun newInstance(img: IconBackground): ResizeEditorFragment {
            return ResizeEditorFragment(img)
        }
    }

    private lateinit var binding: FragmentEditorResizeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditorResizeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}