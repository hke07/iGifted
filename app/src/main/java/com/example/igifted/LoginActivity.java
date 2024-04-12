package com.example.igifted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView registerTVB, error_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.email_login);
        loginPassword = findViewById(R.id.password_login);
        registerTVB = findViewById(R.id.register_tvb);
        loginBtn = findViewById(R.id.login_btn);

        error_text = findViewById(R.id.error);


        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void toReg(View view) {
        Intent intent = new Intent(LoginActivity.this, RegActivity.class);
        startActivity(intent);
    }
    public void signIn(View v) {
        final String email = loginEmail.getText().toString();
        final String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            error_text.setText("All fields are required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null && user.isEmailVerified()) {
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                mAuth.signOut();
                                error_text.setText("Email is not verified. Please verify your email.");
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            error_text.setText("Authentication failed: " + task.getException().getMessage());
                        }
                    }
                });
    }
}
