package com.amityaron.parkease.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.amityaron.parkease.MainActivity;
import com.amityaron.parkease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment {

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void update(@NonNull View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Spinner spinner = view.findViewById(R.id.spinner);

        if (spinner.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please Select Item First", Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("lots")
                .whereEqualTo("name", spinner.getSelectedItem().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getDocuments().forEach(new Consumer<DocumentSnapshot>() {
                                @Override
                                public void accept(DocumentSnapshot documentSnapshot) {
                                    NumberPicker toll = getActivity().findViewById(R.id.toll);
                                    documentSnapshot
                                            .getReference()
                                            .update("tollperhour", toll.getValue());
                                    Toast.makeText(getContext(), "Toll Updated", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        view.findViewById(R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(view);
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Spinner spinner = view.findViewById(R.id.spinner);

        db.collection("lots")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> arraySpinner = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        arraySpinner.add(document.getString("name"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Log.e("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                db.collection("lots")
                        .whereEqualTo("name", spinner.getSelectedItem().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getDocuments().forEach(new Consumer<DocumentSnapshot>() {
                                @Override
                                public void accept(DocumentSnapshot documentSnapshot) {
                                    NumberPicker toll = getActivity().findViewById(R.id.toll);
                                    Object tollValue = documentSnapshot.get("tollperhour");

                                    toll.setMaxValue(150);
                                    toll.setMinValue(15);

                                    if (tollValue != null) {
                                        toll.setValue(Integer.parseInt(tollValue.toString()));
                                    }

                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}