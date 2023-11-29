package com.amityaron.parkease.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amityaron.parkease.MainActivity;
import com.amityaron.parkease.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    public MapFragment() {
        // Required empty public constructor
    }


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    Bundle bundle = new Bundle();

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lots").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            double latitude = (double) doc.get("latitude");
                            double longitude = (double) doc.get("longitude");
                            String name = doc.get("name").toString();

                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(name));

                            marker.setTag(doc.getId());
                        }
                    }
                }
            });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("lots")
                        .document(marker.getTag().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();

                                    new MaterialAlertDialogBuilder(getContext())
                                            .setTitle(doc.get("name").toString())
                                            .setMessage(
                                                    "Address: " + doc.get("address") + ", " + doc.get("city") + "\n"
                                                            + "Stars: " + doc.get("stars") + "/5" + "\n"
                                                            + "Toll: " + doc.get("tollperhour") + "₪/h" + "\n")
                                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                            .setPositiveButton("Buy", (dialog, which) -> {
                                                bundle.putString("lotName", doc.getId());
                                                bundle.putString("lotNameString", doc.get("name").toString());
                                                bundle.putString("lotToll", doc.get("tollperhour").toString());

                                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction transaction = manager.beginTransaction();

                                                LotFragment lotFragment = new LotFragment();
                                                lotFragment.setArguments(bundle);

                                                transaction.replace(R.id.container, lotFragment).commit();
                                                dialog.dismiss();
                                            })
                                            .show();
                                }
                            }
                        });

                return false;
            }
        });
    }
}