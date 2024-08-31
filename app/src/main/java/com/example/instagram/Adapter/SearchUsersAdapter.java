package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.UserModel;
import com.example.instagram.R;

import java.util.List;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.SearchUsersViewHolder> {
    private List<UserModel> users;
    private OnUserClickListener onUserClickListener;

    public SearchUsersAdapter(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        //View view = LayoutInflater.from(context).inflate(R.layout.search_viewholder, parent, false);
        return new SearchUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUsersViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class SearchUsersViewHolder extends RecyclerView.ViewHolder {
        // Declare your views here
        TextView userName, userFullName;
        ImageView avatar;
        public SearchUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            //userName = itemView.findViewById(R.id.userName);
            //userFullName = itemView.findViewById(R.id.userEmail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserClickListener != null) {
                        onUserClickListener.onUserClick(getAdapterPosition());
                    }
                }
            });
        }
        public void bind(UserModel user) {
            userName.setText(user.getUsername());
            userFullName.setText(user.getFullname());
        }
    }
    public interface OnUserClickListener {
        void onUserClick(int position);
    }
}
