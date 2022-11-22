package com.ass3.i190417_i192048.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ass3.i190417_i192048.ChatDetailActivity;
import com.ass3.i190417_i192048.ChatMainScreen;
import com.ass3.i190417_i192048.Models.Users;
import com.ass3.i190417_i192048.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    List <Users> usersList;
    Context context;

    public UsersAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_user_row, parent, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        Users users = usersList.get(position);
        holder.userName.setText(users.getName());
        Glide.with(context).load(usersList.get(pos).getProfileURL()).into(holder.userImage);

        // TODO: Last message
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String senderEmail = sp.getString("email", null);
        String receiverEmail = users.getEmail();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("sender", senderEmail).add("receiver", receiverEmail).build();
        Request request = new Request.Builder().url("http://10.0.2.2:5000/getLastMsg").post(body).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("error", e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String lastMsg = jsonObject.getString("message");
                    String type = jsonObject.getString("messageType");
                    ((ChatMainScreen)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type.equals("Text")) {
                                holder.lastMessage.setText(lastMsg);
                            } else {
                                holder.lastMessage.setText("Image");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userID", users.getEmail());
                intent.putExtra("userName", users.getName());
                intent.putExtra("profileURL", users.getProfileURL());
                context.startActivity(intent);
            }
        });

    }








    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName, lastMessage;
        LinearLayout userLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userLayout = itemView.findViewById(R.id.chat_user_row);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);

        }
    }





}
