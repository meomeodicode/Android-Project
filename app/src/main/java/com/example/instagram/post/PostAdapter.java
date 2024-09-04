package com.example.instagram.post;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.CommentActivity;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public Context mContext;
    private List<Post> postList;
    private  FirebaseUser firebaseUser;

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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = postList.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.postImageResource);
        //String localImageUrl = post.getPostImage();
        //Log.d("ImageURL", "1. Local URL: " + localImageUrl);

        TextView username = holder.itemView.findViewById(R.id.text_username);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the profile ID in SharedPreferences
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                // Get the NavController
                NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);

                // Determine the current destination and set popUpTo accordingly
                int currentDestinationId = navController.getCurrentDestination().getId();
                int popUpToId = (currentDestinationId == R.id.navigation_search) ? R.id.navigation_search : R.id.navigation_home;

                // Create NavOptions with setPopUpTo to avoid duplicate UserProfiles in the stack
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(popUpToId, true) // Clear up to the destination, excluding it
                        .setLaunchSingleTop(true) // Prevent multiple instances of the fragment
                        .build();

                // Navigate to the UserProfile fragment
                navController.navigate(R.id.navigation_search_user_profile, null, navOptions);
            }
        });
        if(post.getDescription() == null || post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        }
        else {
            holder.description.setVisibility((View.VISIBLE));
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.imageProfile, holder.username, holder.publisher, post.getPublisher());
        isLiked(post.getPostId(), holder.like);
        numLikes(holder.likes, post.getPostId());
        getComment(post.getPostId(), holder.comments);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostId());
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                    deleteNotifications(post.getPublisher(), post.getPostId());
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid",post.getPostId());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid",post.getPostId());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });
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
    private void publisherInfo(ImageView imageProfile, TextView username, TextView publisher, String UID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if(user.getImageurl() == null) {
                    imageProfile.setImageResource(R.drawable.ic_profile_filled);
                }
                else {
                    Glide.with(mContext).load(user.getImageurl()).into(imageProfile);
                }
                publisher.setText(user.getUsername());
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void addNotification(String userid, String postID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", firebaseUser.getUid());
        hashMap.put("description", "liked your post");
        hashMap.put("postId", postID);
        hashMap.put("isPost", true);
        reference.push().setValue(hashMap);
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}