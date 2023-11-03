package com.amityaron.parkease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amityaron.parkease.auth.LoginActivity;
import com.amityaron.parkease.auth.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToRegister(View view) {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

    public void goToLogin(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}