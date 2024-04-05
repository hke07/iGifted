package com.example.igifted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
public class LoginActivity extends AppCompatActivity {
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createAccountBtnTextView = findViewById(R.id.register_tvb);

        createAccountBtnTextView.setOnClickListener((v)-> startActivity(new Intent(LoginActivity.this, RegActivity.class)));
    }
}
