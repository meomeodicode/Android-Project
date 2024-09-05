package com.example.instagram.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.Photo;
import com.example.instagram.FollowListActivity;
import com.example.instagram.ForgotPasswordActivity;
import com.example.instagram.LoginActivity;
import com.example.instagram.MainActivity;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.example.instagram.post.Post;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
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

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView, recyclerView_saves;
    private Button editProfile;
    private String profileid;
    private ImageButton menuBtn, shareLink;
    private TextView userPost, userFollowing, userFollower, userBio, username;
    private ShapeableImageView avatar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserModel displayedUser;
    private DatabaseReference userRef, followRef;
    private List<Post> postList;
    private List<Post> postList_saves;
    private Photo postThumbnailAdapter, postThumbnailAdapterSaves;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        updateProfileIdInPrefs(currentUserId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(view);
        setupFirebase();
        loadUserData();
        initEdit();
        TabLayout tabLayout = view.findViewById(R.id.profile_tab_layout);
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
                    flag = false;
                } else if (tab.getPosition() == 1) {
                    fetchSavedPhotos();
                    recyclerView.setVisibility(View.GONE);
                    recyclerView_saves.setVisibility(View.VISIBLE);
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
        userFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowingList();
            }
        });
        userFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowersList();
            }
        });
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetMenu();
            }

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
                flag = true;
            }
        }
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_saves = view.findViewById(R.id.profile_recycler_view_saves);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        recyclerView_saves = view.findViewById(R.id.profile_recycler_view_saves);
        recyclerView_saves.setHasFixedSize(true);
        recyclerView_saves.setLayoutManager(new GridLayoutManager(getContext(), 3));

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        avatar = view.findViewById(R.id.profile_avatar);
        username = view.findViewById(R.id.profile_username);
        userPost = view.findViewById(R.id.profile_posts_count);
        userFollower = view.findViewById(R.id.profile_followers_count);
        userFollowing = view.findViewById(R.id.profile_following_count);
        menuBtn = view.findViewById(R.id.profile_menu_button);
        shareLink = view.findViewById(R.id.profile_share_link);
        userBio = view.findViewById(R.id.profile_bio);
        editProfile = view.findViewById(R.id.profile_edit_button);
        editProfile.setOnClickListener(v -> initEdit());
        postList = new ArrayList<>();
        postList_saves = new ArrayList<>();
        postThumbnailAdapterSaves = new Photo(getContext(), postList_saves);
        postThumbnailAdapter = new Photo(getContext(), postList);
        recyclerView.setAdapter(postThumbnailAdapter);
        recyclerView_saves.setAdapter(postThumbnailAdapterSaves);
    }

    private void initEdit() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        followRef = FirebaseDatabase.getInstance().getReference().child("Follow");
    }

    private void updateProfileIdInPrefs(String newProfileId) {
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profileid", newProfileId);
        editor.apply();
    }

    private void openBottomSheetMenu() {
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.profile_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        Button changePass = bottomSheetView.findViewById(R.id.btn_change_password);
        Button logout = bottomSheetView.findViewById(R.id.btn_logout);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                getActivity().finish();
                bottomSheetDialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void loadUserData() {
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String profileId = prefs.getString("profileid", "none");
        Log.d("loadUserData", "Retrieved profileId from SharedPreferences: " + profileId);
        if (!profileId.equals("none")) {
            userRef.child(profileId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("loadUserData", "DataSnapshot exists for profileId: " + profileId);
                        displayedUser = dataSnapshot.getValue(UserModel.class);
                        if (displayedUser != null) {
                            updateUI();
                        }
                        else {
                            Log.e("loadUserData", "UserModel is null for profileId: " + profileId);
                        }
                    } else {
                        Log.e("loadUserData", "No data found for profileId: " + profileId);
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("loadUserData", "Failed to load user data: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("loadUserData", "No profile ID found in SharedPreferences");
            Toast.makeText(getContext(), "No profile ID found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        username.setText(displayedUser.getUsername() != null ? displayedUser.getUsername() : "No username");
        userBio.setText(displayedUser.getBio() != null ? displayedUser.getBio() : "No bio available");
        Glide.with(getContext()).load(displayedUser.getImageurl()).into(avatar);
        if (displayedUser != null & displayedUser.getId() != null) {
            fetchFollowingCount();
            fetchFollowerCount();
            countPosts();
        }
    }

    private void getFollowersList() {
        Intent intent = new Intent(getContext(), FollowListActivity.class);
        intent.putExtra("id", profileid);
        intent.putExtra("title", "followers");
        startActivity(intent);
    }
    private void getFollowingList() {
        Intent intent = new Intent(getContext(), FollowListActivity.class);
        intent.putExtra("id", profileid);
        intent.putExtra("title", "following");
        startActivity(intent);
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

    private void fetchSavedPhotos() {
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
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchSavedPhotos", "Failed to fetch saved posts: " + error.getMessage());
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

}