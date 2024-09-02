package com.example.rentalhousing;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class BiometricsActivity extends AppCompatActivity {

    private Switch biometricsSwitch;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biometricslogin);

        biometricsSwitch = findViewById(R.id.switch10);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Load the current state from Firestore
        loadBiometricsState();

        // Set listener for switch state changes
        biometricsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveBiometricsState(isChecked);
            }
        });
    }

    private void loadBiometricsState() {
        if (currentUser != null) {
            DocumentReference docRef = firestore.collection("users").document(currentUser.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Boolean isBiometricsEnabled = documentSnapshot.getBoolean("biometricsEnabled");
                    if (isBiometricsEnabled != null) {
                        biometricsSwitch.setChecked(isBiometricsEnabled);
                    }
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(BiometricsActivity.this, "Failed to load biometrics state", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void saveBiometricsState(boolean isEnabled) {
        if (currentUser != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("biometricsEnabled", isEnabled);

            firestore.collection("users").document(currentUser.getUid())
                    .set(data)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(BiometricsActivity.this, "Biometrics state saved", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(BiometricsActivity.this, "Failed to save biometrics state", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}

