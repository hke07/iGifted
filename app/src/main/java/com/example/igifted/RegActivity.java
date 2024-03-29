package com.example.igifted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
public class RegActivity extends AppCompatActivity {
    TextView loginBtnTextView;
    TextView guestTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        loginBtnTextView = findViewById(R.id.login_text_view_btn);
        guestTextView = findViewById(R.id.guest_reg);

        loginBtnTextView.setOnClickListener((v)-> startActivity(new Intent(RegActivity.this, LoginActivity.class)));
        guestTextView.setOnClickListener((v)-> startActivity(new Intent(RegActivity.this, MainActivity.class)));
    }
}
