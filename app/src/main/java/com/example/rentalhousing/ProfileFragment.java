package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;

import androidx.fragment.app.Fragment;
public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView backProfileImageView = view.findViewById(R.id.backprofile);
        backProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });

        Button buttonrentalhousing = view.findViewById(R.id.buttonCreateRentalHousing);

        buttonrentalhousing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RHInformation.class);
                startActivity(intent);
            }
        });


        return view;
    }

}