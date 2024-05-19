package com.example.igifted;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    TextView totalTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button confirmBtn = view.findViewById(R.id.checkout);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder(v);
            }
        });

        totalTextView = view.findViewById(R.id.total);
        recyclerView = view.findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("user_cart").child(userId);
            dialog.show();

            eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();
                    double totalPrice = 0.0;
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                        dataClass.setKey(itemSnapshot.getKey());
                        dataList.add(dataClass);

                        // Calculate total price
                        String priceStr = dataClass.getDataPrice();
                        if (priceStr != null) {
                            double price = Double.parseDouble(priceStr);
                            int quantity = dataClass.getQuantity();
                            if(quantity < 2){
                                quantity = 1;
                            }
                            totalPrice += price * quantity;
                        }
                    }
                    totalTextView.setText(String.format("%.2f $", totalPrice));
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                }
            });
        }
    }

    public void confirmOrder(View view) {
        if (dataList.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to confirm your order?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference().child("user_cart").child(userId);

                                DatabaseReference userPurchasesRef = FirebaseDatabase.getInstance().getReference().child("user_purchases").child(userId);

                                userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                            DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                                            userPurchasesRef.child(itemSnapshot.getKey()).setValue(dataClass); // Store item under user_purchases branch
                                        }

                                        userCartRef.removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle onCancelled
                                    }
                                });
                            }

                            dataList.clear();
                            adapter.notifyDataSetChanged();

                            Toast.makeText(getContext(), "Thank you for your order. We will contact you soon", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventListener != null && databaseReference != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}