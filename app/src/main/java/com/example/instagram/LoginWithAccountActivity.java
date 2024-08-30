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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginWithAccountActivity extends AppCompatActivity {
    private static final String TAG = "LoginWithAccountActivity";

    private EditText editPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private String email;

    private TextView toLogin, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_with_account);
        mAuth = FirebaseAuth.getInstance();
        email = getIntent().getStringExtra("email");
        initUI();
        initListener();
    }

    private void initUI() {
        editPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        toLogin = findViewById(R.id.login_with_another_account);
        forgotPassword = findViewById(R.id.forgot_password);
        // Display the user's email
        TextView emailTextView = findViewById(R.id.username);
        emailTextView.setText(email);
    }

    private void initListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(LoginWithAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent intent = new Intent(LoginWithAccountActivity.this, ForgotPasswordActivity.class);
                        startActivity(intent);
                        finish();
            }
        });
    }

    private void loginUser() {
        String password = editPassword.getText().toString().trim();
        if (password.isEmpty()) {
            Toast.makeText(LoginWithAccountActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            navigateToMainActivity();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginWithAccountActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
