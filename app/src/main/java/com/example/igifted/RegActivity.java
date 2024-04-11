package com.example.igifted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegActivity extends AppCompatActivity {

    EditText regUsername, regEmail, regPassword, regConfPassword;
    TextView loginTVB;
    Button regBtn;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        regUsername = findViewById(R.id.username);
        regEmail = findViewById(R.id.email_reg);
        regPassword = findViewById(R.id.password_reg);
        regConfPassword = findViewById(R.id.confirm_password);
        regBtn = findViewById(R.id.register_btn);
        loginTVB = findViewById(R.id.login_tvb);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String username = regUsername.getText().toString();
                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();
                String conf_password = regConfPassword.getText().toString();

                if(password == conf_password) {
                    HelperClass helperClass = new HelperClass(username, email, password);
                    reference.child(username).setValue(helperClass);


                    Toast.makeText(RegActivity.this, "You have registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginTVB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
