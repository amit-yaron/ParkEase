package com.amityaron.parkease;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amityaron.parkease.main.AdminFragment;
import com.amityaron.parkease.main.HomeFragment;
import com.amityaron.parkease.main.ManageFragment;
import com.amityaron.parkease.main.MapFragment;
import com.amityaron.parkease.main.PaymentsFragment;
import com.amityaron.parkease.main.PersonFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private boolean someDenied = false;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (!result) someDenied = true;
            }
        }).launch(Manifest.permission.POST_NOTIFICATIONS);


        if (someDenied) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Go to settings and allow permissions you have denied")
                    .setCancelable(false)
                    .show();
        }

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

    public void goToLogin(View view) {
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

    public void goToRegister(View view) {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra("type", "register");
        startActivity(intent);
    }

    public void goToMaps(MenuItem menuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new MapFragment()).commit();
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    public void goToMaps() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        transaction.replace(R.id.container, new MapFragment()).commit();
    }


    public void goToMaps(View view) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        transaction.replace(R.id.container, new MapFragment()).commit();
    }


    public void goToPerson(MenuItem menuItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            bottomNavigationView.getMenu().getItem(0).setChecked(true);
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
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        } else {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("You're not logged in")
                    .setNegativeButton("Cancel", (dialog, which) -> goToHome())
                    .setPositiveButton("Log In", (dialog, which) -> goToLogin())
                    .show();
        }
    }

    public void seeAbout(View view) {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("About")
                .setMessage("This is app for parking car and looking for parking to park car")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    String m_Text = "";

    public void goToAdmin(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Password");

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();

                if (m_Text.equals("12345678")) {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();

                    transaction.replace(R.id.container, new AdminFragment()).commit();
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Admin Authenticated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Admin Authentication Failed", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void goToHome(MenuItem ignoredMenuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }

    public void goToManage(View view) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new ManageFragment()).commit();
    }

    public void goToHome() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }


}