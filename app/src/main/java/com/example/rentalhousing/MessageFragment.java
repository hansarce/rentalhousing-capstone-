package com.example.rentalhousing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageFragment extends Fragment {

    private EditText searchinput;
    private RecyclerView recyclerViewsearch;

    public MessageFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        /*searchinput = view.findViewById(R.id.searchbarinput);
        recyclerViewsearch = view.findViewById(R.id.recyclerviewchat);


        recyclerViewsearch.setLayoutManager(new LinearLayoutManager(getContext()));


        searchinput.setOnClickListener(v -> {
            String searchTerm = searchinput.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchinput.setError("Invalid Username");

            }

            setupSearchRecyclerView(searchTerm);
        });*/


        return view;
    }


  // private void setupSearchRecyclerView()

}
