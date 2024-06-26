package com.amityaron.parkease.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amityaron.parkease.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AuthHandler {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context mContext;

    public AuthHandler(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mContext = context;
    }

    public void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            showToast("Login successful");
                            redirectToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            showToast("Authentication failed. " + task.getException().getMessage());
                            Log.e("Me", task.getException().getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Me", e.getMessage());
                        showToast("Error");
                    }
                });
    }

    public CollectionReference collection(String name) {
        return db.collection(name);
    }

    public void signup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-up user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profilesUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(getDisplayName(email))
                                    .setPhotoUri(Uri.parse("https://i.pinimg.com/736x/c0/27/be/c027bec07c2dc08b9df60921dfd539bd.jpg"))
                                    .build();

                            assert user != null;
                            user.updateProfile(profilesUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast("Sign-up successful");
                                                redirectToMainActivity();
                                            }
                                        }
                                    });
                        } else {
                            // If sign up fails, display a message to the user.
                            showToast("Sign-up failed. " + task.getException().getMessage());
                            Log.e("Me", task.getException().getMessage());
                        }
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
        showToast("Successfully Logged Out");
    }

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private String getDisplayName(String email) {
        int atIndex = email.indexOf('@');

        return email.substring(0, atIndex);
    }

    private void redirectToMainActivity() {
        // Assuming MainActivity is the class for your main activity
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }
}
