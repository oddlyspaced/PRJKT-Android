package com.oddlyspaced.prjkt.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentEditorMoveBinding

class MoveEditorFragment(val foreground: ImageView) : Fragment() {

    companion object {
        fun newInstance(img: ImageView): MoveEditorFragment {
            return MoveEditorFragment(img)
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
        }

        binding.sliderEditorMoveY.addOnChangeListener { _, value, _ ->
            foreground.y = originalY + value
        }

        binding.sliderEditorMoveRotate.addOnChangeListener { _, value, _ ->
            foreground.rotation = value
        }

        return binding.root
    }

}