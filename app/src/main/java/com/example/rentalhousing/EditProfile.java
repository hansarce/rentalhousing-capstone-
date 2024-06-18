package com.example.rentalhousing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfile";

    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;

    private ImageView returnButton;
    private ImageView profilePic;
    private EditText editTextEmail, editTextContactNumber, editTextCity, editTextBirthday;

    private String currentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextCity = findViewById(R.id.editTextCity);
        editTextBirthday = findViewById(R.id.editTextBirthday);

        fetchUserData();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        } else {
            // Handle the case where the user is not authenticated
            Log.e(TAG, "User is not authenticated");
            finish(); // Close the activity
            return;
        }

        // Initialize views
        returnButton = findViewById(R.id.returntoprofile);
        profilePic = findViewById(R.id.profilepicedit);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            Glide.with(EditProfile.this).load(selectedImageUri).circleCrop().into(profilePic);
                        }
                    }
                });

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        returnButton.setOnClickListener(v -> {
            // Assuming ProfileActivity is the correct activity
            Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
            startActivity(intent);
        });

        Button saveButton = findViewById(R.id.buttonSaveChanges);
        saveButton.setOnClickListener(v -> {
            uploadProfileDataToFirestore();
        });
    }

    private StorageReference getCurrentProfilePicStorageRef() {
        if (currentUserID != null) {
            return FirebaseStorage.getInstance().getReference().child("profilepic").child(currentUserID);
        }
        return null;
    }

    private void uploadProfileDataToFirestore() {
        String email = editTextEmail.getText().toString().trim();
        String contactNumber = editTextContactNumber.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String birthday = editTextBirthday.getText().toString().trim();

        // Ensure all fields are filled
        if (email.isEmpty() || contactNumber.isEmpty() || city.isEmpty() || birthday.isEmpty()) {
            // Handle case where any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("contactNumber", contactNumber);
        userData.put("city", city);
        userData.put("birthday", birthday);

        if (currentUserID != null) {
            db.collection("users")
                    .document(currentUserID)
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Profile data saved to Firestore");
                        // Optionally, show success message or navigate back to profile
                        Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save profile data to Firestore", e);
                        // Handle failure
                    });
        } else {
            Log.e(TAG, "User ID is null, cannot save to Firestore");
        }
    }


    private void saveProfilePicUrlToFirestore(String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("profilePicUrl", url);

        if (currentUserID != null) {
            db.collection("users")
                    .document(currentUserID)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Profile pic URL saved to Firestore");
                        // Start ProfileFragment activity after successful update
                        Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save profile pic URL to Firestore", e));
        } else {
            Log.e(TAG, "User ID is null, cannot save to Firestore");
        }
    }

    private void fetchUserData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if currentUserID is not null
        if (currentUserID != null) {
            db.collection("users")
                    .document(currentUserID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Check if the document exists in Firestore
                        if (documentSnapshot.exists()) {
                            // Populate EditText fields with existing data
                            String email = documentSnapshot.getString("email");
                            String contactNumber = documentSnapshot.getString("contactNumber");
                            String city = documentSnapshot.getString("city");
                            String birthday = documentSnapshot.getString("birthday");

                            // Set fetched data to EditText fields
                            editTextEmail.setText(email);
                            editTextContactNumber.setText(contactNumber);
                            editTextCity.setText(city);
                            editTextBirthday.setText(birthday);

                            // Load profile picture if profilePicUrl exists
                            String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profilePicUrl)
                                        .circleCrop()
                                        .into(profilePic);
                            } else {
                                // If no profile picture is set, load a placeholder or default image
                                profilePic.setImageResource(R.drawable.baseline_account_circle_24);
                            }
                        } else {
                            Log.e(TAG, "Document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching document", e);
                        // Handle error fetching document from Firestore
                    });
        } else {

            Log.e(TAG, "User ID is null, cannot fetch from Firestore");

        }
    }


}
