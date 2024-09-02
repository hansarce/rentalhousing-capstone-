package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private static final String TAG = "VerifyOTPActivity";
    private static final long OTP_TIMEOUT = 5 * 60 * 1000; // 5 minutes

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String verificationId;
    private String mobileNumber;

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private TextView textMobile;
    private ProgressBar progressBar;
    private Button buttonVerify;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        textMobile = findViewById(R.id.textMobile);
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);
        progressBar = findViewById(R.id.progressBar);
        buttonVerify = findViewById(R.id.buttonVerify);

        // Get the mobile number from the previous activity
        mobileNumber = getIntent().getStringExtra("mobile");
        textMobile.setText(String.format(mobileNumber));

        // Send OTP code to the mobile number
        sendVerificationCode(mobileNumber);

        // Start a 5-minute timer
        startOTPTimer();

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = inputCode1.getText().toString().trim() +
                        inputCode2.getText().toString().trim() +
                        inputCode3.getText().toString().trim() +
                        inputCode4.getText().toString().trim() +
                        inputCode5.getText().toString().trim() +
                        inputCode6.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(VerifyOTPActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyCode(code);
            }
        });
    }

    private void sendVerificationCode(String mobile) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + mobile)        // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)  // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code != null) {
                fillOTPCode(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Verification failed", e);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationId = s;
            progressBar.setVisibility(View.GONE);
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            countDownTimer.cancel(); // Stop the timer on successful verification
                            Intent intent = new Intent(VerifyOTPActivity.this, pincode.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VerifyOTPActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fillOTPCode(String code) {
        if (code.length() == 6) {
            inputCode1.setText(String.valueOf(code.charAt(0)));
            inputCode2.setText(String.valueOf(code.charAt(1)));
            inputCode3.setText(String.valueOf(code.charAt(2)));
            inputCode4.setText(String.valueOf(code.charAt(3)));
            inputCode5.setText(String.valueOf(code.charAt(4)));
            inputCode6.setText(String.valueOf(code.charAt(5)));
        }
    }

    private void startOTPTimer() {
        countDownTimer = new CountDownTimer(OTP_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Optional: Update UI to show remaining time
            }

            @Override
            public void onFinish() {
                // Timer finishes, delete user data and go back to home
                deleteUserDataAndGoHome();
            }
        };
        countDownTimer.start();
    }

    private void deleteUserDataAndGoHome() {
        // Delete user data from Firestore
        db.collection("users").document(mobileNumber).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Navigate to home screen
                            Intent intent = new Intent(VerifyOTPActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "Error deleting user data");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // Delete user data and navigate to home if back button is pressed
        deleteUserDataAndGoHome();
        // Call the super implementation of onBackPressed
        super.onBackPressed();
    }

}
