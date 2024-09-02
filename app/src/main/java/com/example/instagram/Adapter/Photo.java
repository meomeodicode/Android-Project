package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.instagram.R;
import com.example.instagram.post.Post;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.time.Instant;
import java.util.List;
import android.util.Log;

public class Photo extends RecyclerView.Adapter<Photo.ImageViewHolder> {

    private Context context;
    private List<Post> postLists;
    private Post mPosts;

    public Photo(Context context, List<Post> posts) {
        this.context = context;
        this.postLists = posts;
    }

    @NonNull
    @Override
    public Photo.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_grid_item, parent, false);
        return new Photo.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Post post = postLists.get(position);
        Glide.with(context).load(post.getPostImage()).into(holder.postImage);
        //Post post
        /*int imagePos = position * 3;
        int numItems = imageResources.size();
        int remainder = numItems % 3;
        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);
        holder.image1.setImageResource(imageResources.get(imagePos));
        holder.image2.setImageResource(imageResources.get(imagePos + 1));
        holder.image3.setImageResource(imageResources.get(imagePos + 2));
        holder.*/
    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;
        public ImageViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}
