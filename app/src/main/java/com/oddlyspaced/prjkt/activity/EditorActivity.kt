package com.oddlyspaced.prjkt.activity

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding
import com.oddlyspaced.prjkt.fragment.DesignEditorFragment
import com.oddlyspaced.prjkt.fragment.MoveEditorFragment
import com.oddlyspaced.prjkt.fragment.ResizeEditorFragment
import com.oddlyspaced.prjkt.fragment.ShapeEditorFragment

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    private lateinit var shapeEditorFragment: ShapeEditorFragment
    private lateinit var resizeEditorFragment: ResizeEditorFragment
    private lateinit var designEditorFragment: DesignEditorFragment
    private lateinit var moveEditorFragment: MoveEditorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        // overlayDrawable()
        colorTest()

        shapeEditorFragment = ShapeEditorFragment.newInstance(binding.imgIconBackground)
        resizeEditorFragment = ResizeEditorFragment.newInstance(binding.imgIconBackground)
        designEditorFragment = DesignEditorFragment.newInstance(binding.imageView6)
        moveEditorFragment = MoveEditorFragment.newInstance(binding.imageView6)

        binding.txEditorShape.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.addToBackStack("shape").add(binding.frag.id, shapeEditorFragment, "tagShape").commit()
        }

        binding.txEditorSize.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.addToBackStack("size").add(binding.frag.id, resizeEditorFragment, "tagSize").commit()
        }

        binding.txEditorDesign.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("design").add(binding.frag.id, designEditorFragment, "tagDesign").commit()
        }

        binding.txEditorMove.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("move").add(binding.frag.id, moveEditorFragment, "tagMove").commit()
        }

        binding.imgIconBackground.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

    }

    private fun colorTest() {
        val sourceOriginal = binding.imageView6.drawable.toBitmap()
        val newLayer = Bitmap.createBitmap(sourceOriginal.height, sourceOriginal.width, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        val luar = LinearGradient(0F, 0F, 0F, newLayer.height.toFloat(), Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
        val rgb = Color.HSVToColor(floatArrayOf(1f, 1f, 1f))
        val dalam = LinearGradient(0F, 0F, newLayer.width.toFloat(), 0F, Color.WHITE, rgb, Shader.TileMode.CLAMP)
        val shader = ComposeShader(luar, dalam, PorterDuff.Mode.MULTIPLY)
        paint.shader = shader
        val canvas = Canvas(newLayer)
        canvas.drawRect(0F, 0F, newLayer.width.toFloat(), newLayer.height.toFloat(), paint)
        binding.imageView6.setImageBitmap(newLayer)

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

    private fun overlayDrawable() {
        val sourceOriginal = binding.imageView6.drawable.toBitmap()
        val newLayer = Bitmap.createBitmap(sourceOriginal.height, sourceOriginal.width, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        // paint.color = Color.RED
        paint.shader = LinearGradient(
            0F,
            0F,
            0F,
            sourceOriginal.height.toFloat(),
            Color.RED,
            Color.GREEN,
            Shader.TileMode.CLAMP
        )
        val canvas = Canvas(newLayer)
        //canvas.drawColor(Color.RED)
        canvas.drawPaint(paint)
        // binding.imageView6.setImageBitmap(newLayer)
        // At this point newLayer has the base map from which we need to cutout sourceOriginal path
        for (y in 0 until sourceOriginal.height) {
            for (x in 0 until sourceOriginal.width) {
                val pixelColorOriginal = sourceOriginal.getPixel(x, y)
                // check if the pixel is not transparent
                if (Color.alpha(pixelColorOriginal) != 0) {
                    val newPixel = newLayer.getPixel(x, y)
                    sourceOriginal.setPixel(x, y, Color.argb(pixelColorOriginal.alpha, newPixel.red, newPixel.green, newPixel.blue))
                }
            }
        }
        binding.imageView6.setImageBitmap(sourceOriginal)
    }


}