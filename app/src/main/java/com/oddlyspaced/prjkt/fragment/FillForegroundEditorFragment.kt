package com.oddlyspaced.prjkt.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.FragmentFillForegroundBinding

class FillForegroundEditorFragment(val root: Int, val foreground: ImageView) : Fragment() {

    companion object {
        fun newInstance(root: Int, img: ImageView): FillForegroundEditorFragment {
            return FillForegroundEditorFragment(root, img)
        }
    }

    private lateinit var binding: FragmentFillForegroundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFillForegroundBinding.inflate(layoutInflater, container, false)

        binding.imgFillForegroundBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        binding.cvFillForegroundColor.setOnClickListener {
            fragmentManager?.beginTransaction()?.addToBackStack("move")?.add(root, ColorPickerFragment.newInstance { color ->
                foreground.setColorFilter(color)
            }, "tagColorPicker")?.commit()
        }

        return binding.root
    }

}