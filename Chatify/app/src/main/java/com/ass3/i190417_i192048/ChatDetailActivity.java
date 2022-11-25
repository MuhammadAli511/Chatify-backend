package com.ass3.i190417_i192048;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatDetailActivity extends AppCompatActivity {

    ImageView backButton, userImage, sendMsg, recordAudioButton;
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
    String token;
    MediaRecorder mediaRecorder;
    String pathSave;
    boolean isRecording = false;
    String[] senderName = new String[1];
    String[] senderImage = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        HandlerThread handlerThread = new HandlerThread("content_observer");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        String deviceID = getIntent().getStringExtra("deviceID");
        Log.d("deviceID", deviceID);
        // detect changes in the media store
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                try {
                    Log.d("ScreenShot", "ScreenShot detected");
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + "Screenshot detected" + "'}, 'include_player_ids': ['" + deviceID + "']}"), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        backButton = findViewById(R.id.backButton);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        sendMsg = findViewById(R.id.sendMsg);
        messageToSend = findViewById(R.id.messageToSend);
        status = findViewById(R.id.status);
        selectImageButton = findViewById(R.id.selectImageButton);
        recordAudioButton = findViewById(R.id.recordAudioButton);


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
        if(isMicrophonePresent())
            getMicrophonePermission();

        recordAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording){
                    stopRecording();
                    //Uri uri = Uri.fromFile(new File(pathSave));
                    try {
                        sendAudio(pathSave);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    startRecording();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 55);
            }
        });

        String senderRoom = senderId + receiverId;
        String receiverRoom = receiverId + senderId;
        senderId1 = senderId;
        receiverId1 = receiverId;
        senderName = new String[]{""};
        senderImage = new String[]{""};

        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("email", receiverId).build();
        Request request = new Request.Builder().url("http://10.0.2.2:5000/getStatus").post(body).build();
        okhttpclient.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("error", e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String status1 = jsonObject.getString("userStatus");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText(status1);
                        }
                    });
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


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


                        try {
                            Log.d("response", res);
                            JSONObject jsonObject = new JSONObject(res);
                            token = jsonObject.getString("deviceID");
                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"'}, 'include_player_ids': ['" + token + "'], 'data': {'senderId': '"+senderId+"', 'senderName': '"+senderName[0]+"' , 'senderImage': '"+senderImage[0]+"' }}"),null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageToSend.setText("");
                                getChats();
                            }
                        });


                    }
                });


            }
        });
    }

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

    public void sendImage(Uri val){
        MediaManager.get().upload(val).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {}
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override
            public void onSuccess(String requestId, Map resultData) {
                String imageURL = resultData.get("url").toString();
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
                String formattedTime = df1.format(c);
                OkHttpClient okhttpclient = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("message", imageURL)
                        .add("receiver", receiverId)
                        .add("sender", senderId)
                        .add("timestamp", formattedDate + " " + formattedTime)
                        .add("messageType", "Image")
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

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                token = jsonObject.getString("deviceID");
                                OneSignal.postNotification(new JSONObject("{'contents': {'en':'Image'}, 'include_player_ids': ['" + token + "'], 'data': {'senderId': '"+senderId+"', 'senderName': '"+senderName[0]+"' , 'senderImage': '"+senderImage[0]+"' }}"),null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getChats();
                                }
                            });

                        }
                    }
                });

            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                Toast.makeText(ChatDetailActivity.this, "Image upload failed", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    boolean isMicrophonePresent(){
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO},100);
        }
    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory,System.currentTimeMillis() + ".mp3");
        return file.getPath();
    }

    void startRecording() {
        isRecording = true;
        pathSave = getRecordingFilePath();
        Log.d("pathSave", pathSave);
        setupMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(pathSave);
    }

    void stopRecording() {
        isRecording = false;
        mediaRecorder.stop();
    }



    public void sendAudio(String url) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(url));
        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();
        byte[] audioBytes = out.toByteArray();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final String audioData= Base64.getEncoder().encodeToString(audioBytes);
            Log.d("audioData", audioData + " Hello");
        }

    }

}