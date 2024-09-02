package com.example.instagram;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button resetPasswordButton;
    FirebaseAuth mAuth;
    private static final String TAG = "ForogtPassword";
    private EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.forgot_password);
        mAuth = FirebaseAuth.getInstance();
        initUI();
        initListener();
    }

    private void initListener() {
        // Display the user's email
        TextView emailTextView = findViewById(R.id.username);
    }

    private void initUI() {
    }
}
