package com.example.rentalhousing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class rhinformation2 extends AppCompatActivity {

    private Switch switchWifi;
    private Switch switchAircon;
    private Switch switchParking;
    private Switch switchPetFriendly;
    private Switch switchLaundry;
    private Switch switchRefrigerator;
    private Switch switchKitchen;
    private Switch switchEmergencyExit;
    private Switch switchMisc;
    private Button buttonNext;

    private static final String PREFS_NAME = "RentalHousingPrefs";
    private static final String KEY_WIFI = "wifi";
    private static final String KEY_AIRCON = "aircon";
    private static final String KEY_PARKING = "parking";
    private static final String KEY_PET_FRIENDLY = "petFriendly";
    private static final String KEY_LAUNDRY = "laundry";
    private static final String KEY_REFRIGERATOR = "refrigerator";
    private static final String KEY_KITCHEN = "kitchen";
    private static final String KEY_EMERGENCY_EXIT = "emergencyExit";
    private static final String KEY_MISC = "misc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info2); // Ensure this matches your XML layout file

        // Initialize the switches
        switchWifi = findViewById(R.id.switch2);
        switchAircon = findViewById(R.id.switch1);
        switchParking = findViewById(R.id.switch3);
        switchPetFriendly = findViewById(R.id.switch4);
        switchLaundry = findViewById(R.id.switch5);
        switchRefrigerator = findViewById(R.id.switch6);
        switchKitchen = findViewById(R.id.switch7);
        switchEmergencyExit = findViewById(R.id.switch8);
        switchMisc = findViewById(R.id.switch9);

        // Initialize the Next button
        buttonNext = findViewById(R.id.next2);

        // Set an OnClickListener for the Next button
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the states of switches
                boolean isWifiChecked = switchWifi.isChecked();
                boolean isAirconChecked = switchAircon.isChecked();
                boolean isParkingChecked = switchParking.isChecked();
                boolean isPetFriendlyChecked = switchPetFriendly.isChecked();
                boolean isLaundryChecked = switchLaundry.isChecked();
                boolean isRefrigeratorChecked = switchRefrigerator.isChecked();
                boolean isKitchenChecked = switchKitchen.isChecked();
                boolean isEmergencyExitChecked = switchEmergencyExit.isChecked();
                boolean isMiscChecked = switchMisc.isChecked();

                // Save the states to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_WIFI, isWifiChecked);
                editor.putBoolean(KEY_AIRCON, isAirconChecked);
                editor.putBoolean(KEY_PARKING, isParkingChecked);
                editor.putBoolean(KEY_PET_FRIENDLY, isPetFriendlyChecked);
                editor.putBoolean(KEY_LAUNDRY, isLaundryChecked);
                editor.putBoolean(KEY_REFRIGERATOR, isRefrigeratorChecked);
                editor.putBoolean(KEY_KITCHEN, isKitchenChecked);
                editor.putBoolean(KEY_EMERGENCY_EXIT, isEmergencyExitChecked);
                editor.putBoolean(KEY_MISC, isMiscChecked);
                editor.apply(); // Save the data

                // Navigate to the next activity
                Intent intent = new Intent(rhinformation2.this, rhinformation3.class);
                startActivity(intent);
            }
        });
    }
}
