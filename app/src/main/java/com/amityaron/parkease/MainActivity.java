package com.amityaron.parkease;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
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
import com.amityaron.parkease.misc.NetworkChangeReceiver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Handle Navigation Drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);


        goToHome();
    }

    public void getTextForNavDrawer(View view) {

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
    }

    public void goToMaps() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new MapFragment()).commit();
    }


    public void goToMaps(View view) {
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

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }

    public void goToManage(View view) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new ManageFragment()).commit();
    }

    public void goToManage(MenuItem menuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new ManageFragment()).commit();
    }

    public void goToHome() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new HomeFragment()).commit();
    }


}