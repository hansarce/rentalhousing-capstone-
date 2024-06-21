package com.example.rentalhousing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class signupinformation extends AppCompatActivity {

    private static final String TAG = "SignupInformation";

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button saveChangesButton;
    private ImageView profilePicImageView;
    private EditText nameEditText;

    private Uri profileImageUri;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupinformation);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize date picker
        initDatePicker();

        // Initialize views
        dateButton = findViewById(R.id.datepickerbutton);
        saveChangesButton = findViewById(R.id.buttonSaveChanges);
        profilePicImageView = findViewById(R.id.profilepicsg);
        nameEditText = findViewById(R.id.sgname);

        // Set click listener for date picker button
        dateButton.setOnClickListener(this::openDatePicker);

        // Set click listener for profile picture
        profilePicImageView.setOnClickListener(v -> selectProfilePicture());

        // Set click listener for save changes button
        saveChangesButton.setOnClickListener(v -> saveProfileInformation());
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        // Initialize calendar with current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + "/" + day + "/" + year;
    }

    private String getMonthFormat(int month) {
        return String.format("%02d", month); // Format month to two digits
    }

    private void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void selectProfilePicture() {
        ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .start(); // Start image picker activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle image picker result
        if (resultCode == Activity.RESULT_OK && data != null) {
            profileImageUri = data.getData();
            // Display selected image
            profilePicImageView.setImageURI(profileImageUri);
        }
    }

    private void saveProfileInformation() {
        String name = nameEditText.getText().toString().trim();
        String birthday = dateButton.getText().toString().trim();

        if (name.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            if (profileImageUri != null) {
                uploadProfilePicture(name, birthday);
            } else {
                saveProfileToFirestore(name, birthday, null);
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User not authenticated");
        }
    }

    private void uploadProfilePicture(final String name, final String birthday) {
        // Create a storage reference for the profile picture
        StorageReference profilePicRef = getCurrentProfilePicStorageRef();

        if (profilePicRef != null) {
            profilePicRef.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the URL of the uploaded image
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String profilePicUrl = uri.toString();
                            // Save profile information including profile pic URL to Firestore
                            saveProfileToFirestore(name, birthday, profilePicUrl);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(signupinformation.this, "Failed to get profile picture URL", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to get profile picture URL", e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(signupinformation.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to upload profile picture", e);
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User not authenticated");
        }
    }

    private void saveProfileToFirestore(String name, String birthday, String profilePicUrl) {
        // Create a new user profile document in Firestore
        Map<String, Object> profile = new HashMap<>();
        profile.put("Name", name); // Consistent with EditProfile
        profile.put("birthday", birthday);
        if (profilePicUrl != null) {
            profile.put("profilePicUrl", profilePicUrl);
        }

        // Add a new document with a generated ID
        db.collection("users")
                .document(currentUser.getUid())
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(signupinformation.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Profile saved successfully!");
                    startActivity(new Intent(signupinformation.this, pincode.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(signupinformation.this, "Failed to save profile information", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to save profile information", e);
                });
    }

    private StorageReference getCurrentProfilePicStorageRef() {
        if (currentUser != null) {
            return FirebaseStorage.getInstance().getReference().child("profile_pictures").child(currentUser.getUid());
        }
        return null;
    }
}
