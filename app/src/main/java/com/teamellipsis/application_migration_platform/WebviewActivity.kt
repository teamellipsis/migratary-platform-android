package com.teamellipsis.application_migration_platform

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import com.google.gson.Gson
//import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_webview.*
//import org.java_websocket.client.WebSocketClient
import java.io.File
import java.net.URI

//import java.net.URI
//import org.java_websocket.drafts.Draft_6455
//import org.java_websocket.handshake.ServerHandshake
//import java.lang.Exception
//import java.util.HashMap

class WebviewActivity : AppCompatActivity() {
    lateinit var appConfig: AppConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setSupportActionBar(toolbar)
        appConfig = AppConfig(applicationContext)
        val filepath = intent.getStringExtra("WEB_VIEW_URL_FILE")
        val webFile = File( filepath,"/build/index.html")
        val htmlWebView = findViewById<View>(R.id.web) as WebView
        val webSetting = htmlWebView.settings
        webSetting.javaScriptEnabled = true
        webSetting.displayZoomControls = true
        htmlWebView.loadUrl("file:///" + webFile.getAbsolutePath())

        fab.setOnClickListener { view ->
            servr.appsave()
//            saveapp(true)
//            AppManagementActivity.closeserver()
            finish()
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSave -> {
                servr.appsave()
                true
            }
            R.id.btnClose -> {
                servr.appsave()
                finish()
                true
            }
            R.id.btnSend -> {
                var selectedname= File(AppManagementActivity.AppPath).name
                var senditempath= appConfig.get(AppConstant.KEY_SENTITM_DIR)+"/"+selectedname+".zip"
                val intent = Intent(applicationContext, ServerActivity::class.java).apply {
                    putExtra("APP_PATH",senditempath)
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManagementActivity.closeserver();
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("Do you want to exit?")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "No",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Yes",
            DialogInterface.OnClickListener { dialog, which ->
                servr.appsave()
                finish()
            }
        )
        alertDialog.show()
    }




}
