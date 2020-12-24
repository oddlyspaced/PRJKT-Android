package com.oddlyspaced.prjkt.fragment.foreground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorDesignBinding
import com.oddlyspaced.prjkt.modal.IconProperties

class DesignEditorFragment(private val foreground: ImageView, private val properties: IconProperties) : Fragment() {

    companion object {
        fun newInstance(img: ImageView, properties: IconProperties): DesignEditorFragment {
            return DesignEditorFragment(img, properties)
        }
    }

    private lateinit var binding: FragmentEditorDesignBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditorDesignBinding.inflate(layoutInflater, container, false)

        binding.imageView4.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.sliderDesignSize.addOnChangeListener { _, value, _ ->
            foreground.scaleX = value
            foreground.scaleY = value
            properties.foregroundSize = value
            binding.txDesignSize.text = value.toString()
        }

        binding.imgDesignSizeReset.setOnClickListener {
            binding.sliderDesignSize.value = 1F
            binding.txDesignSize.text = "1"
        }

        return binding.root
    }

}