package com.oddlyspaced.prjkt.fragment.foreground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorMoveBinding
import com.oddlyspaced.prjkt.modal.IconProperties

class MoveEditorFragment(private val foreground: ImageView, private val properties: IconProperties) : Fragment() {

    companion object {
        fun newInstance(img: ImageView, properties: IconProperties): MoveEditorFragment {
            return MoveEditorFragment(img, properties)
        }
    }

    private lateinit var binding: FragmentEditorMoveBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditorMoveBinding.inflate(layoutInflater, container, false)

        binding.imageView5.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val originalX = foreground.x
        val originalY = foreground.y

        binding.sliderEditorMoveX.addOnChangeListener { _, value, _ ->
            foreground.x = originalX + value
            properties.foregroundMoveX = value
        }

        binding.sliderEditorMoveY.addOnChangeListener { _, value, _ ->
            foreground.y = originalY + value
            properties.foregroundMoveY = value
        }

        binding.sliderEditorMoveRotate.addOnChangeListener { _, value, _ ->
            foreground.rotation = value
            properties.foregroundRotate = value
        }

        return binding.root
    }

}