package com.example.instagram.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.Noti;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.example.instagram.post.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotiViewHolder> {
    private Context mContext;
    private List<Noti> Notifications;

    public NotificationsAdapter(Context context, List<Noti> notification){
        mContext = context;
        Notifications = notification;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.noti_item, parent, false);
        return new NotificationsAdapter.NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.NotiViewHolder holder, int position) {
        Noti notification = Notifications.get(position);
        holder.content.setText(notification.getContent());
        getUser(holder.avatar, holder.username, notification.getUserID());
        if (notification.getIsPost()) {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostId());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateNextAction(notification);
            }
        });
    }

    private void navigateNextAction(Noti notification) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, false)
                .setLaunchSingleTop(true)
                .build();

        if (notification.getIsPost()) {
            editor.putString("postid", notification.getPostId());
            editor.apply();
            navController.navigate(R.id.navigation_post_detail, null, navOptions);
        } else {
            editor.putString("profileid", notification.getUserID());
            editor.apply();
            navController.navigate(R.id.navigation_profile, null, navOptions);
        }
    }
    @Override
    public int getItemCount() {
        return Notifications.size();
    }

    private void getPostImage(ImageView postImage, String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    Glide.with(mContext).load(post.getPostImage()).into(postImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void getUser(ImageView avatar, TextView username, String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null) {
                    Glide.with(mContext).load(user.getImageUrl()).into(avatar);
                    username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    public static class NotiViewHolder extends RecyclerView.ViewHolder {
        private TextView content, username;
        private ImageView avatar, postImage;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
            avatar = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}
