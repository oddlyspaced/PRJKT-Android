package com.oddlyspaced.prjkt

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.widget.ImageViewCompat
import com.oddlyspaced.prjkt.utils.kellinwood.zipsigner.ZipSigner
import kotlinx.android.synthetic.main.activity_main.*
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    private val template by lazy {
        File(applicationContext.externalCacheDir!!.path, "template")
    }

    private val templateIconPath by lazy {
        File(applicationContext.externalCacheDir!!.path, "template/res/drawable-nodpi-v4").path
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        template.deleteRecursively()

        if (!template.exists()) {
            copyTemplate()
        }

        if (!File(applicationContext.externalCacheDir!!.path, "keys").exists()) {
            copyKeys()
        }

        setupOnClick()
    }

    private val icons = arrayListOf("chrome", "spotify", "camera", "calendar", "contacts", "clock", "phone", "netflix", "playstore", "message", "whatsapp")

    private fun setupOnClick() {
        btnRender.setOnClickListener {
            txStatus.text = "Rendered!"
            for (icon in icons) {
                imgForeground.setImageResource(resources.getIdentifier(icon, "drawable", applicationContext.packageName))
                renderIcon("$icon")
            }
        }

        btnPack.setOnClickListener {
            packApk()
            txStatus.text = "Apk Packing Done"
        }

        btnApply.setOnClickListener {
            try {
                imgForeground.setColorFilter(editTextForeground.text.toString().toColorInt())
                // imgForeground.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                // imgForeground.setTint(editTextForeground.text.toString())
                cvBackground.setCardBackgroundColor(editTextBackground.text.toString().toColorInt())
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun copyTemplate() {
        try {
            val templateFile = assets.open("template.zip")
            val outFile = File(applicationContext.externalCacheDir!!.path + "/template.zip")
            templateFile.copyTo(outFile.outputStream())
            templateFile.close()
            ZipUtil.explode(outFile)
            outFile.renameTo(File(applicationContext.externalCacheDir!!.path + "/template"))

        } catch (e: Exception) {
            Log.e("MAIN", e.toString())
            e.printStackTrace()
        }
    }

    private fun copyKeys() {
        try {
            val keysFile = assets.open("keys.zip")
            val outFile = File(applicationContext.externalCacheDir!!.path + "/keys.zip")
            keysFile.copyTo(outFile.outputStream())
            keysFile.close()
            ZipUtil.explode(outFile)
            outFile.renameTo(File(applicationContext.externalCacheDir!!.path + "/keys"))
        } catch (e: Exception) {
            Log.e("MAIN", e.toString())
            e.printStackTrace()
        }
    }

    private fun renderIcon(iconName: String) {
        val bitmap = Bitmap.createBitmap(cvBackground.measuredWidth, cvBackground.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        cvBackground.draw(canvas)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, File(templateIconPath, "$iconName.png").outputStream())
    }

    private fun packApk() {
        try {
            val app = File(applicationContext.externalCacheDir!!.path, "app.zip")
            ZipUtil.pack(template, app)
            // Sign with the built-in default test key/certificate.
            val zipSigner = ZipSigner()
            zipSigner.context = applicationContext
            zipSigner.keymode = "testkey"
            //         File publicKey = new File(Environment.getExternalStorageDirectory().getPath() + "/keys/"+name+".x509.pem");
            val outFile = File(applicationContext.externalCacheDir!!.path, "app-signed.apk")
            zipSigner.signZip(app.path, outFile.path)
            Toast.makeText(applicationContext, "SUCCESS", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_LONG).show()
            Log.e("ERROR", e.toString())
            e.printStackTrace()
        }
    }

    fun ImageView.setTint(colorRes: String) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(Color.parseColor(colorRes)))
    }

}