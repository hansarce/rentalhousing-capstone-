package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOTPActivity extends AppCompatActivity {

    /*private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private Button buttonVerify;
    private ProgressBar progressBar;
    private TextView textMobile, textResendOTP;

    private FirebaseAuth mAuth;
    private String verificationId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);
        buttonVerify = findViewById(R.id.buttonVerify);
        progressBar = findViewById(R.id.progressBar);
        textMobile = findViewById(R.id.textMobile);
        textResendOTP = findViewById(R.id.textResendOTP);

        // Get verificationId and phoneNumber from Intent
        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        // Display the phone number
        textMobile.setText(getString(R.string.enter_the_otp_sent_to, phoneNumber));

        // Setup button click listener
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = inputCode1.getText().toString().trim() +
                        inputCode2.getText().toString().trim() +
                        inputCode3.getText().toString().trim() +
                        inputCode4.getText().toString().trim() +
                        inputCode5.getText().toString().trim() +
                        inputCode6.getText().toString().trim();

                if (TextUtils.isEmpty(otp) || otp.length() < 6) {
                    Toast.makeText(VerifyOTPActivity.this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                buttonVerify.setEnabled(false);

                // Verify OTP
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });

        // Handle resend OTP
        textResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement resend OTP functionality here
                // For example, you can initiate phone number verification again
                Toast.makeText(VerifyOTPActivity.this, "Resend OTP clicked", Toast.LENGTH_SHORT).show();
                // Resend OTP logic
                // For example, you can call PhoneAuthProvider.getInstance().verifyPhoneNumber() again
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(VerifyOTPActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();

                        // Proceed to your next activity after successful verification
                        Intent intent = new Intent(VerifyOTPActivity.this, NextActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(VerifyOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            // There is no user corresponding to the given phone number
                            Toast.makeText(VerifyOTPActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Any other errors
                            Toast.makeText(VerifyOTPActivity.this, "Verification failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        buttonVerify.setEnabled(true);
                    }
                });
    }*/
}
