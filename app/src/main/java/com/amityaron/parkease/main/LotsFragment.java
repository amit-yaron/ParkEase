package com.amityaron.parkease.main;

import static com.google.android.material.internal.ViewUtils.dpToPx;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LotsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LotsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LotsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LotsFragment newInstance(String param1, String param2) {
        LotsFragment fragment = new LotsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    CardView lotCardView(String name, String address, String city, int stars, int toll) {
//
//        TextView name = cardView.findViewById(R.id.name);
//        name.setText("שמנמ");
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_lots, container, false);

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
                                        FragmentManager manager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();

                                        transaction.replace(R.id.container, new LotFragment()).commit();
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