package com.example.igifted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailPrice;
    Button deleteBtn, editBtn;
    ImageView detailImage;
    String key = "";
    String imageUrl = "";

    TextView quantityTextView, quantityValueTextView;
    ImageButton increaseQuantityButton, decreaseQuantityButton;

    int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailPrice = findViewById(R.id.detailPrice);
        deleteBtn = findViewById(R.id.deleteButton);
        editBtn = findViewById(R.id.editButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailPrice.setText(bundle.getString("Price"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            if (currentUserEmail != null && currentUserEmail.equals("14haiko16op@gmail.com")) {
                deleteBtn.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
            }
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("My Database");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Price", detailPrice.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });




        quantityTextView = findViewById(R.id.quantityTextView);
        quantityValueTextView = findViewById(R.id.quantityValueTextView);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityValueTextView.setText(String.valueOf(quantity));
                updateQuantityInDatabase();
            }
        });

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityValueTextView.setText(String.valueOf(quantity));
                    updateQuantityInDatabase();
                }
            }
        });

        Button addToCartButton = findViewById(R.id.addToCartButton);
        checkIfInCart(addToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addToCartButton.getText().toString().equals("Remove from Cart")) {
                    removeFromCart(addToCartButton);
                } else {
                    addToCart(addToCartButton);
                }
            }
        });

    }

    private void addToCart(final Button addToCartButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference().child("user_cart").child(userId).child(key);

            userCartRef.setValue(getProductDetails()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        addToCartButton.setText("Remove from Cart");
                        Toast.makeText(DetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void removeFromCart(final Button addToCartButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference().child("user_cart").child(userId).child(key);
            userCartRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        addToCartButton.setText("Add To Cart");
                        Toast.makeText(DetailActivity.this, "Removed from Cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "Failed to remove from Cart", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkIfInCart(final Button addToCartButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference().child("user_cart").child(userId).child(key);
            userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.child("quantity").getValue() != null) {
                        addToCartButton.setText("Remove from Cart");
                        quantity = dataSnapshot.child("quantity").getValue(Integer.class);
                        quantityValueTextView.setText(String.valueOf(quantity));
                    } else {
                        addToCartButton.setText("Add To Cart");
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void updateQuantityInDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference().child("user_cart").child(userId).child(key);

            userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userCartRef.child("quantity").setValue(quantity);
                    } else {
                        Toast.makeText(DetailActivity.this, "Item is not in the cart", Toast.LENGTH_SHORT).show();
                        quantityValueTextView.setText(String.valueOf(quantity));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private Map<String, Object> getProductDetails() {
        Map<String, Object> productDetails = new HashMap<>();
        productDetails.put("dataDesc", detailDesc.getText().toString());
        productDetails.put("dataImage", imageUrl);
        productDetails.put("dataPrice", detailPrice.getText().toString());
        productDetails.put("dataTitle", detailTitle.getText().toString());
        return productDetails;
    }

}