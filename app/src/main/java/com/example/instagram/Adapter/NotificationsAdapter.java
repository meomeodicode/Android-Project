package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.Noti;
import com.example.instagram.R;

import java.util.List;

public class NotificationsAdapter extends  RecyclerView.Adapter<NotificationsAdapter.NotiViewHolder> {
    private Context mContext;
    private List<Noti> Notifications;

    public NotificationsAdapter(Context context, List<Noti> notification){
        mContext = context;
        Notifications = notification;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_notifications, parent, false);
        return new NotiViewHolder(view);
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NotiViewHolder extends RecyclerView.ViewHolder{

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
