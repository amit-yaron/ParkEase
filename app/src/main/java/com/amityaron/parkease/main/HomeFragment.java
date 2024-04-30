package com.amityaron.parkease.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amityaron.parkease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        TextView welcomeText = rootView.findViewById(R.id.welcomeText);
        LinearLayout linearLayout = rootView.findViewById(R.id.layoutHome);

        if (user != null) {
            welcomeText.setText("Welcome, " + user.getDisplayName());
            linearLayout.addView(getLayoutInflater().inflate(R.layout.loginhome, null));

        } else {
            welcomeText.setText("Welcome, Please Log In");
            linearLayout.addView(getLayoutInflater().inflate(R.layout.logouthome, null));
        }


        return rootView;
    }
}