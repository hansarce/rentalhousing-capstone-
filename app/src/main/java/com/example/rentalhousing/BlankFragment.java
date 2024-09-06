package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BlankFragment extends Fragment {

    private static final String TAG = "BlankFragment";

    private ImageView profilePic;
    private TextView userName;
    private TextView userStatus;
    private Button accountButton;
    private Button createRentalHousingButton;
    private Button enableBiometricsButton;
    private Button logoutButton;

    private GoogleSignInClient mGoogleSignInClient;
    private String currentUserID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profiletalaga, container, false);

        // Initialize views
        profilePic = view.findViewById(R.id.profilepic);
        userName = view.findViewById(R.id.name);
        userStatus = view.findViewById(R.id.userstatus);
        accountButton = view.findViewById(R.id.button3);
        createRentalHousingButton = view.findViewById(R.id.button5);
        enableBiometricsButton = view.findViewById(R.id.button6);
        logoutButton = view.findViewById(R.id.button7);

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        // Fetch current user data
        fetchCurrentUser();

        // Fetch and populate user data
        fetchUserData();

        // Set up listeners for buttons
        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase Auth and Google Sign-In
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                // Redirect to MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();  // Finish current activity
            });
        });

        accountButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        createRentalHousingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), rhinformation1.class);
            startActivity(intent);
        });

        enableBiometricsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BiometricsActivity.class);
            startActivity(intent);
        });

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
            // Populate userName from Firestore
            userName.setText(documentSnapshot.getString("Name"));

            // Populate userStatus from Firestore
            userStatus.setText(documentSnapshot.getString("UserStatus")); // Assuming you have "UserStatus" field

            // Load profile picture from Firebase Storage
            loadProfilePicture();
        } else {
            Log.e(TAG, "Document does not exist");
        }
    }

    private void loadProfilePicture() {
        if (currentUserID != null) {
            StorageReference profilePicRef = FirebaseStorage.getInstance().getReference()
                    .child("profile_pictures")
                    .child(currentUserID);
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
            Log.e(TAG, "Current user ID is null");
            profilePic.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }
}
