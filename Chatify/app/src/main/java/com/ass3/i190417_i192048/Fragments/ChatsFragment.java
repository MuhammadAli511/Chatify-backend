package com.ass3.i190417_i192048.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ass3.i190417_i192048.Adapters.UsersAdapter;
import com.ass3.i190417_i192048.ChatMainScreen;
import com.ass3.i190417_i192048.Models.Users;
import com.ass3.i190417_i192048.R;
import com.ass3.i190417_i192048.SignIn;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatsFragment extends Fragment {


    public ChatsFragment() {}

    List<Users> list = new ArrayList<>();
    UsersAdapter adapter;
    RecyclerView recyclerView;
    ImageView profilePicture;
    String imageURL;

    public void getData3(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String emailStr = sp.getString("email", null);
        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("email", emailStr).build();
        Request request = new Request.Builder().url("http://10.0.2.2:5000/getContacts").post(body).build();

        okhttpclient.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(getContext(), "Server Not Working", Toast.LENGTH_SHORT).show();
                Log.d("error", e.getMessage());
            }


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                        String userID = jsonArray1.getString(0);
                        String name = jsonArray1.getString(1);
                        String email = jsonArray1.getString(2);
                        String password = jsonArray1.getString(3);
                        String gender = jsonArray1.getString(4);
                        String phoneNum = jsonArray1.getString(5);
                        String imageURL = jsonArray1.getString(6);
                        String status = jsonArray1.getString(7);
                        String deviceID = jsonArray1.getString(8);
                        Users tempUser = new Users(name, email, password, imageURL, gender, phoneNum, userID, deviceID, status);
                        list.add(tempUser);
                    }
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String emailStr = sp.getString("email", null);

        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("email", emailStr).build();
        Request request = new Request.Builder().url("http://10.0.2.2:5000/getProfilePic").post(body).build();


        okhttpclient.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(getContext(), "Server Not Working", Toast.LENGTH_SHORT).show();
                Log.d("error", e.getMessage());
            }


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    imageURL = jsonObject.getString("profileUrl");
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("imageURL", imageURL);
                    editor.apply();
                    // run on ui thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // run when imageURL is not null
                            if (imageURL != null) {
                                profilePicture = getActivity().findViewById(R.id.profilePicture);
                                Glide.with(getContext()).load(imageURL).into(profilePicture);
                            }
                        }
                    });

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });







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