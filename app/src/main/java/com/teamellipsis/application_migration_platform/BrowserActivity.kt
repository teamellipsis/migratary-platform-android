package com.teamellipsis.application_migration_platform

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebView
import java.io.File

class BrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        val filepath = intent.getStringExtra("WEB_VIEW_URL_FILE")
        val webFile = File( filepath,"/build/index.html")
        val htmlWebView = findViewById<View>(R.id.web) as WebView
        val webSetting = htmlWebView.settings
        webSetting.javaScriptEnabled = true

        webSetting.displayZoomControls = true
        htmlWebView.loadUrl("file:///" + webFile.getAbsolutePath())
//        htmlWebView.loadUrl("file:///android_asset/index.html");
//        val lWebView = findViewById<View>(R.id.web) as WebView
//        val lFile = File(Environment.getExternalStorageDirectory().toString() + "/fyp/build/index.html")

    }
}
