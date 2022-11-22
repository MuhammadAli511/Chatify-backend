package com.ass3.i190417_i192048;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ass3.i190417_i192048.Adapters.MessageAdapter;
import com.ass3.i190417_i192048.Models.Messages;
import com.ass3.i190417_i192048.Models.Users;
import com.bumptech.glide.Glide;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    String senderId;
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
        senderId = prefs.getString("email", "");
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

        String senderRoom = senderId + receiverId;
        String receiverRoom = receiverId + senderId;
        senderId1 = senderId;
        receiverId1 = receiverId;
        final String[] senderName = {""};
        final String[] senderImage = {""};


        // Getting name and profile picture of sender
        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String emailStr = sp.getString("email", null);
        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("email", emailStr).build();
        Request request = new Request.Builder().url("http://10.0.2.2:5000/getNamePic").post(body).build();
        okhttpclient.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("error", e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    senderImage[0] = jsonObject.getString("profileUrl");
                    senderName[0] = jsonObject.getString("name");
                    // wait for 1 second with exception handling
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/

        // TODO: Check status of receiver


        getChats();



        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageToSend.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatDetailActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
                String formattedTime = df1.format(c);
                OkHttpClient okhttpclient = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("message", message)
                        .add("receiver", receiverId)
                        .add("sender", senderId)
                        .add("timestamp", formattedDate + " " + formattedTime)
                        .add("messageType", "Text")
                        .build();
                Request request = new Request.Builder().url("http://10.0.2.2:5000/sendMsg").post(body).build();
                okhttpclient.newCall(request).enqueue(new Callback() {
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("error", e.getMessage());
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String res = response.body().string();
                        if (res.contains("Sent")){
                            Log.d("success", "Message sent");
                            // TODO: Send Notification
                            // runonui thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageToSend.setText("");
                                    getChats();
                                }
                            });

                        }
                    }
                });


            }
        });


    }











    // TODO: onResume for Status



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55 && resultCode == RESULT_OK) {
            imageURISelecting = data.getData();
            ClipData mClipData = data.getClipData();
            if (imageURISelecting!=null){
                sendImage(imageURISelecting);
            } else {
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    sendImage(uri);
                }
            }
        }
    }



    public void getChats(){
        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody body1 = new FormBody.Builder().add("sender", senderId).add("receiver", receiverId).build();
        Request request1 = new Request.Builder().url("http://10.0.2.2:5000/getMsg").post(body1).build();
        okhttpclient.newCall(request1).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("error", e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    messagesList.clear();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                        String id = jsonArray1.getString(0);
                        String message = jsonArray1.getString(1);
                        String receiver = jsonArray1.getString(2);
                        String sender = jsonArray1.getString(3);
                        String timestamp = jsonArray1.getString(4);
                        String type = jsonArray1.getString(5);
                        Messages messages = new Messages(id, sender, receiver, message, type, timestamp);
                        messagesList.add(messages);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }









    // TODO: send image function
    public void sendImage(Uri val){
        Log.d("image", val.toString());
    }















}