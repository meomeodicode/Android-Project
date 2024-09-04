package com.example.instagram;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.UserModel;
import com.example.instagram.post.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePostDetailFragment extends Fragment {

    private String postId;
    private ImageView postImage;
    private TextView postContent, postUsername, postUsernameTop;
    private ImageView imageProfile; // Added to show publisher's profile image
    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.item_post, container, false);

        mContext = getContext();
        postImage = view.findViewById(R.id.image_post);
        postContent = view.findViewById(R.id.text_description);
        postUsername = view.findViewById(R.id.publisher);
        postUsernameTop = view.findViewById(R.id.text_username);
        imageProfile = view.findViewById(R.id.image_profile); // Initialize profile image view

        if (getArguments() != null) {
            postId = getArguments().getString("postid");
        }
        readPost();
        return view;
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    Glide.with(mContext).load(post.getPostImage()).into(postImage);
                    postContent.setText(post.getDescription());
                    String publisherId = post.getPublisher();
                    publisherInfo(imageProfile, postUsername, postUsernameTop, publisherId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void publisherInfo(ImageView imageProfile, TextView username, TextView postUsernameTop, String UID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    if (user.getImageurl() == null) {
                        imageProfile.setImageResource(R.drawable.ic_profile_filled);
                    } else {
                        Glide.with(mContext).load(user.getImageurl()).into(imageProfile);
                    }
                    username.setText(user.getUsername());
                    postUsernameTop.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
