package com.amityaron.parkease;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.amityaron.parkease.main.HomeFragment;
import com.amityaron.parkease.main.ManageFragment;
import com.amityaron.parkease.main.MapFragment;
import com.amityaron.parkease.main.PaymentsFragment;
import com.amityaron.parkease.main.PersonFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    public void viewProfileInfo(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("Profile Info")
                .setMessage(
                "Email: " + user.getEmail() + "\n"
                + "Name: " + user.getDisplayName() + "\n"
                + "User ID: " + user.getUid() + "\n")
                .setPositiveButton("Done", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void goToLogin(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra("type", "login");
        startActivity(intent);
    }

    public void viewPayments(View view) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new PaymentsFragment()).commit();
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

    public void goToMaps(MenuItem menuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new MapFragment()).commit();
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

    public void goToPerson(View view) {
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

    public void seeAbout(MenuItem menuItem) {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("About")
                .setMessage("This is app for parking car and looking for parking to park car")
                .setNegativeButton("Cancel", (dialog, which) -> goToHome())
                .show();
    }

    public void goToHome(MenuItem ignoredMenuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }

    public void goToManage(View view) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new ManageFragment()).commit();
    }


    public void goToHome(View view) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }


    public void goToHome() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}