package com.zhenl.payhook

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.concurrent.thread

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        verifyStoragePermissions(this)
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun verifyStoragePermissions(activity: Activity) {
        try {
            val permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            } else {
                copyConfig()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                copyConfig()
            } else {
                Toast.makeText(this, R.string.msg_permission_fail, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    fun copyConfig() {
        thread {
            val sharedPrefsDir = File(filesDir, "../shared_prefs")
            val sharedPrefsFile = File(sharedPrefsDir, Common.MOD_PREFS + ".xml")
            val sdSPDir = File(Common.APP_DIR_PATH)
            val sdSPFile = File(sdSPDir, Common.MOD_PREFS + ".xml")
            if (sharedPrefsFile.exists()) {
                if (!sdSPDir.exists())
                    sdSPDir.mkdirs()
                val outStream = FileOutputStream(sdSPFile)
                FileUtils.copyFile(FileInputStream(sharedPrefsFile), outStream)
            } else if (sdSPFile.exists()) { // restore sharedPrefsFile
                if (!sharedPrefsDir.exists())
                    sharedPrefsDir.mkdirs()
                val input = FileInputStream(sdSPFile)
                val outStream = FileOutputStream(sharedPrefsFile)
                FileUtils.copyFile(input, outStream)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        copyConfig()
    }
}
