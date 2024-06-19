package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class rhinformation3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info3);

        Button buttonNext = findViewById(R.id.submitbutton);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationButton();
            }
        });
    }

    private void ConfirmationButton() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are finish filling up the information?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showWaitDialog();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showWaitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Please Wait")
                .setMessage("Please wait for 3 working days.")
                .setPositiveButton("OK", (dialog, which) -> {
                    navigateToProfile();
                })
                .show();
    }

    private void navigateToProfile() {

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(rhinformation3.this, DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 2000); // Delay for 3 seconds, for example.
    }
}
