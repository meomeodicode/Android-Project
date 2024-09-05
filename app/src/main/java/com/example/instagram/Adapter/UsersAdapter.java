package com.example.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.instagram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private Context mContext;
    private List<UserModel> Users;
    private FirebaseUser firebaseUser;
    private boolean isFragment;

    public UsersAdapter(Context context, List<UserModel> users, boolean isFragment) {
        mContext = context;
        Users = users;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        isFragment = isFragment;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView email;
        public CircleImageView imageProfile;

        public UserViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameInCard);
            email = itemView.findViewById(R.id.emailInCard);
            imageProfile = itemView.findViewById(R.id.image_profile);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }
    public void onBindViewHolder(@NonNull UserViewHolder userHolder, int position) {
        UserModel user = Users.get(userHolder.getAdapterPosition());
        userHolder.username.setText(user.getUsername());
        userHolder.email.setText(user.getEmail());
        if (user.getImageurl() != null && !user.getImageurl().isEmpty()) {
            Glide.with(userHolder.itemView.getContext())
                    .load(user.getImageurl())
                    .placeholder(R.drawable.ic_profile_filled)
                    .error(R.drawable.ic_profile_filled)
                    .circleCrop()
                    .into(userHolder.imageProfile);
        } else {
            userHolder.imageProfile.setImageResource(R.drawable.ic_profile_filled);
        }
        userHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();
                NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_searched_user);
            }
        });
    }
    @Override
    public int getItemCount() {
        return Users.size();
    }
}

    /*
    private void addNotification(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }*/
