package com.example.rentalhousing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView profilePic;
    private TextView name, email,  birthday;
    private String currentUserID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profilePic = view.findViewById(R.id.profilepic);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        birthday = view.findViewById(R.id.birthday);

        // Fetch current user data
        fetchCurrentUser();



        Button createRentalHousingButton = view.findViewById(R.id.buttonCreateRentalHousing);
        createRentalHousingButton.setOnClickListener(v -> navigateToRentalHousingInfo());

        // Fetch and populate user data
        fetchUserData();

        return view;
    }

    private void fetchCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not authenticated");
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
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
            // Get email from Firebase Authentication
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                email.setText(currentUser.getEmail());
            } else {
                Log.e(TAG, "Current user is null");
            }

            // Populate other fields from Firestore
            name.setText(documentSnapshot.getString("Name"));
            birthday.setText(documentSnapshot.getString("birthday"));

            // Load profile picture if available
            loadProfilePicture();
        } else {
            Log.e(TAG, "Document does not exist");
        }
    }


    private void loadProfilePicture() {
        StorageReference profilePicRef = getCurrentProfilePicStorageRef();
        if (profilePicRef != null) {
            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(requireContext())
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


    private void navigateToRentalHousingInfo() {
        Intent intent = new Intent(requireActivity(), rhinformation1.class);
        startActivity(intent);
    }
}
