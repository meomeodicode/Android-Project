package com.example.instagram.post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public Context mContext;
    private List<Post> postList;

    public PostAdapter(Context mContext, List<Post> postList) {
        this.mContext = mContext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new PostAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.postImageResource);
        String localImageUrl = post.getPostImage();

        // Print to Logcat
        Log.d("ImageURL", "1. Local URL: " + localImageUrl);

        if(post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        }
        else {
            holder.description.setVisibility((View.VISIBLE));
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.imageProfile, holder.username, holder.publisher, post.getPublisher());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageProfile, postImageResource, like, comment, save;
        public TextView username, likes, publisher, description, comments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            postImageResource = itemView.findViewById(R.id.image_post);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.favorite);

            username = itemView.findViewById(R.id.text_username);
            description = itemView.findViewById(R.id.text_description);
            likes = itemView.findViewById(R.id.numLikes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.text_description);
            comments = itemView.findViewById((R.id.comments));
        }
    }
    private void publisherInfo(ImageView imageProfile, TextView username, TextView publisher, String UID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if(user.getImageUrl() == null) {
                    imageProfile.setImageResource(R.drawable.ic_profile);
                }
                else {
                    Glide.with(mContext).load(user.getImageUrl()).into(imageProfile);
                }
                publisher.setText(user.getUsername());
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}