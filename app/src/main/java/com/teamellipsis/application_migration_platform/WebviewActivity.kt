package com.teamellipsis.application_migration_platform

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setSupportActionBar(toolbar)

        val filepath = intent.getStringExtra("WEB_VIEW_URL_FILE")
        val webFile = File( filepath,"/build/index.html")
        val htmlWebView = findViewById<View>(R.id.web) as WebView
        val webSetting = htmlWebView.settings
        webSetting.javaScriptEnabled = true
        webSetting.displayZoomControls = true
        htmlWebView.loadUrl("file:///" + webFile.getAbsolutePath())

        fab.setOnClickListener { view ->
            saveapp(true)
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(applicationContext, AppManagementActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.action_favorite -> {
                finish()
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
fun saveapp(close: Boolean){
    System.out.println("connect server...............................................")
    var client=Client(URI("ws://localhost:4444"))
    client.connect()
    val args = HashMap<String, String>()
    args.put("operation","saveobject")
    val gson = Gson()
    val json = gson.toJson(args)
    client.send(json)
    if(close){
        AppManagementActivity.closeserver()
    }


}



}
