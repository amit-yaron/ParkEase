package com.amityaron.parkease.main;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amityaron.parkease.R;
import com.amityaron.parkease.auth.AuthHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PersonFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    View view;
    FirebaseUser firebaseUser;

    private Button selectImageButton;
    private Uri filePath;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;


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

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        TextView profileName = rootView.findViewById(R.id.profileName);
        ImageView pfp = rootView.findViewById(R.id.pfp);

        view = rootView;
        firebaseUser = user;

        refreshPfp();


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

        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open File Upload
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);


            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            // You can display the image here if needed
            uploadFile();
        }
    }

    private void refreshPfp() {
        ImageView pfp = view.findViewById(R.id.pfp);

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = null;
        try {
            url = new URL(firebaseUser.getPhotoUrl().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try {
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            pfp.setImageBitmap(bmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadFile() {
        if (filePath != null) {
            StorageReference imageReference = storageReference.child("profile_pictures/" + System.currentTimeMillis() + ".jpg");
            imageReference.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            databaseReference.child(userId).child("profilePictureUrl").setValue(imageUrl);
                            UserProfileChangeRequest profilesUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(imageUrl))
                                    .build();

                            firebaseUser.updateProfile(profilesUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                refreshPfp();
                                                Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                             });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}