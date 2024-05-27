package com.amityaron.parkease.main;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class UploadJsonLots {

    private static final String TAG = "UploadJsonLots";

    // Define JSON string
    String JSON_STRING;
    Context context;

    public UploadJsonLots(String JSON_STRING, Context context) {
        this.JSON_STRING = JSON_STRING;
        this.context = context;
    }

    public void uploadLotsDataToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // Parse JSON string
        Gson gson = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> lotsData = gson.fromJson(JSON_STRING, listType);

        for (Map<String, Object> lot : lotsData) {
            // Create a new document reference
            batch.set(db.collection("lots").document(), lot);
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Data successfully uploaded to Firebase!");
                Toast.makeText(context, "upload successful", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Error uploading data to Firebase", task.getException());
                Toast.makeText(context, "upload unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }
}
