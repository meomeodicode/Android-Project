package com.example.instagram;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePostDetailFragment extends Fragment {

    private  FirebaseUser firebaseUser;
    private String postId;
    public ImageView imageProfile, postImageResource, like, comment, save;
    public TextView username, likes, publisher, description, comments, timestamp;
    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.item_post, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mContext = getContext();
        postImageResource= view.findViewById(R.id.image_post);
        description = view.findViewById(R.id.text_description);
        publisher = view.findViewById(R.id.publisher);
        username = view.findViewById(R.id.text_username);
        imageProfile = view.findViewById(R.id.image_profile); // Initialize profile image view
        like = view.findViewById(R.id.like);
        comment = view.findViewById(R.id.comment);
        save = view.findViewById(R.id.favorite);
        comments = view.findViewById(R.id.comments);
        timestamp = view.findViewById(R.id.timestamp);
        likes = view.findViewById(R.id.numLikes);
        if (getArguments() != null) {
            postId = getArguments().getString("postid");
        }
        readPost();
        isLiked(postId, like);
        numLikes(likes, postId);
        getComment(postId, comments);
        isSaved(postId, save);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(postId)
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(postId).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(postId).removeValue();
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid",postId);
                intent.putExtra("publisherid", postId);
                mContext.startActivity(intent);
            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid",postId);
                intent.putExtra("publisherid", postId);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    Glide.with(mContext).load(post.getPostImage()).into(postImageResource);
                    description.setText(post.getDescription());
                    if (post.getTimestamp() != 0) {
                        timestamp.setText(getTimeAgo(post.getTimestamp()));
                    } else {
                        timestamp.setText("Just now");
                    }
                    String publisherId = post.getPublisher();
                    publisherInfo(imageProfile, publisher, username, publisherId);
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
                    if (user.getImageurl() == null || user.getImageurl().isEmpty()) {
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
    private String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60 * 1000) {
            return "just now";
        } else if (diff < 2 * 60 * 1000) {
            return "a minute ago";
        } else if (diff < 60 * 60 * 1000) {
            return (diff / (60 * 1000)) + " minutes ago";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return (diff / (60 * 60 * 1000)) + " hours ago";
        } else {
            return (diff / (24 * 60 * 60 * 1000)) + " days ago";
        }
    }
    private void isSaved(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_favorite_filled);
                    imageView.setTag("saved");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("save");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void numLikes(TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void isLiked(String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_red);
                    imageView.setTag("liked");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void getComment(String postId, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("View All "+snapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}
