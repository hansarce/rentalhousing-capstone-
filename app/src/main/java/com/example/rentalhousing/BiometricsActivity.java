package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BiometricsActivity extends AppCompatActivity {

    private Switch biometricsSwitch;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biometricslogin);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        biometricsSwitch = findViewById(R.id.switch10);
        ImageButton backButton = findViewById(R.id.bioback);

        // Load the current state of biometrics setting
        loadBiometricsSetting();

        // Set switch listener
        biometricsSwitch.setOnCheckedChangeListener(this::onBiometricsSwitchChanged);

        // Set back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the previous activity
                onBackPressed();
            }
        });
    }

    private void loadBiometricsSetting() {
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Boolean biometricsEnabled = documentSnapshot.getBoolean("biometricsEnabled");
                            if (biometricsEnabled != null) {
                                biometricsSwitch.setChecked(biometricsEnabled);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(BiometricsActivity.this, "Failed to load settings", Toast.LENGTH_SHORT).show());
        }
    }

    private void onBiometricsSwitchChanged(CompoundButton buttonView, boolean isChecked) {
        if (currentUser != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("biometricsEnabled", isChecked);

            db.collection("users")
                    .document(currentUser.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(BiometricsActivity.this, "Biometrics setting updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(BiometricsActivity.this, "Failed to update setting", Toast.LENGTH_SHORT).show());
        }
    }
}
