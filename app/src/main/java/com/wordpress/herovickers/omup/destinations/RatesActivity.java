package com.wordpress.herovickers.omup.destinations;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.wordpress.herovickers.omup.R;

public class RatesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        WebView browser = (WebView) findViewById(R.id.webview);
        browser.loadUrl("https://www.omuppcall.com/rates");
    }
}
