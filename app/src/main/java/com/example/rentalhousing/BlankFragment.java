package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class BlankFragment extends Fragment {

    private ImageView profilePic;
    private TextView userName;
    private TextView userStatus;
    private Button accountButton;
    private Button createRentalHousingButton;
    private Button enableBiometricsButton;
    private Button logoutButton;

    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profiletalaga, container, false);

        // Initialize views
        profilePic = view.findViewById(R.id.profilepic);
        userName = view.findViewById(R.id.textView8);
        userStatus = view.findViewById(R.id.textView14);
        accountButton = view.findViewById(R.id.button3);
        createRentalHousingButton = view.findViewById(R.id.button5);
        enableBiometricsButton = view.findViewById(R.id.button6);
        logoutButton = view.findViewById(R.id.button7);

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        // Set up listeners for buttons
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase Auth and Google Sign-In
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                    // Redirect to LoginMenu activity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();  // Finish current activity
                });
            }
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileFragment.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        createRentalHousingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), rhinformation1.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        enableBiometricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BiometricsActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        return view;
    }
}