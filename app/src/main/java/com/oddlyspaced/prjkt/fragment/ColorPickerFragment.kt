package com.oddlyspaced.prjkt.fragment

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.*
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.*

class ColorPickerFragment(val onColorChanged: (Int) -> Unit) : Fragment() {

    companion object {
        fun newInstance(onColorChanged: (Int) -> Unit): ColorPickerFragment {
            return ColorPickerFragment(onColorChanged)
        }
    }

    private lateinit var binding: FragmentColorPickerBinding

    private var R = 0
    private var G = 0
    private var B = 0
    private var A = 0
    private var active = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentColorPickerBinding.inflate(layoutInflater, container, false)
        bruh()

        binding.imageView7.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val cards = arrayOf(binding.cardColorPickerRed, binding.cardColorPickerGreen, binding.cardColorPickerBlue, binding.cardColorPickerAlpha)

        cards.forEachIndexed { index, card ->
            card.setOnClickListener {
                active = index
                activateCard()
                binding.sliderColorPicker.value = when(active) {
                    0 -> R
                    1 -> G
                    2 -> B
                    3 -> A
                    else -> R
                }.toFloat()
            }
        }

        binding.sliderColorPicker.addOnChangeListener { _, value, _ ->
            when (active) {
                0 -> R = value.toInt()
                1 -> G = value.toInt()
                2 -> B = value.toInt()
                3 -> A = value.toInt()
            }
            applyHue(Color.argb(A, R, G, B))
        }

        return binding.root
    }

    private fun activateCard() {
        val cards = arrayOf(binding.cardColorPickerRed, binding.cardColorPickerGreen, binding.cardColorPickerBlue, binding.cardColorPickerAlpha)
        cards.forEach {
            it.strokeColor = ContextCompat.getColor(context!!, com.oddlyspaced.prjkt.R.color.background_light)
            it.invalidate()
        }
        cards[active].strokeColor = ContextCompat.getColor(context!!, com.oddlyspaced.prjkt.R.color.blue)
        cards[active].invalidate()
    }

    private var x = 0
    private var y = 0

    private fun bruh() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (binding.imgColorPicker.measuredHeight > 0) {
                applyHue(Color.GREEN)
            }
            else {
                bruh()
            }
        }, -2000)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun applyHue(color: Int) {
        val newLayer = Bitmap.createBitmap(binding.imgColorPicker.width, binding.imgColorPicker.height, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        val luar = LinearGradient(0F, 0F, 0F, newLayer.height.toFloat(), Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
        val dalam = LinearGradient(0F, 0F, newLayer.width.toFloat(), 0F, Color.WHITE, color, Shader.TileMode.CLAMP)
        val shader = ComposeShader(luar, dalam, PorterDuff.Mode.MULTIPLY)
        paint.shader = shader
        val canvas = Canvas(newLayer)
        canvas.drawRect(0F, 0F, newLayer.width.toFloat(), newLayer.height.toFloat(), paint)

        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, newLayer)
        val roundPx = newLayer.width * 0.03F
        roundedBitmapDrawable.cornerRadius = roundPx

        binding.imgColorPicker.setImageDrawable(roundedBitmapDrawable)

        binding.imgColorPicker.setOnTouchListener { view, motionEvent ->
            x = motionEvent.x.toInt()
            if (x < 0) {
                x = 0
            }
            if (x > newLayer.width - 1) {
                x = newLayer.width - 1
            }

            y = motionEvent.y.toInt()
            if (y < 0) {
                y = 0
            }
            if (y > newLayer.height - 1) {
                y = newLayer.height - 1
            }

            val pixel = newLayer.getPixel(x, y)
            val hex = String.format("%02x%02x%02x", pixel.red, pixel.green, pixel.blue)
            binding.txColorPickerHex.text = hex
            binding.viewIndicator.x = x.toFloat() - (binding.viewIndicator.width/2)
            binding.viewIndicator.y = y.toFloat() - (binding.viewIndicator.height/2)
            onColorChanged(Color.argb(pixel.alpha, pixel.red, pixel.green, pixel.blue))
            true
        }

        val pixel = newLayer.getPixel(x, y)
        val hex = String.format("%02x%02x%02x", pixel.red, pixel.green, pixel.blue)
        binding.txColorPickerHex.text = hex
        onColorChanged(Color.argb(pixel.alpha, pixel.red, pixel.green, pixel.blue))
    }

}