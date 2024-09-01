package com.example.instagram.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

public class ProfileFragment2 extends Fragment {
    private RecyclerView recyclerView;
    private Button followBtn;
    private TextView userPost, userFollowing, userFollower, userBio, username, avatar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserModel displayedUser;
    private DatabaseReference userRef, followRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_user_profile, container, false);

        initializeViews(view);
        setupFirebase();
        loadUserData();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        followBtn = view.findViewById(R.id.btn_follow_profile);
        username = view.findViewById(R.id.username);
        userPost = view.findViewById(R.id.user_post);
        userFollower = view.findViewById(R.id.user_follower);
        userBio = view.findViewById(R.id.user_bio);
        userFollowing = view.findViewById(R.id.user_following);
        followBtn.setOnClickListener(v -> toggleFollow());
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        followRef = FirebaseDatabase.getInstance().getReference().child("Follow");
    }

    private void loadUserData() {
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String profileId = prefs.getString("profileid", "none");
        if (!profileId.equals("none")) {
            userRef.child(profileId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        displayedUser = dataSnapshot.getValue(UserModel.class);
                        if (displayedUser != null) {
                            updateUI();
                        }
                    } else {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No profile ID found", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUI() {
        username.setText(displayedUser.getUsername() != null ? displayedUser.getUsername() : "No username");
        userBio.setText(displayedUser.getBio() != null ? displayedUser.getBio() : "No bio available");
        if (displayedUser != null & displayedUser.getId()!=null) {
            fetchFollowingCount();
            fetchFollowerCount();
            checkFollowStatus();
        }
    }

    private void fetchFollowerCount() {
        followRef.child(displayedUser.getId()).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followersCount = dataSnapshot.exists() ? dataSnapshot.getChildrenCount() : 0;
                userFollower.setText(String.valueOf(followersCount) + " Followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch followers count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFollowingCount() {
        followRef.child(displayedUser.getId()).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followingCount = dataSnapshot.exists() ? dataSnapshot.getChildrenCount() : 0;
                userFollowing.setText(String.valueOf(followingCount) + " Following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch following count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFollowStatus() {
        followRef.child(currentUser.getUid()).child("following").child(displayedUser.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        followBtn.setText(dataSnapshot.exists() ? "Following" : "Follow");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to check follow status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleFollow() {
        boolean isFollowing = followBtn.getText().toString().equals("Following");
        DatabaseReference userFollowingRef = followRef.child(currentUser.getUid()).child("following").child(displayedUser.getId());
        DatabaseReference userFollowersRef = followRef.child(displayedUser.getId()).child("followers").child(currentUser.getUid());
        if (isFollowing) {
            userFollowingRef.removeValue();
            userFollowersRef.removeValue();
        } else {
            userFollowingRef.setValue(true);
            userFollowersRef.setValue(true);
        }
        loadUserData();
    }
}