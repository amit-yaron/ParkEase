package com.amityaron.parkease.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amityaron.parkease.R;
import com.amityaron.parkease.auth.AuthHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PersonFragment extends Fragment {


    public PersonFragment() {
        // Required empty public constructor
    }


    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_person, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TextView profileName = rootView.findViewById(R.id.profileName);
        ImageView pfp = rootView.findViewById(R.id.pfp);

        profileName.setText(user.getDisplayName());

        // Logout Button
        Button logoutButton = rootView.findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onClick(View v) {
                new AuthHandler(getContext()).logout();

                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.replace(R.id.container, new HomeFragment()).commit();

                getActivity().finish();
                getActivity().startActivity(getActivity().getIntent());
            }
        });

        return rootView;
    }
}