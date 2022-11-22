package com.ass3.i190417_i192048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ass3.i190417_i192048.Adapters.MessageAdapter;
import com.ass3.i190417_i192048.Models.Messages;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {

    ImageView backButton, userImage, sendMsg;
    TextView userName, status;
    EditText messageToSend;
    RecyclerView chatDetailRecyclerView;
    List<Messages> messagesList;
    MessageAdapter messageAdapter;
    String senderId1;
    String receiverId1;
    ImageView selectImageButton;
    Uri imageURISelecting;
    String receiverId;
    String receiverName;
    String receiverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        backButton = findViewById(R.id.backButton);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        sendMsg = findViewById(R.id.sendMsg);
        messageToSend = findViewById(R.id.messageToSend);
        status = findViewById(R.id.status);
        selectImageButton = findViewById(R.id.selectImageButton);


        chatDetailRecyclerView = findViewById(R.id.chatDetailRecyclerView);
        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList, this);
        chatDetailRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatDetailRecyclerView.setLayoutManager(layoutManager);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String senderId = prefs.getString("email", "");
        receiverId = getIntent().getStringExtra("userID");
        receiverName = getIntent().getStringExtra("userName");
        receiverImage = getIntent().getStringExtra("profileURL");



        userName.setText(receiverName);
        Glide.with(this).load(receiverImage).into(userImage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });










    }
}