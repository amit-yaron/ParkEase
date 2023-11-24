package com.amityaron.parkease.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.amityaron.parkease.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LotFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LotFragment newInstance(String param1, String param2) {
        LotFragment fragment = new LotFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lot, container, false);

        GridLayout gridLayout = rootView.findViewById(R.id.gridLayout);

        gridLayout.setColumnCount(5);
        gridLayout.setRowCount(6);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                // Create ImageView
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.free_spot);

                // Set layout parameters for each ImageView
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.height = 142;
                params.width = 142;

                params.setMargins(8, 8, 8, 8); // Adjust margins as needed

                imageView.setLayoutParams(params);

                // Set click listener for each ImageView
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle item click (you can perform actions based on the clicked item)
                        Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                // Add ImageView to GridLayout
                gridLayout.addView(imageView);
            }
        }

        return  rootView;
    }
}