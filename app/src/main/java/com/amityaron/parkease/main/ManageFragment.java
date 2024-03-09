package com.amityaron.parkease.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import java.util.concurrent.ThreadLocalRandom;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amityaron.parkease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageFragment extends Fragment {
    public ManageFragment() {
        // Required empty public constructor
    }

    public static ManageFragment newInstance(String param1, String param2) {
        ManageFragment fragment = new ManageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FirebaseFirestore db;
    FirebaseAuth user;
    Timer timer;
    TextView text;
    Timestamp ts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance();
        text = rootView.findViewById(R.id.text1);

        db.collection("parks")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().getDocuments().isEmpty()) {
                            text.setText("You have no active parking");
                            rootView.findViewById(R.id.stop).setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                ts = (Timestamp) doc.get("value");
                                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView rate = rootView.findViewById(R.id.rate);
                                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lotNameString = rootView.findViewById(R.id.lotnameString);
                                lotNameString.setText("You have been parking in " + doc.get("lotNameString").toString() + " for");

                                if (timer == null) {
                                    timer = new Timer();
                                    timer.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    text.setText(stringGapDates(ts.toDate()) + " Minutes");

                                                    long timePassedMillis = System.currentTimeMillis() - ts.toDate().getTime();
                                                    long secondsPassed = timePassedMillis / 1000;

                                                    long minutes = (secondsPassed / 60);

                                                    int toll = Integer.parseInt(doc.get("lotToll").toString());
                                                    int shekels = (int) ((double) minutes / 60 * toll);

                                                    rate.setText("Charge: " + shekels + "₪" + "\nRate: " + doc.get("lotToll").toString() + "₪/h");

                                                }
                                            });
                                        }
                                    }, 0, 1000);
                                }
                            }

                        }
                    }
                });

        rootView.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("parks")
                        .whereEqualTo("uid", user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {

                                    db.collection("parks").document(doc.getId()).delete();
                                    timer.cancel();
                                    text.setText("it is over");

                                    Map<String, Object> data = new HashMap<>();
                                    Bundle bundle = getArguments();

                                    long timePassedMillis = System.currentTimeMillis() - ts.toDate().getTime();
                                    long secondsPassed = timePassedMillis / 1000;

                                    long minutes = (secondsPassed / 60);

                                    int toll = Integer.parseInt(doc.get("lotToll").toString());
                                    int shekels = (int) ((double) minutes / 60 * toll);

                                    data.put("date", new Timestamp(new Date()));
                                    data.put("name", doc.get("lotNameString"));
                                    data.put("uid", user.getUid());
                                    data.put("minutes", minutes);
                                    data.put("shekels", shekels);
                                    data.put("lotId", doc.get("lotName"));

                                    db.collection("payments")
                                            .add(data)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getContext(), "Spot Bought!", Toast.LENGTH_LONG).show();

                                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                                    FragmentTransaction transaction = manager.beginTransaction();

                                                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                                    notificationManager.cancel(0);

                                                    transaction.replace(R.id.container, new HomeFragment()).commit();
                                                }
                                            });

                                }

                            }
                        });

            }
        });

        return rootView;
    }

    void updateCounter(View rootView) {

        db.collection("parks")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Timestamp date1 = (Timestamp) doc.get("value");

                                if (timer == null) {
                                    timer = new Timer();
                                    timer.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            // Use a Handler to post updates to the main thread
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    text.setText(stringGapDates(date1.toDate()));
                                                }
                                            });
                                        }
                                    }, 0, 1000);
                                } else {
                                    Toast.makeText(getContext(), "timer exist", Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                        return;
                    }
                });
    }


    String stringGapDates(Date startDate) {
        long timePassedMillis = System.currentTimeMillis() - startDate.getTime();
        long secondsPassed = timePassedMillis / 1000;

        long minutes = secondsPassed / 60;
        long seconds = secondsPassed % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}