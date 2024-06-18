package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView profilepic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilepic = view.findViewById(R.id.profilepic);
        ImageView backProfileImageView = view.findViewById(R.id.backprofile);
        Button buttonrentalhousing = view.findViewById(R.id.buttonCreateRentalHousing);

        backProfileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        buttonrentalhousing.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RHInformation.class);
            startActivity(intent);
        });

        // Load the profile picture
        loadProfilePicture();

        return view;
    }

    private void loadProfilePicture() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserID = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(currentUserID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                            Log.d(TAG, "Profile picture URL retrieved: " + profilePicUrl);

                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Log.d(TAG, "Loading profile picture into ImageView");
                                Glide.with(ProfileFragment.this)
                                        .load(profilePicUrl)
                                        .circleCrop()
                                        .into(profilepic);
                            } else {
                                Log.e(TAG, "Profile picture URL is empty");
                            }
                        } else {
                            Log.e(TAG, "User document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch profile picture URL", e));
        } else {
            Log.e(TAG, "User is not authenticated");
        }
    }
}
