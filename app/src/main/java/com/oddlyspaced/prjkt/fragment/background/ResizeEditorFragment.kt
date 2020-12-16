package com.oddlyspaced.prjkt.fragment.background

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorResizeBinding
import com.oddlyspaced.prjkt.external.IconBackground

class ResizeEditorFragment(val background: IconBackground) : Fragment() {

    companion object {
        fun newInstance(img: IconBackground): ResizeEditorFragment {
            return ResizeEditorFragment(img)
        }
    }

    private lateinit var binding: FragmentEditorResizeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditorResizeBinding.inflate(layoutInflater, container, false)

        binding.imageView3.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.sliderResizeWidth.addOnChangeListener { _, value, _ ->
            background.scaleX = value
        }

        binding.sliderResizeHeight.addOnChangeListener { _, value, _ ->
            background.scaleY = value
        }

        return binding.root
    }

}