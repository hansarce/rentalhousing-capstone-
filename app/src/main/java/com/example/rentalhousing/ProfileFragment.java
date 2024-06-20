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
    private TextView email, contactNumber, name , birthday;
    private String currentUserID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        fetchCurrentUser();
        setButtonListeners(view);

        fetchUserData();

        return view;
    }

    private void initializeViews(View view) {
        profilePic = view.findViewById(R.id.profilepic);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        contactNumber = view.findViewById(R.id.contactnumber);
        birthday = view.findViewById(R.id.birthday);
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

    private void setButtonListeners(View view) {
        ImageView backProfileImageView = view.findViewById(R.id.backprofile);
        Button buttonRentalHousing = view.findViewById(R.id.buttonCreateRentalHousing);

        backProfileImageView.setOnClickListener(v -> navigateToEditProfile());
        buttonRentalHousing.setOnClickListener(v -> navigateToRentalHousingInfo());
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfile.class);
        startActivity(intent);
    }

    private void navigateToRentalHousingInfo() {
        Intent intent = new Intent(getActivity(), rhinformation1.class);
        startActivity(intent);
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
            email.setText(documentSnapshot.getString("email"));
            name.setText(documentSnapshot.getString("name"));
            contactNumber.setText(documentSnapshot.getString("contactNumber"));

            birthday.setText(documentSnapshot.getString("birthday"));

            loadProfilePicture();
        } else {
            Log.e(TAG, "Document does not exist");
        }
    }

    private void loadProfilePicture() {
        StorageReference profilePicRef = getCurrentProfilePicStorageRef();
        if (profilePicRef != null) {
            profilePicRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Uri uri = task.getResult();
                    Glide.with(this).load(uri).circleCrop().into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.baseline_account_circle_24);
                }
            });
        }
    }

    private StorageReference getCurrentProfilePicStorageRef() {
        if (currentUserID != null) {
            return FirebaseStorage.getInstance().getReference().child("profile").child(currentUserID);
        }
        return null;
    }
}