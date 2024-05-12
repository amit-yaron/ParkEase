package com.amityaron.parkease.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amityaron.parkease.R;
import com.amityaron.parkease.auth.AuthHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentsFragment extends Fragment {

    AuthHandler authHandler = new AuthHandler(getContext());

    public PaymentsFragment() {
        // Required empty public constructor
    }

    public static PaymentsFragment newInstance(String param1, String param2) {
        PaymentsFragment fragment = new PaymentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_payments, container, false);

        LinearLayout linearLayout = rootView.findViewById(R.id.list);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authHandler.collection("payments")
                .whereEqualTo("uid", user.getUid())
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                CardView cardView = (CardView) inflater.inflate(R.layout.payment, container, false);

                                TextView name = cardView.findViewById(R.id.name);
                                TextView shekels = cardView.findViewById(R.id.shekels);
                                TextView minutes = cardView.findViewById(R.id.minutes);
                                TextView date = cardView.findViewById(R.id.date);
                                TextView time = cardView.findViewById(R.id.time);

                                Date nDate = ((Timestamp)doc.get("date")).toDate();

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                                DateFormat timeFormat = new SimpleDateFormat("hh:mm");

                                String dateText = dateFormat.format(nDate);
                                String timeText = timeFormat.format(nDate);

                                name.setText(doc.get("name").toString());
                                shekels.setText(doc.get("shekels") + "₪");
                                minutes.setText(doc.get("minutes") + "m");
                                date.setText(dateText);
                                time.setText(timeText);

                                linearLayout.addView(cardView);
                            }
                        }
                    }
                });

        return  rootView;
    }
}