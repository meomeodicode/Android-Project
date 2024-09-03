package com.example.instagram.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.Photo;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.example.instagram.post.Post;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button followBtn, editProfile;
    private ImageButton addToPhotoBtn, menuBtn, shareLink;
    private TextView userPost, userFollowing, userFollower, userBio, username, avatar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserModel displayedUser;
    private DatabaseReference userRef, followRef;
    private List<Post> postList;
    private List<Post> postList_saves;
    private Photo postThumbnailAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(view);
        setupFirebase();
        loadUserData();
        fetchPhoto();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postThumbnailAdapter  = new Photo(getContext(), postList);
        followBtn = view.findViewById(R.id.btn_follow_profile);
        username = view.findViewById(R.id.profile_username);
        userPost = view.findViewById(R.id.profile_posts_count);
        userFollower = view.findViewById(R.id.profile_followers_count);
        userFollowing = view.findViewById(R.id.profile_following_count);
        addToPhotoBtn = view.findViewById(R.id.profile_add_to_photos_button);
        menuBtn = view.findViewById(R.id.profile_menu_button);
        shareLink = view.findViewById(R.id.profile_share_link);
        userBio = view.findViewById(R.id.profile_bio);
        editProfile = view.findViewById(R.id.profile_edit_button);
        editProfile.setOnClickListener(v -> initEdit());
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postThumbnailAdapter = new Photo(getContext(), postList);
        recyclerView.setAdapter(postThumbnailAdapter);
    }
    private void initEdit() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        }
        );
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
        }
    }

    private void fetchFollowerCount() {
        followRef.child(displayedUser.getId()).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followersCount = dataSnapshot.exists() ? dataSnapshot.getChildrenCount() : 0;
                userFollower.setText(String.valueOf(followersCount));
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
                userFollowing.setText(String.valueOf(followingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch following count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPhoto() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Log.d("fetchPhoto", "Starting to fetch photos");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Log.d("fetchPhoto", "Cleared postList");
                SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                String profileId = prefs.getString("profileid", "none");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        Log.d("fetchPhoto", "Processing post: " + post.getPostImage() + ", Publisher: " + post.getPublisher());
                        if (post.getPublisher().equals(profileId)) {
                            postList.add(post);
                            Log.d("fetchPhoto", "Added post to postList: " + post.getPostImage());
                        }
                    } else {
                        Log.d("fetchPhoto", "Post is null, skipping");
                    }
                }
                Log.d("fetchPhoto", "Post list size before reversing: " + postList.size());
                Collections.reverse(postList);
                Log.d("fetchPhoto", "Post list size after reversing: " + postList.size());

                postThumbnailAdapter.notifyDataSetChanged();
                Log.d("fetchPhoto", "Adapter notified of data change");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchPhoto", "Failed to fetch photos: " + error.getMessage());
            }
        });
    }
}