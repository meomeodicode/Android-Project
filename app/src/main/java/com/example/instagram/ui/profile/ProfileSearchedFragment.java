package com.example.instagram.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.Photo;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.example.instagram.post.Post;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileSearchedFragment extends Fragment {
    private RecyclerView recyclerView, recyclerView_saves;
    private Button followBtn, editProfile;
    boolean isCurrentUser, flag;
    private ImageButton backBtn;
    private TextView userPost, userFollowing, userFollower, userBio, username, avatar, privateText;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserModel displayedUser;
    private DatabaseReference userRef, followRef;
    private List<Post> postList, postList_saves;
    private Photo postThumbnailAdapter, postThumbnailAdapterSaves;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_user_profile, container, false);
        initializeViews(view);
        setupFirebase();
        loadUserData();
        fetchPhoto();
        TabLayout tabLayout = view.findViewById(R.id.profile_tab_layout_2);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_all));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_favorite));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = getResources().getColor(android.R.color.black);
                if (tab.getIcon() != null) {
                    tab.getIcon().setTint(tabIconColor);
                }
                if (tab.getPosition() == 0) {
                    fetchPhoto();
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView_saves.setVisibility(View.GONE);
                    privateText.setVisibility(View.GONE);
                    flag = false;
                } else if (tab.getPosition() == 1) {
                    if (isCurrentUser) {
                        fetchSavedPhotos();
                        recyclerView.setVisibility(View.GONE);
                        recyclerView_saves.setVisibility(View.VISIBLE);
                        privateText.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        recyclerView_saves.setVisibility(View.GONE);
                        privateText.setVisibility(View.VISIBLE);
                    }
                    flag = true;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = getResources().getColor(R.color.grey);
                if (tab.getIcon() != null) {
                    tab.getIcon().setTint(tabIconColor);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        TabLayout.Tab firstTab = tabLayout.getTabAt(0);
        TabLayout.Tab secondTab = tabLayout.getTabAt(1);
        if(!flag) {
            if (firstTab != null) {
                firstTab.select();
                fetchPhoto();
                if (firstTab.getIcon() != null) {
                    int tabIconColor = getResources().getColor(android.R.color.black);
                    firstTab.getIcon().setTint(tabIconColor);
                }
                flag = false;
            }
        }
        else {
            if(secondTab != null) {
                secondTab.select();
                if (secondTab.getIcon() != null) {
                    int tabIconColor = getResources().getColor(android.R.color.black);
                    secondTab.getIcon().setTint(tabIconColor);
                }
                if (!isCurrentUser) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerView_saves.setVisibility(View.GONE);
                    privateText.setVisibility(View.VISIBLE);
                }
                flag = true;
            }
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(view);
                navController.navigateUp();
            }
        });
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.profile_recycler_view_2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        recyclerView_saves = view.findViewById(R.id.profile_recycler_view_saves_2);
        recyclerView_saves.setHasFixedSize(true);
        recyclerView_saves.setLayoutManager(new GridLayoutManager(getContext(), 3));

        postList = new ArrayList<>();
        postThumbnailAdapter  = new Photo(getContext(), postList);
        recyclerView.setAdapter(postThumbnailAdapter);

        postList_saves = new ArrayList<>();
        postThumbnailAdapterSaves = new Photo(getContext(), postList_saves);
        recyclerView_saves.setAdapter(postThumbnailAdapterSaves);

        followBtn = view.findViewById(R.id.profile_edit_button_2);
        username = view.findViewById(R.id.profile_username_2);
        userPost = view.findViewById(R.id.profile_posts_count_2);
        userFollower = view.findViewById(R.id.profile_followers_count_2);
        userBio = view.findViewById(R.id.profile_bio_2);
        userFollowing = view.findViewById(R.id.profile_following_count_2);
        privateText = view.findViewById(R.id.private_saved_posts_text);
        followBtn.setOnClickListener(v -> toggleFollow());

        backBtn = view.findViewById(R.id.back_button);
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
        String currentUserId = currentUser.getUid();
         isCurrentUser = currentUserId.equals(profileId);
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
            countPosts();
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

                    // Log the post data being processed
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
    private void countPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences postPrefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                String profileId = postPrefs.getString("profileid", "none");
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        count++;
                    }
                }
                userPost.setText(String.valueOf(count));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void fetchSavedPhotos() {
        if (!isCurrentUser)
            return;
        else {
            DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference("Saves")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            savedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postList_saves.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String postId = ds.getKey();
                        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
                        postRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                Post post = postSnapshot.getValue(Post.class);
                                if (post != null) {
                                    postList_saves.add(post);
                                }
                                postThumbnailAdapterSaves.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("fetchSavedPhotos", "Failed to fetch saved posts: " + error.getMessage());
                }
            });
        }
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