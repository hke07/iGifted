package com.example.igifted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegActivity extends AppCompatActivity {

    EditText regUsername, regEmail, regPassword, regConfPassword;
    TextView loginTVB;
    Button regBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "RegActivity";
    TextView error_text;
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


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        error_text = findViewById(R.id.error);

    }
    public void toLogin(View view) {
        Intent intent = new Intent(RegActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void signUp(View v) {
        final String username = regUsername.getText().toString();
        String email = regEmail.getText().toString();
        String password = regPassword.getText().toString();
        String confirmPassword = regConfPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            error_text.setText("All fields are required");
            return;
        }

        if (!password.matches(".*\\d.*")) {
            error_text.setText("Password must contain at least one number");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            error_text.setText("Password must contain at least one capital letter");
            return;
        }

        if (!password.equals(confirmPassword)) {
            error_text.setText("Passwords don't match");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", username);
                            userMap.put("email", email);
                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            sendEmailVerification(user);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                            sendEmailVerification(user);
                                        }
                                    });
                            sendEmailVerification(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            error_text.setText("Authentication failed: " + task.getException().getMessage());
                        }
                    }
                });
    }


    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegActivity.this,
                                    "Failed to send verification email: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}

