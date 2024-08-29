package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNextActivity();
            }
        }, 2000);
    }

    private void startNextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //If user==null, not logging in
        if (user == null) {
            navigateToLoginActivity();
        }
        else {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            navigateToLoginActivity();
                        }
                        else {
                            FirebaseAuth.getInstance().signOut();
                            navigateToLoginActivity();
                        }
                    } else {
                        Log.e("UserVerification", "Error reloading user", task.getException());
                        handleReloadError();
                        navigateToLoginActivity();
                    }
                }
            });
        }
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleReloadError() {
        Log.e("SplashActivity", "Error reloading user data. Navigating to login.");
    }
}