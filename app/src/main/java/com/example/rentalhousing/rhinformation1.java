package com.example.rentalhousing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class rhinformation1 extends Activity {private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info1);

        webView = findViewById(R.id.pinloc);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/map_for_pin.html");

        Button buttonNext = findViewById(R.id.next1);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rhinformation1.this, rhinformation2.class);
                startActivity(intent);
            }
        });

        Button pinLocationButton = findViewById(R.id.button2);
        pinLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.evaluateJavascript("addMarkerAtCurrentLocation();", null);
            }
        });
    }
}