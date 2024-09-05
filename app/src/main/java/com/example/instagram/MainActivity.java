package com.example.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.instagram.ui.home.HomeFragment;
import com.example.instagram.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.instagram.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button rtnToLogin;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Bundle intent = getIntent().getExtras();
//        if(intent != null) {
//            String publisher = intent.getString("publisherid");
//
//            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//            editor.putString("profileid", publisher);
//            editor.apply();
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new ProfileFragment()).commit();
//        }
//        else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new HomeFragment()).commit();
//        }

        BottomNavigationView navView = binding.navView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_add) {
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    return true;
                } else {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
            }
        });
    }
}

