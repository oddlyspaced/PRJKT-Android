package com.oddlyspaced.prjkt.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oddlyspaced.prjkt.BuildConfig
import com.oddlyspaced.prjkt.databinding.ActivityEditorBinding
import com.oddlyspaced.prjkt.fragment.ColorPickerFragment
import com.oddlyspaced.prjkt.fragment.background.FillBackgroundEditorFragment
import com.oddlyspaced.prjkt.fragment.background.ResizeEditorFragment
import com.oddlyspaced.prjkt.fragment.background.ShapeEditorFragment
import com.oddlyspaced.prjkt.fragment.foreground.DesignEditorFragment
import com.oddlyspaced.prjkt.fragment.foreground.FillForegroundEditorFragment
import com.oddlyspaced.prjkt.fragment.foreground.MoveEditorFragment
import com.oddlyspaced.prjkt.modal.IconProperties
import com.oddlyspaced.prjkt.utils.kellinwood.zipsigner.ZipSigner
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    private lateinit var shapeEditorFragment: ShapeEditorFragment
    private lateinit var resizeEditorFragment: ResizeEditorFragment
    private lateinit var designEditorFragment: DesignEditorFragment
    private lateinit var moveEditorFragment: MoveEditorFragment
    private lateinit var fillForegroundEditorFragment: FillForegroundEditorFragment
    private lateinit var fillBackgroundEditorFragment: FillBackgroundEditorFragment

    private lateinit var alert: AlertDialog

    private val iconProperties = IconProperties(
        backgroundRadius = 120F,
        backgroundSides = 4,
        backgroundRotation = 45F,
        backgroundWidth = 1F,
        backgroundHeight = 1F,
        backgroundStartColor = "#FF000000",
        backgroundEndColor = "#FF000000",
        foregroundSize = 1F,
        foregroundMoveX = 0F,
        foregroundMoveY = 0F,
        foregroundRotate = 0F,
        foregroundStartColor = "#FFFFFFFF",
        foregroundEndColor = "#FFFFFFFF",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareEnvironment()

        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        shapeEditorFragment = ShapeEditorFragment.newInstance(binding.imgIconBackground, iconProperties)
        resizeEditorFragment = ResizeEditorFragment.newInstance(binding.imgIconBackground, iconProperties)
        designEditorFragment = DesignEditorFragment.newInstance(binding.imgIconForeground, iconProperties)
        moveEditorFragment = MoveEditorFragment.newInstance(binding.imgIconForeground, iconProperties)
        fillForegroundEditorFragment = FillForegroundEditorFragment.newInstance(binding.frag.id, binding.imgIconForeground, iconProperties)
        fillBackgroundEditorFragment = FillBackgroundEditorFragment.newInstance(binding.frag.id, binding.imgIconBackground, iconProperties)

        binding.txEditorShape.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("shape").add(binding.frag.id, shapeEditorFragment, "tagShape").commit()
        }

        binding.txEditorSize.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("size").add(binding.frag.id, resizeEditorFragment, "tagSize").commit()
        }

        binding.txEditorDesign.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("design").add(binding.frag.id, designEditorFragment, "tagDesign").commit()
        }

        binding.txEditorMove.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("move").add(binding.frag.id, moveEditorFragment, "tagMove").commit()
        }

        binding.txEditorForegroundFill.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("foregroundFill").add(binding.frag.id, fillForegroundEditorFragment, "tagForegroundFill").commit()
        }

        binding.txEditorBackgroundFill.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("backgroundFill").add(binding.frag.id, fillBackgroundEditorFragment, "tagBackgroundFill").commit()
        }

        binding.imgIconBackground.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        binding.cardView.setOnClickListener {
            showAlert()
            render()
        }
    }

    private fun showAlert() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Rendering")
        builder.setMessage("Please wait while we render the icons and package it into an apk...")
//        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
//        }
        builder.setCancelable(false)
        alert = builder.show()
    }

    private val template by lazy {
        File(applicationContext.externalCacheDir!!.path, "template")
    }

    private val templateIconPath by lazy {
        File(applicationContext.externalCacheDir!!.path, "template/res/drawable-nodpi-v4").path
    }

    private fun prepareEnvironment() {
        // template.deleteRecursively()
        if (!template.exists()) {
            copyTemplate()
        }

        if (!File(applicationContext.externalCacheDir!!.path, "keys").exists()) {
            copyKeys()
        }

        checkInstallPermission()
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

    private fun render() {
        for (icon in icons) {
            binding.imgIconForeground.setImageResource(resources.getIdentifier(icon, "drawable", applicationContext.packageName))
            ColorPickerFragment.generateGradient(binding.imgIconForeground, iconProperties.foregroundStartColor, iconProperties.foregroundEndColor)
            renderIcon("$icon")
        }

        packApk()
    }

    private fun renderIcon(iconName: String) {
        val bitmap = Bitmap.createBitmap(binding.consLayers.measuredWidth, binding.consLayers.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.consLayers.draw(canvas)
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

            alert.dismiss()
            installApk(outFile)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_LONG).show()
            Log.e("ERROR", e.toString())
            e.printStackTrace()
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