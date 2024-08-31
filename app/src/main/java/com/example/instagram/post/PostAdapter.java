package com.example.instagram.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instagram.R;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.username.setText(post.getUsername());
        holder.description.setText(post.getDescription());
        holder.profileImage.setImageResource(post.getProfileImageResource());
        holder.postImage.setImageResource(post.getPostImageResource());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, description;
        ImageView profileImage, postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.text_username);
            description = itemView.findViewById(R.id.text_description);
            profileImage = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.image_post);
        }
    }
}
