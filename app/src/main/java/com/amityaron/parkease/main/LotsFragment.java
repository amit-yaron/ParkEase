package com.amityaron.parkease.main;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amityaron.parkease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.file.Files;

public class LotsFragment extends Fragment {
    public LotsFragment() {
        // Required empty public constructor
    }

    public static LotsFragment newInstance(String param1, String param2) {
        LotsFragment fragment = new LotsFragment();
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

        View rootView =  inflater.inflate(R.layout.fragment_lots, container, false);

        Bundle bundle = new Bundle();

        LinearLayout linearLayout = rootView.findViewById(R.id.list);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lots")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                CardView cardView = (CardView) inflater.inflate(R.layout.lot, container, false);

                                cardView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bundle.putString("lotName", doc.getId());

                                        FragmentManager manager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();

                                        LotFragment lotFragment = new LotFragment();
                                        lotFragment.setArguments(bundle);

                                        transaction.replace(R.id.container, lotFragment).commit();
                                    }
                                });

                                TextView name = cardView.findViewById(R.id.name);
                                TextView address = cardView.findViewById(R.id.address);
                                TextView city = cardView.findViewById(R.id.city);
                                TextView stars = cardView.findViewById(R.id.stars);
                                TextView toll = cardView.findViewById(R.id.toll);


                                name.setText(doc.get("name").toString());
                                address.setText(doc.get("address").toString());
                                city.setText(doc.get("city").toString());
                                stars.setText(doc.get("stars").toString() + "/5");
                                toll.setText(doc.get("tollperhour") + "$/h");

                                linearLayout.addView(cardView);
                            }
                        }
                    }
                });

        return rootView;
    }
}