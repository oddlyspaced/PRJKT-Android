package com.oddlyspaced.prjkt.fragment

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.oddlyspaced.prjkt.databinding.*

class ColorPickerFragment : Fragment() {

    companion object {
        fun newInstance(): ColorPickerFragment {
            return ColorPickerFragment()
        }
    }

    private lateinit var binding: FragmentColorPickerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentColorPickerBinding.inflate(layoutInflater, container, false)
        bruh()
        return binding.root
    }

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
        binding.imgColorPicker.setImageBitmap(newLayer)

        binding.imgColorPicker.setOnTouchListener { view, motionEvent ->
            var x = motionEvent.x.toInt()
            if (x < 0) {
                x = 0
            }
            if (x > newLayer.width - 1) {
                x = newLayer.width - 1
            }

            var y = motionEvent.y.toInt()
            if (y < 0) {
                y = 0
            }
            if (y > newLayer.height - 1) {
                y = newLayer.height - 1
            }

            val pixel = newLayer.getPixel(x, y)
            binding.txColorPickerHex.text = pixel.red.toString() + ", " + pixel.green.toString() + ", " + pixel.blue.toString()
            binding.txColorPickerHex.setTextColor(Color.rgb(pixel.red, pixel.green, pixel.blue))
            binding.viewIndicator.x = x.toFloat() - (binding.viewIndicator.width/2)
            binding.viewIndicator.y = y.toFloat() - (binding.viewIndicator.height/2)
            true
        }

        /*
        if (paint == null) {
			paint = new Paint();
			luar = new LinearGradient(0.f, 0.f, 0.f, this.getMeasuredHeight(), 0xffffffff, 0xff000000, TileMode.CLAMP);
		}
		int rgb = Color.HSVToColor(color);
		Shader dalam = new LinearGradient(0.f, 0.f, this.getMeasuredWidth(), 0.f, 0xffffffff, rgb, TileMode.CLAMP);
		ComposeShader shader = new ComposeShader(luar, dalam, PorterDuff.Mode.MULTIPLY);
		paint.setShader(shader);
		canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(), this.getMeasuredHeight(), paint);
         */
    }

}