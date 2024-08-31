// HomeFragment.java
package com.example.instagram.ui.home;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.MainActivity;
import com.example.instagram.post.Post;
import com.example.instagram.post.PostAdapter;
import com.example.instagram.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton homeButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recycler_view_posts);
        homeButton = root.findViewById(R.id.home_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getParentFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
                    }
                }, 500);
            }
        });


        List<Post> postList = new ArrayList<>();
        postList.add(new Post("user1", "First post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));
        postList.add(new Post("user2", "Second post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));
        postList.add(new Post("user2", "Second post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));
        postList.add(new Post("user2", "Second post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));
        postList.add(new Post("user2", "Second post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));
        postList.add(new Post("user2", "Second post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));
        postList.add(new Post("user2", "Second post description", R.drawable.ic_profile, R.drawable.ic_instagram_logo));

        // Setup RecyclerView
        PostAdapter adapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }
}
