package com.teamellipsis.application_migration_platform;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

public class BrowserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        WebView htmlWebView = (WebView)findViewById(R.id.web);
        WebSettings webSetting = htmlWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);

        webSetting.setDisplayZoomControls(true);
//        htmlWebView.loadUrl("file:///android_asset/index.html");
        WebView lWebView = (WebView)findViewById(R.id.web);
        File lFile = new File(Environment.getExternalStorageDirectory() + "/fyp/build/index.html");
        lWebView.loadUrl("file:///" + lFile.getAbsolutePath());
    }
}
