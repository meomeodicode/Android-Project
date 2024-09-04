package com.example.instagram.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.instagram.Model.Noti;
import com.example.instagram.R;
import com.example.instagram.post.Post;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
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
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPost(post,context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }
    private void getPost(Post post, Context mContext) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, false)
                .setLaunchSingleTop(true)
                .build();

        if (post != null && post.getPostId() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("postid", post.getPostId());
            navController.navigate(R.id.navigation_post_detail, bundle, navOptions);
        }
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;
        public ImageViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}
