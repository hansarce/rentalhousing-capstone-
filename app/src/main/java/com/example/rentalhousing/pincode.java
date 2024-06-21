package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class pincode extends AppCompatActivity {

    private static final String TAG = "pincodeActivity";
    private EditText pinCodeEditText;
    private TextView firstInputTextView;
    private String firstInput = "";
    private String secondInput = "";
    private boolean isFirstInputComplete = false;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pincode);

        pinCodeEditText = findViewById(R.id.pin_code_edit_text);
        firstInputTextView = findViewById(R.id.firstinput);

        fetchCurrentUser();
        setButtonListeners();

        pinCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    if (!isFirstInputComplete) {
                        firstInput = s.toString();
                        pinCodeEditText.setText("");
                        firstInputTextView.setText("Confirm your passcode");
                        isFirstInputComplete = true;
                    } else {
                        secondInput = s.toString();
                        if (firstInput.equals(secondInput)) {
                            storePasscodeToFirestore(firstInput);
                        } else {
                            Toast.makeText(pincode.this, "Passcodes do not match. Try again.", Toast.LENGTH_SHORT).show();
                            pinCodeEditText.setText("");
                            firstInputTextView.setText("Input desired passcode");
                            isFirstInputComplete = false;
                        }
                    }
                }
            }
        });

        // Fetch and display existing passcode
        fetchPasscodeFromFirestore();
    }

    private void fetchCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not authenticated");
            finish();
        }
    }

    private void setButtonListeners() {
        View.OnClickListener listener = v -> {
            Button button = (Button) v;
            pinCodeEditText.append(button.getText().toString());
        };

        findViewById(R.id.btn_0).setOnClickListener(listener);
        findViewById(R.id.btn_1).setOnClickListener(listener);
        findViewById(R.id.btn_2).setOnClickListener(listener);
        findViewById(R.id.btn_3).setOnClickListener(listener);
        findViewById(R.id.btn_4).setOnClickListener(listener);
        findViewById(R.id.btn_5).setOnClickListener(listener);
        findViewById(R.id.btn_6).setOnClickListener(listener);
        findViewById(R.id.btn_7).setOnClickListener(listener);
        findViewById(R.id.btn_8).setOnClickListener(listener);
        findViewById(R.id.btn_9).setOnClickListener(listener);
        findViewById(R.id.btn_del).setOnClickListener(v -> {
            Editable text = pinCodeEditText.getText();
            if (text.length() > 0) {
                text.delete(text.length() - 1, text.length());
            }
        });
    }

    private void storePasscodeToFirestore(String passcode) {
        if (currentUserID == null) {
            Log.e(TAG, "User ID is null. Cannot store passcode.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> passcodeData = new HashMap<>();
        passcodeData.put("passcode", passcode);

        db.collection("users")
                .document(currentUserID)
                .set(passcodeData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(pincode.this, "Passcode saved successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(pincode.this, welcome.class);
                    startActivity(intent);
                    finish();  // Optionally finish this activity
                })
                .addOnFailureListener(e -> Toast.makeText(pincode.this, "Error saving passcode", Toast.LENGTH_SHORT).show());
    }

    private void fetchPasscodeFromFirestore() {
        if (currentUserID == null) {
            Log.e(TAG, "User ID is null. Cannot fetch passcode.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(currentUserID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String passcode = document.getString("passcode");
                    if (passcode != null) {
                        Toast.makeText(pincode.this, "Your passcode is: " + passcode, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public static class UserPasscode {
        private String passcode;

        public UserPasscode() {}

        public UserPasscode(String passcode) {
            this.passcode = passcode;
        }

        public String getPasscode() {
            return passcode;
        }

        public void setPasscode(String passcode) {
            this.passcode = passcode;
        }
    }
}
