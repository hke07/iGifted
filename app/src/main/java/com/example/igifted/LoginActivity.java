package com.example.igifted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
public class LoginActivity extends AppCompatActivity {
    TextView createAccountBtnTextView;
    TextView guestTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        guestTextView = findViewById(R.id.guest_login);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);

        createAccountBtnTextView.setOnClickListener((v)-> startActivity(new Intent(LoginActivity.this, RegActivity.class)));
        guestTextView.setOnClickListener((v)-> startActivity(new Intent(LoginActivity.this, MainActivity.class)));
    }
}
