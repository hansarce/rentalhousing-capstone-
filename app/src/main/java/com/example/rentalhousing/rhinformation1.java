package com.example.rentalhousing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class rhinformation1 extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info1);

        webView = findViewById(R.id.pinloc);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/map_for_pin.html");

        // Initialize EditText fields
        EditText propertyNameInput = findViewById(R.id.propertyname);
        EditText landlordNameInput = findViewById(R.id.landlordname);
        EditText contactNumberInput = findViewById(R.id.contactnumber);
        EditText propertyAddressInput = findViewById(R.id.propertyaddress);

        Button buttonNext = findViewById(R.id.next1);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String propertyName = propertyNameInput.getText().toString();
                String landlordName = landlordNameInput.getText().toString();
                String contactNumber = contactNumberInput.getText().toString();
                String propertyAddress = propertyAddressInput.getText().toString();


                SharedPreferences sharedPreferences = getSharedPreferences("PropertyInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PropertyName", propertyName);
                editor.putString("LandlordName", landlordName);
                editor.putString("ContactNumber", contactNumber);
                editor.putString("PropertyAddress", propertyAddress);
                editor.apply();

                // Navigate to the next activity
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
