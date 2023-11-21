package com.amityaron.parkease;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amityaron.parkease.main.HomeFragment;
import com.amityaron.parkease.main.PersonFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Navigation Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the navigation drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        goToHome();
    }

    public void goToLogin(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra("type", "login");
        startActivity(intent);
    }

    public void goToLogin() {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra("type", "login");
        startActivity(intent);
    }

    public void goToRegister(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra("type", "register");
        startActivity(intent);
    }

    public void goToPerson(MenuItem menuItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.container, new PersonFragment()).commit();
        } else {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("You're not logged in")
                    .setNegativeButton("Cancel", (dialog, which) -> goToHome())
                    .setPositiveButton("Log In", (dialog, which) -> goToLogin())
                    .show();
        }
    }

    public void goToHome(MenuItem ignoredMenuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }

    public void goToHome() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }
}