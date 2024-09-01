package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button resetPasswordButton, backToLogin, createAccount;
    FirebaseAuth mAuth;
    String trimmedEmail;
    private static final String TAG = "ForogtPassword";
    private EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        mAuth = FirebaseAuth.getInstance();
        initUI();
        initListener();
    }
    private void initUI() {
        email = findViewById(R.id.emailInput);
        resetPasswordButton = findViewById(R.id.sendLoginLinkButton);
        createAccount = findViewById(R.id.createNewAccountButton);
        backToLogin = findViewById(R.id.backToLoginButton);
    }
    private void initListener() {
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetPassword();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendResetPassword()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        trimmedEmail =  email.getText().toString().trim();
        mAuth.sendPasswordResetEmail(trimmedEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Reset email sent successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(ForgotPasswordActivity.this, "Reset email sent failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
