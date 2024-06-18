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
    private EditText editTextEmail, editTextContactNumber, editTextCity, editTextBirthday;

    private String currentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextCity = findViewById(R.id.editTextCity);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        returnButton = findViewById(R.id.returntoprofile);
        profilePic = findViewById(R.id.profilepicedit);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not authenticated");
            finish(); // Close the activity
            return;
        }

        fetchUserData();

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
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });

        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
            startActivity(intent);
        });

        Button saveButton = findViewById(R.id.buttonSaveChanges);
        saveButton.setOnClickListener(v -> uploadProfileDataToFirestore());
    }

    private StorageReference getCurrentProfilePicStorageRef() {
        if (currentUserID != null) {
            return FirebaseStorage.getInstance().getReference().child("profile").child(currentUserID);
        }
        return null;
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
            editTextEmail.setText(documentSnapshot.getString("email"));
            editTextContactNumber.setText(documentSnapshot.getString("contactNumber"));
            editTextCity.setText(documentSnapshot.getString("city"));
            editTextBirthday.setText(documentSnapshot.getString("birthday"));

            getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    Glide.with(EditProfile.this).load(uri).circleCrop().into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.baseline_account_circle_24);
                }
            });
        } else {
            Log.e(TAG, "Document does not exist");
        }
    }

    private void uploadProfileDataToFirestore() {
        String email = editTextEmail.getText().toString().trim();
        String contactNumber = editTextContactNumber.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String birthday = editTextBirthday.getText().toString().trim();

        if (email.isEmpty() || contactNumber.isEmpty() || city.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save profile text data first
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("contactNumber", contactNumber);
        userData.put("city", city);
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
            Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
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
                        Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save profile pic URL to Firestore", e));
        } else {
            Log.e(TAG, "User ID is null, cannot save to Firestore");
        }
    }
}
