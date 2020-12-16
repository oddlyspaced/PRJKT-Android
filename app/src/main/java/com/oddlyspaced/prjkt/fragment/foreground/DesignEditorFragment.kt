package com.oddlyspaced.prjkt.fragment.foreground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorDesignBinding

class DesignEditorFragment(val foreground: ImageView) : Fragment() {

    companion object {
        fun newInstance(img: ImageView): DesignEditorFragment {
            return DesignEditorFragment(img)
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
        }

        return binding.root
    }

}