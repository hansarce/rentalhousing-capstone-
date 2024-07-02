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

public class loginpincode extends AppCompatActivity {

    private static final String TAG = "LoginPincode";
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCKOUT_DURATION = 3600000; // 1 hour in milliseconds

    private EditText pinCodeEditText;
    private TextView statusTextView;
    private String currentUserID;

    private int failedAttempts = 0;
    private long lockoutEndTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpincode);

        pinCodeEditText = findViewById(R.id.pin_code_edit_text);
        statusTextView = findViewById(R.id.status_text_view);

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
                    String inputPin = s.toString();
                    verifyPasscode(inputPin);
                }
            }
        });

        fetchLockoutInfoFromFirestore();
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

    private void verifyPasscode(String inputPin) {
        if (System.currentTimeMillis() < lockoutEndTime) {
            Toast.makeText(this, "Account is locked. Try again later.", Toast.LENGTH_SHORT).show();
            pinCodeEditText.setText("");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(currentUserID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UserPasscode userPasscode = document.toObject(UserPasscode.class);
                    if (userPasscode != null && userPasscode.getPasscode().equals(inputPin)) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, DashBoard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        handleFailedAttempt();
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void handleFailedAttempt() {
        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS) {
            lockoutEndTime = System.currentTimeMillis() + LOCKOUT_DURATION;
            failedAttempts = 0; // reset attempts after lockout

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> lockoutInfo = new HashMap<>();
            lockoutInfo.put("lockoutEndTime", lockoutEndTime);

            db.collection("users").document(currentUserID)
                    .set(lockoutInfo, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Too many attempts. Account locked for 1 hour.", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating lockout info.", Toast.LENGTH_SHORT).show());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Incorrect PIN. Attempt " + failedAttempts + " of " + MAX_ATTEMPTS, Toast.LENGTH_SHORT).show();
        }
        pinCodeEditText.setText("");
    }

    private void fetchLockoutInfoFromFirestore() {
        if (currentUserID == null) {
            Log.e(TAG, "User ID is null. Cannot fetch lockout info.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(currentUserID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long lockoutTime = document.getLong("lockoutEndTime");
                    if (lockoutTime != null) {
                        lockoutEndTime = lockoutTime;
                        if (System.currentTimeMillis() < lockoutEndTime) {
                            Toast.makeText(this, "Account is currently locked. Try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
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
