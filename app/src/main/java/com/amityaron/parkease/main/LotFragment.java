package com.amityaron.parkease.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.amityaron.parkease.AuthActivity;
import com.amityaron.parkease.MainActivity;
import com.amityaron.parkease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LotFragment extends Fragment {

    public LotFragment() {
        // Required empty public constructor
    }

    public static LotFragment newInstance(String param1, String param2) {
        LotFragment fragment = new LotFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void handlePayments(boolean taken, int row, int col, Bundle bundle) {
        if (taken) {
            Toast.makeText(getContext(), "Parking Taken!", Toast.LENGTH_LONG).show();
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Want to rent the spot?")
                        .setMessage("Row: " + (row + 1) + " Col: " + (col + 1))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();


                                Map<String, Object> data = new HashMap<>();
                                data.put("value", new Timestamp(new Date()));
                                data.put("uid", user.getUid());

                                db.collection("parks").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "yes", Toast.LENGTH_LONG).show();

                                            ManageFragment manageFragment = new ManageFragment();
                                            manageFragment.setArguments(bundle);
                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.replace(R.id.container, manageFragment);
                                            transaction.commit();
                                        }
                                        else {
                                            Toast.makeText(getContext(), "yes", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        })
                        .show();
            } else {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("You're not logged in")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Log In", (dialog, which) -> goToLogin())
                        .show();
            }
        }
    }

    public void goToLogin() {
        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra("type", "login");
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lot, container, false);

        GridLayout gridLayout = rootView.findViewById(R.id.gridLayout);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle bundle = this.getArguments();

        db.collection("lots")
                .document(bundle.get("lotName").toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Gson gson = new Gson();

                            String lotJsonString = doc.getString("lot");
                            boolean[][] lotData = gson.fromJson(lotJsonString, boolean[][].class);

                            int row = lotData.length;
                            int count = lotData[0].length;

                            gridLayout.setColumnCount(count);
                            gridLayout.setRowCount(row);

                            for (int i = 0; i < row; i++) {
                                for (int j = 0; j < count; j++) {
                                    // Create ImageView
                                    ImageView imageView = new ImageView(getContext());

                                    if (lotData[i][j]) {
                                        imageView.setImageResource(R.drawable.taken_spot);
                                    } else {
                                        imageView.setImageResource(R.drawable.free_spot);
                                    }

                                    // Set layout parameters for each ImageView
                                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                    params.height = 142;
                                    params.width = 142;

                                    params.setMargins(8, 8, 8, 8); // Adjust margins as needed

                                    imageView.setLayoutParams(params);

                                    // Set click listener for each ImageView
                                    int finalI = i;
                                    int finalJ = j;
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            handlePayments(lotData[finalI][finalJ], finalI, finalJ, bundle);
                                        }
                                    });

                                    // Add ImageView to GridLayout
                                    gridLayout.addView(imageView);
                                }
                            }
                        }
                    }
                });

        return  rootView;
    }
}