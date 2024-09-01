package com.example.instagram.ui.profile;

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
        userRef.addValueEventListener(new ValueEventListener() {
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
    }

    private void updateUI() {
        if (displayedUser != null) {
            username.setText(displayedUser.getUsername() != null ? displayedUser.getUsername() : "No username");
            userBio.setText(displayedUser.getBio() != null ? displayedUser.getBio() : "No bio available");
            //avatar.setText(displayedUser.getImageUrl() != null ? displayedUser.getImageUrl() : "No image URL");
            fetchFollowingCount();
            fecthFollowerCount();
            checkFollowStatus();
        }
    }

    private void fecthFollowerCount() {
        followRef.child(displayedUser.getId()).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followersCount = dataSnapshot.getChildrenCount();
                userFollower.setText(String.valueOf(followersCount) + "Followers");
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
                long followingCount = dataSnapshot.getChildrenCount();
                userFollowing.setText(String.valueOf(followingCount) + "Following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch followers count", Toast.LENGTH_SHORT).show();
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
        DatabaseReference userFollowersRef = followRef.child(displayedUser.getId()).child("follower").child(currentUser.getUid());
        if (isFollowing) {
            userFollowingRef.removeValue();
            userFollowersRef.removeValue();
            updateFollowCounts(-1);
        } else {
            userFollowingRef.setValue(true);
            userFollowersRef.setValue(true);
            updateFollowCounts(1);
        }
    }

    private void updateFollowCounts(int change) {
        userRef.child(currentUser.getUid()).child("following")
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer currentValue = mutableData.getValue(Integer.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue + change);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                        if (!committed) {
                        }
                    }
                });

        userRef.child(displayedUser.getId()).child("followers")
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer currentValue = mutableData.getValue(Integer.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue + change);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                        if (committed) {
                            loadUserData();
                        }
                    }
                });
    }
}