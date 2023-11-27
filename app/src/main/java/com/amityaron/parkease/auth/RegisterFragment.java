package com.amityaron.parkease.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amityaron.parkease.AuthActivity;
import com.amityaron.parkease.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View rootView =  inflater.inflate(R.layout.fragment_register, container, false);

        Activity activity = getActivity();
        TextView loginText = rootView.findViewById(R.id.loginText);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AuthActivity.class);
                intent.putExtra("type", "login");
                startActivity(intent);
            }
        });

        // Handle Register
        rootView.findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout emailTextInputLayout = rootView.findViewById(R.id.email);
                TextInputLayout passwordTextInputLayout = rootView.findViewById(R.id.password);

                try {
                    String email = Objects.requireNonNull(emailTextInputLayout.getEditText()).getText().toString();
                    String password = Objects.requireNonNull(passwordTextInputLayout.getEditText()).getText().toString();

                    new AuthHandler(getContext()).signup(email, password);
                } catch (Exception e) {
                    Toast.makeText(rootView.getContext(), "Some Fields Are Null", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;    }
}