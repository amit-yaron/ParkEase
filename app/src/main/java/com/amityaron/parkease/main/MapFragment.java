package com.amityaron.parkease.main;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amityaron.parkease.MainActivity;
import com.amityaron.parkease.R;
import com.amityaron.parkease.auth.AuthHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Permission;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    Bundle bundle = new Bundle();
    AuthHandler authHandler = new AuthHandler(getContext());

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) { }
    });

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getContext(), "please allow location permissions.", Toast.LENGTH_LONG).show();
            ((MainActivity)getActivity()).goToHome();
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.0f));

                googleMap.addCircle(new CircleOptions().center(latLng).radius(10000).strokeWidth(0f).fillColor(0x220000FF));

                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                marker.setTag("currLocation");
            }
        });

        authHandler.collection("lots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        double latitude = (double) doc.get("latitude");
                        double longitude = (double) doc.get("longitude");
                        String name = doc.get("name").toString();

                        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));

                        marker.setTag(doc.getId());
                    }
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTag().toString().equals("currLocation")) return false;

                authHandler.collection("lots").document(marker.getTag().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            new MaterialAlertDialogBuilder(getContext()).setTitle(doc.get("name").toString()).setMessage("Address: " + doc.get("address") + ", " + doc.get("city") + "\n" + "Stars: " + doc.get("stars") + "/5" + "\n" + "Toll: " + doc.get("tollperhour") + "₪/h" + "\n").setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).setPositiveButton("Buy", (dialog, which) -> {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user == null) {
                                    Toast.makeText(getContext(), "you not signed in", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                authHandler.collection("parks").whereEqualTo("uid", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {
                                                new MaterialAlertDialogBuilder(getContext()).setTitle("You already have parking").setNegativeButton("Cancel", (dialog, which) -> {
                                                    dialog.dismiss();
                                                }).show();

                                            } else {
                                                bundle.putString("lotName", doc.getId());
                                                bundle.putString("lotNameString", doc.get("name").toString());
                                                bundle.putString("lotToll", doc.get("tollperhour").toString());

                                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction transaction = manager.beginTransaction();

                                                LotFragment lotFragment = new LotFragment();
                                                lotFragment.setArguments(bundle);

                                                transaction.replace(R.id.container, lotFragment).commit();
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                                });
                            }).show();
                        }
                    }
                });

                return false;
            }
        });
    }

}