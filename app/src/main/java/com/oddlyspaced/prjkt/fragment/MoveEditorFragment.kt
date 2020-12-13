package com.oddlyspaced.prjkt.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oddlyspaced.prjkt.adapter.ShapeSelectAdapter
import com.oddlyspaced.prjkt.databinding.FragmentEditorDesignBinding
import com.oddlyspaced.prjkt.databinding.FragmentEditorMoveBinding
import com.oddlyspaced.prjkt.databinding.FragmentEditorResizeBinding
import com.oddlyspaced.prjkt.databinding.FragmentEditorShapeBinding
import com.oddlyspaced.prjkt.external.IconBackground
import com.oddlyspaced.prjkt.modal.ShapeItem

class MoveEditorFragment(val foreground: ImageView) : Fragment() {

    companion object {
        fun newInstance(img: ImageView): MoveEditorFragment {
            return MoveEditorFragment(img)
        }
    }

    private lateinit var binding: FragmentEditorMoveBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditorMoveBinding.inflate(layoutInflater, container, false)

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