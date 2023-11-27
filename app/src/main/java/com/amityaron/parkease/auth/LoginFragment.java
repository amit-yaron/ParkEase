package com.amityaron.parkease.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amityaron.parkease.AuthActivity;
import com.amityaron.parkease.MainActivity;
import com.amityaron.parkease.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        View rootView =  inflater.inflate(R.layout.fragment_login, container, false);

        Activity activity = getActivity();
        TextView registerText = rootView.findViewById(R.id.registerText);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AuthActivity.class);
                intent.putExtra("type", "register");
                startActivity(intent);
            }
        });

        // Handle Login
        rootView.findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout emailTextInputLayout = rootView.findViewById(R.id.email);
                TextInputLayout passwordTextInputLayout = rootView.findViewById(R.id.password);

                try {
                    String email = Objects.requireNonNull(emailTextInputLayout.getEditText()).getText().toString();
                    String password = Objects.requireNonNull(passwordTextInputLayout.getEditText()).getText().toString();

                    new AuthHandler(getContext()).login(email, password);
                } catch (Exception e) {
                    Toast.makeText(rootView.getContext(), "Some Fields Are Null", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Inflate the layout for this fragment
        return rootView;

    }
}