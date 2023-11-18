package com.amityaron.parkease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.amityaron.parkease.auth.LoginFragment;
import com.amityaron.parkease.auth.RegisterFragment;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getStringExtra("type");

            if (Objects.equals(type, "login")) {
                transaction.replace(R.id.container, new LoginFragment()).commit();
            } else if (Objects.equals(type, "register")) {
                transaction.replace(R.id.container, new RegisterFragment()).commit();
            }
        }
    }

    public void goToMain(MenuItem item) {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
    }
}