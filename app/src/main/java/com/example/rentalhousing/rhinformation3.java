package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class rhinformation3 extends AppCompatActivity {

    private EditText editTextDescription;
    private EditText editTextLegalDocuments;
    private TextView textViewPricing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info3);

        // Initialize views
        editTextDescription = findViewById(R.id.editTextTextMultiLine);
        editTextLegalDocuments = findViewById(R.id.editTextNumberDecimal);
        textViewPricing = findViewById(R.id.textView3);
        Button buttonNext = findViewById(R.id.submitbutton);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        String description = intent.getStringExtra("description");
        String legalDocuments = intent.getStringExtra("legalDocuments");
        String pricing = intent.getStringExtra("pricing");

        // Set the retrieved data to views
        if (description != null) {
            editTextDescription.setText(description);
        }
        if (legalDocuments != null) {
            editTextLegalDocuments.setText(legalDocuments);
        }
        if (pricing != null) {
            textViewPricing.setText(pricing);
        }

        // Set button click listener
        buttonNext.setOnClickListener(v -> ConfirmationButton());
    }

    private void ConfirmationButton() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you finished filling up the information?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showWaitDialog();
                    saveDataToFirestore();  // Save data to Firestore
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showWaitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Please Wait")
                .setMessage("Please wait for 3 working days.")
                .setPositiveButton("OK", (dialog, which) -> navigateToProfile())
                .show();
    }

    private void navigateToProfile() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(rhinformation3.this, DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 2000); // Delay for 2 seconds
    }

    private void saveDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a document with the data
        Map<String, Object> data = new HashMap<>();
        data.put("description", editTextDescription.getText().toString());
        data.put("legalDocuments", editTextLegalDocuments.getText().toString());
        data.put("pricing", textViewPricing.getText().toString());

        // Save the data to Firestore
        db.collection("userData").document("userDocument")
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
