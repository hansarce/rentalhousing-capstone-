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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfile";

    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;

    private ImageView returnButton;
    private ImageView profilePic;
    private EditText editTextContactNumber, editName, editTextBirthday;

    private String currentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        // Initialize views
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editName = findViewById(R.id.editname);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        returnButton = findViewById(R.id.returntoprofile);
        profilePic = findViewById(R.id.profilepicedit);

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not authenticated");
            finish(); // Close the activity
            return;
        }

        // Fetch existing user data to populate the form
        fetchUserData();

        // Set up image picker
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(EditProfile.this).load(selectedImageUri).circleCrop().into(profilePic);
                    }
                });

        // Profile picture click listener to pick an image
        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });

        // Return to Profile button
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), BlankFragment.class);
            startActivity(intent);
        });

        // Save changes button
        Button saveButton = findViewById(R.id.buttonSaveChanges);
        saveButton.setOnClickListener(v -> uploadProfileDataToFirestore());
    }

    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUserID != null) {
            db.collection("users")
                    .document(currentUserID)
                    .get()
                    .addOnSuccessListener(this::populateUserData)
                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching document", e));
        } else {
            Log.e(TAG, "User ID is null, cannot fetch from Firestore");
        }
    }

    private void populateUserData(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            // Populate fields with existing data
            editName.setText(documentSnapshot.getString("Name"));
            editTextContactNumber.setText(documentSnapshot.getString("contactNumber"));
            editTextBirthday.setText(documentSnapshot.getString("birthday"));

            // Load existing profile picture
            loadProfilePicture();
        } else {
            Log.e(TAG, "Document does not exist");
        }
    }

    private void loadProfilePicture() {
        StorageReference profilePicRef = getCurrentProfilePicStorageRef();
        if (profilePicRef != null) {
            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .error(R.drawable.baseline_account_circle_24)
                        .into(profilePic);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to fetch profile picture", e);
                profilePic.setImageResource(R.drawable.baseline_account_circle_24);
            });
        } else {
            Log.e(TAG, "Profile picture reference is null");
            profilePic.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }

    private void uploadProfileDataToFirestore() {
        String edtname = editName.getText().toString().trim();
        String contactNumber = editTextContactNumber.getText().toString().trim();
        String birthday = editTextBirthday.getText().toString().trim();

        if (edtname.isEmpty() || contactNumber.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save profile text data first
        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", edtname);
        userData.put("contactNumber", contactNumber);
        userData.put("birthday", birthday);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUserID != null) {
            db.collection("users")
                    .document(currentUserID)
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile data saved to Firestore"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save profile data to Firestore", e));
        } else {
            Log.e(TAG, "User ID is null, cannot save to Firestore");
        }

        // Save profile image if selected
        if (selectedImageUri != null) {
            StorageReference profilePicRef = getCurrentProfilePicStorageRef();
            if (profilePicRef != null) {
                profilePicRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String photoUrl = uri.toString();
                            saveProfilePicUrlToFirestore(photoUrl);
                        }))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to upload profile picture", e));
            }
        } else {
            // If no new image is selected, just go back to the profile
            Intent intent = new Intent(getApplicationContext(), BlankFragment.class);
            startActivity(intent);
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
                        Intent intent = new Intent(getApplicationContext(), BlankFragment.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save profile pic URL to Firestore", e));
        } else {
            Log.e(TAG, "User ID is null, cannot save to Firestore");
        }
    }

    private StorageReference getCurrentProfilePicStorageRef() {
        if (currentUserID != null) {
            return FirebaseStorage.getInstance().getReference()
                    .child("profile_pictures")
                    .child(currentUserID);
        } else {
            Log.e(TAG, "Current user ID is null");
            return null;
        }
    }
}
