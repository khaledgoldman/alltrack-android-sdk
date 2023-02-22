package com.example.examples;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alltrack.sdk.webbridge.AlltrackBridge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        AlltrackBridge.registerAndGetInstance(getApplication(), webView);
        try {
//            webView.loadUrl("file:///android_asset/AlltrackExample-WebView.html");
            webView.loadUrl("https://alltrackweb.neocities.org");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        AlltrackBridge.unregister();

        super.onDestroy();
    }
}
