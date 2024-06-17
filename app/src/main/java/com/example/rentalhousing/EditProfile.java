package com.example.rentalhousing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfile extends AppCompatActivity {

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    ImageView returnButton;
    ImageView profilePic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

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
            Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
            startActivity(intent);
        });

        Button saveButton = findViewById(R.id.buttonSaveChanges);
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
            startActivity(intent);
        });
    }
}
