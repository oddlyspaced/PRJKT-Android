package com.oddlyspaced.prjkt.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import com.oddlyspaced.prjkt.BuildConfig
import com.oddlyspaced.prjkt.databinding.ActivityMainBinding
import com.oddlyspaced.prjkt.utils.kellinwood.zipsigner.ZipSigner
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val template by lazy {
        File(applicationContext.externalCacheDir!!.path, "template")
    }

    private val templateIconPath by lazy {
        File(applicationContext.externalCacheDir!!.path, "template/res/drawable-nodpi-v4").path
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startActivity(Intent(applicationContext, HomeActivity::class.java))

//        template.deleteRecursively()
//
//        if (!template.exists()) {
//            copyTemplate()
//        }
//
//        if (!File(applicationContext.externalCacheDir!!.path, "keys").exists()) {
//            copyKeys()
//        }
//
//        checkInstallPermission()
//        setupOnClick()
    }

    private val icons = arrayListOf(
        "chrome",
        "spotify",
        "camera",
        "calendar",
        "contacts",
        "clock",
        "phone",
        "netflix",
        "playstore",
        "message",
        "whatsapp",
        "telegram",
        "instagram",
        "gmail",
        "google",
        "settings"
    )

    private fun setupOnClick() {
        binding.btnRender.setOnClickListener {
            binding.txStatus.text = "Rendered!"
            for (icon in icons) {
                binding.imgForeground.setImageResource(resources.getIdentifier(icon, "drawable", applicationContext.packageName))
                renderIcon("$icon")
            }
        }

        binding.btnPack.setOnClickListener {
            packApk()
            binding.txStatus.text = "Apk Packing Done"
        }

        binding.btnApply.setOnClickListener {
            try {
                binding.imgForeground.setColorFilter(binding.editTextForeground.text.toString().toColorInt())
                // imgForeground.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                // imgForeground.setTint(editTextForeground.text.toString())
                binding.cvBackground.setCardBackgroundColor(binding.editTextBackground.text.toString().toColorInt())
            } catch (e: Exception) {
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
        val bitmap = Bitmap.createBitmap(binding.cvBackground.measuredWidth, binding.cvBackground.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.cvBackground.draw(canvas)
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

            installApk(outFile)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_LONG).show()
            Log.e("ERROR", e.toString())
            e.printStackTrace()
        }
    }

    private fun checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                        Uri.parse(
                            String.format(
                                "package:%s",
                                getPackageName()
                            )
                        )
                    ), 1234
                );
            }
        }
    }

    private fun installApk(apk: File) {
        val intent = Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uriFromFile(apk), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
            Log.e("TAG", "Error in opening the file!");
        }
    }

    private fun uriFromFile(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            Uri.fromFile(file);
        }
    }
}