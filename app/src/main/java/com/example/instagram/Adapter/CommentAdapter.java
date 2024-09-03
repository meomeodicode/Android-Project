package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.MainActivity;
import com.example.instagram.Model.Comment;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Comment comment = mComment.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.comment.setText(comment.getComment());
        getUserInfo(holder.imageProfile, holder.username, comment.getPublisher());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    username.setText(user.getUsername());
                    if (user.getImageUrl() != null) {
                        Glide.with(mContext).load(user.getImageUrl()).into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.ic_profile_filled);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
