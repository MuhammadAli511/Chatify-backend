package com.ass3.i190417_i192048.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ass3.i190417_i192048.ChatMainScreen;
import com.ass3.i190417_i192048.R;
import com.ass3.i190417_i192048.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NewContactFragment extends Fragment {

    public NewContactFragment() {}

    EditText phoneNum;
    Button addContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        phoneNum = view.findViewById(R.id.phoneNumOne);
        addContact = view.findViewById(R.id.addContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String emailStr = prefs.getString("email", "");
                String phoneNumStr = phoneNum.getText().toString();

                OkHttpClient okhttpclient = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("email", emailStr)
                        .add("phoneNum", phoneNumStr)
                        .build();

                Request request = new Request.Builder().url("http://10.0.2.2:5000/addContact").post(body).build();
                okhttpclient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("Error", e.getMessage());
                        // toast on ui thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseStr = response.body().string();
                        if (responseStr.contains("Contact Added")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    phoneNum.setText("");
                                    Toast.makeText(getContext(), "Contact Added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (responseStr.contains("Contact Already Exists")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Contact Already Exists", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Invalid Contact Number", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_contact, container, false);
    }

}