package com.example.igifted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView registerTVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.email_login);
        loginPassword = findViewById(R.id.password_login);
        registerTVB = findViewById(R.id.register_tvb);
        loginBtn = findViewById(R.id.login_btn);
    }

    public Boolean validateEmail(){
        String val = loginEmail.getText().toString();
        if(val.isEmpty()){
            loginEmail.setError("Email Can't be empty");
            return false;
        }else{
            loginEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Password Can't be empty");
            return false;
        }else{
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loginEmail.setError(null);
                    String passwordFromDB = snapshot.child(userEmail).child("password").getValue(String.class);

                    if(!Objects.equals(passwordFromDB,userPassword)){
                        loginEmail.setError(null);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        loginPassword.setError("Invalid Password");
                        loginPassword.requestFocus();
                    }
                }else{
                    loginEmail.setError("User does not exist");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
