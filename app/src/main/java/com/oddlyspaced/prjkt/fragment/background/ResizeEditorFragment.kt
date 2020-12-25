package com.oddlyspaced.prjkt.fragment.background

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorResizeBinding
import com.oddlyspaced.prjkt.external.IconBackground
import com.oddlyspaced.prjkt.modal.IconProperties

class ResizeEditorFragment(private val background: IconBackground, private val properties: IconProperties) : Fragment() {

    companion object {
        fun newInstance(img: IconBackground, properties: IconProperties): ResizeEditorFragment {
            return ResizeEditorFragment(img, properties)
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
            properties.backgroundWidth = value
            binding.txResizeWidth.text = properties.backgroundWidth.toString()
        }

        binding.sliderResizeHeight.addOnChangeListener { _, value, _ ->
            background.scaleY = value
            properties.backgroundHeight = value
            binding.txResizeHeight.text = properties.backgroundHeight.toString()
        }

        binding.imgResizeWidthReset.setOnClickListener {
            binding.sliderResizeWidth.value = 1F
        }

        binding.imgResizeHeightReset.setOnClickListener {
            binding.sliderResizeHeight.value = 1F
        }

        binding.txResizeWidth.text = properties.backgroundWidth.toString()
        binding.txResizeHeight.text = properties.backgroundHeight.toString()

        return binding.root
    }

}