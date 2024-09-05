package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.UsersAdapter;
import com.example.instagram.Model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowListActivity extends AppCompatActivity {
    private String id;
    private String title;
    private List<String> idList;

    private RecyclerView recyclerView;
    private UsersAdapter userAdapter;
    private List<UserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        if (id == null || title == null) {
            Log.e("FollowListActivity", "Intent extras are null. id: " + id + ", title: " + title);
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("FollowListActivity", "Received id: " + id + ", title: " + title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(title.equals("followers")) {
            getSupportActionBar().setTitle("Followers");
        }
        else {
            getSupportActionBar().setTitle("Following");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UsersAdapter(this, userList, false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        switch (title.toLowerCase()) {
            case "likes":
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
            default:
                Log.e("FollowListActivity", "Unknown title: " + title);
                Toast.makeText(this, "Unknown list type", Toast.LENGTH_SHORT).show();
                finish();
        }
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey() != null) {
                        idList.add(snapshot.getKey());
                    } else {
                        Log.e("getFollowers", "Snapshot key is null.");
                    }
                }
                Log.d("getFollowers", "Followers count: " + idList.size());
                showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getFollowers", "Failed to retrieve followers: " + databaseError.getMessage());
                Toast.makeText(FollowListActivity.this, "Failed to load followers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey() != null) {
                        idList.add(snapshot.getKey());
                    } else {
                        Log.e("getFollowing", "Snapshot key is null.");
                    }
                }
                Log.d("getFollowing", "Following count: " + idList.size());
                showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getFollowing", "Failed to retrieve following: " + databaseError.getMessage());
                Toast.makeText(FollowListActivity.this, "Failed to load following", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey() != null) {
                        idList.add(snapshot.getKey());
                    } else {
                        Log.e("getLikes", "Snapshot key is null.");
                    }
                }
                Log.d("getLikes", "Likes count: " + idList.size());
                showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getLikes", "Failed to retrieve likes: " + databaseError.getMessage());
                Toast.makeText(FollowListActivity.this, "Failed to load likes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null && user.getId() != null) {
                        for (String id : idList) {
                            if (user.getId().equals(id)) {
                                userList.add(user);
                            }
                        }
                    } else {
                        Log.e("showUsers", "User or user ID is null.");
                    }
                }
                Log.d("showUsers", "User list updated, size: " + userList.size());
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("showUsers", "Failed to load users: " + databaseError.getMessage());
                Toast.makeText(FollowListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

