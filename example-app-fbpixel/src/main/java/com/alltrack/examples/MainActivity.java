package com.alltrack.examples;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alltrack.sdk.webbridge.AlltrackBridge;
import com.alltrack.sdk.webbridge.AlltrackBridgeInstance;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = String.format("%s - %d: %s", consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message());
                Log.d("AlltrackExample", message);
                return super.onConsoleMessage(consoleMessage);
            }
        });

        AlltrackBridgeInstance defaultInstance = AlltrackBridge.registerAndGetInstance(getApplication(), webView);
        defaultInstance.registerFacebookSDKJSInterface();

        try {
            webView.loadUrl("file:///android_asset/AlltrackExample-FbPixel.html");
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
