package com.example.rentalhousing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1234;
    private GoogleSignInClient client;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuplayout);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);

        // Check if the user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserInFirestore(currentUser.getUid());
        } else {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    Log.d(TAG, "ID Token: " + idToken);

                    // Proceed with Firebase authentication using the ID token
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            // Check if user exists in Firestore
                                            checkUserInFirestore(user.getUid());
                                        }
                                    } else {
                                        // Sign in failed
                                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                }
            } catch (ApiException e) {
                // Sign in failed
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google sign-in failed", e);
                finish();
            }
        }
    }

    private void checkUserInFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // User exists, go to loginpincode activity
                    startActivity(new Intent(MainActivity.this, loginpincode.class));
                } else {
                    // User does not exist, go to signupinformation activity
                    startActivity(new Intent(MainActivity.this, signupinformation.class));
                }
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Failed to check user existence", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
