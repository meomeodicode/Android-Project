package com.example.instagram.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instagram.Adapter.SearchUsersAdapter;
import com.example.instagram.Model.UserModel;
import com.example.instagram.R;
import com.example.instagram.databinding.FragmentSearchBinding;
import com.google.firebase.firestore.auth.User;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchUsersAdapter userAdapter;
    private List<UserModel> userList;
    private EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchBar = view.findViewById(R.id.search_bar);
        userList = new ArrayList<>();
        //userAdapter = new SearchUsersAdapter(getContext(), userList, true);
        recyclerView.setAdapter(userAdapter);
        readUsers();
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    private void searchUsers(String lowerCase) {
    }

    private void readUsers() {
        
    }
}