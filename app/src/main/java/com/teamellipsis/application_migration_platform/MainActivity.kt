package com.teamellipsis.application_migration_platform

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.teamellipsis.dynamic.DynamicApp
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {
//    var fileContent: String? = null
//    var projectPath: String? = null
    var fileSystem: FileSystem? = null

//    var nodeThread: Thread? = null
//    var projectThread: Thread? = null
//    var webViewUrl: String? = null
    var PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()
        fileSystem = FileSystem(this.application)
        val first_time=true
        if(first_time){
            val intent = Intent(applicationContext, AppManagementActivity::class.java).apply {
                putExtra("WEB_VIEW_URL", "test")
            }
            startActivity(intent)
        }
    }

    fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_WRITE_EXTERNAL_STORAGE
            )
        } else {
//            printLog("LOG: (getPermission) PERMISSION_GRANTED")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                printLog("LOG: (onRequestPermissionsResult) PERMISSION_GRANTED")
            } else {
//                printLog("LOG: (onRequestPermissionsResult) PERMISSION_DENIED")
            }
        }
    }

}
