package com.amityaron.parkease.auth;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amityaron.parkease.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthHandler {
    private FirebaseAuth mAuth;
    private Context mContext;

    public AuthHandler(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
    }

    public void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            showToast("Login successful");
                            redirectToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            showToast("Authentication failed. " + task.getException().getMessage());
                        }
                    }
                });
    }

    public void signup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-up user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            showToast("Sign-up successful");
                            redirectToMainActivity();
                            // You can add additional actions here if needed, e.g., redirect to MainActivity
                        } else {
                            // If sign up fails, display a message to the user.
                            showToast("Sign-up failed. " + task.getException().getMessage());
                        }
                    }
                });
    }


    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void redirectToMainActivity() {
        // Assuming MainActivity is the class for your main activity
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }
}
