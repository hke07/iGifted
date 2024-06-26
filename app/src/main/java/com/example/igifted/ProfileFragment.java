package com.example.igifted;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    TextView emailTextView;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void toSettings(View view) {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void toOrders(View view) {
        Intent intent = new Intent(getContext(), OrdersActivity.class);
        startActivity(intent);
    }

    public void toOffers(View view) {
        Intent intent = new Intent(getContext(), OffersActivity.class);
        startActivity(intent);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView settingsImageView = view.findViewById(R.id.settings);
        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSettings(v);
            }
        });

        Button myOrders = view.findViewById(R.id.myOrders);
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOrders(v);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        emailTextView = view.findViewById(R.id.email);
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                emailTextView.setText(email);
            }
        }
    }
}