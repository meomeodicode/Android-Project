package com.example.instagram;

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
import com.example.instagram.post.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePostDetailFragment extends Fragment {

    private String postId;
    private ImageView postImage;
    private TextView postContent, postUsername;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.item_post, container, false);
        postImage = view.findViewById(R.id.image_post);
        postContent = view.findViewById(R.id.text_description);
        postUsername = view.findViewById(R.id.publisher);
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
                    Glide.with(getContext()).load(post.getPostImage()).into(postImage);
                    postContent.setText(post.getDescription());
                    postUsername.setText(post.getPublisher());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
