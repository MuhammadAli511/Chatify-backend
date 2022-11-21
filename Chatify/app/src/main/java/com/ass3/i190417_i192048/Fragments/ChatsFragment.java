package com.ass3.i190417_i192048.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ass3.i190417_i192048.Adapters.UsersAdapter;
import com.ass3.i190417_i192048.Models.Users;
import com.ass3.i190417_i192048.R;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {


    public ChatsFragment() {}

    List<Users> list = new ArrayList<>();
    UsersAdapter adapter;
    RecyclerView recyclerView;

    public void getData3(){
        List<String> contactsList = new ArrayList<>();
        // TODO : Get contacts list from flask server


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsersAdapter(list, getContext());
        recyclerView.setAdapter(adapter);

        // clear list
        list.clear();
        getData3();
        return view;
    }


}