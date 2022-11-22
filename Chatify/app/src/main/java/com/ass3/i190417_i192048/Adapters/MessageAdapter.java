package com.ass3.i190417_i192048.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ass3.i190417_i192048.Models.Messages;
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

public class MessageAdapter  extends RecyclerView.Adapter{
    List<Messages> messagesList;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public MessageAdapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = View.inflate(context, R.layout.sender_msg_row, null);
            return new SenderViewHolder(view);
        } else {
            View view = View.inflate(context, R.layout.receiver_msg_row, null);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String emailStr = sp.getString("email", null);
        if (messagesList.get(position).getSender().equals(emailStr)) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);
        Log.d("Message Type", holder.getClass().toString());
        if (holder.getClass() == SenderViewHolder.class) {
            if (messages.getMessageType().equals("Image")){
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderImageMsg.setVisibility(View.VISIBLE);
                Glide.with(context).load(messages.getMessage()).into(((SenderViewHolder) holder).senderImageMsg);
            } else if (messages.getMessageType().equals("Text")){
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderImageMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setText(messages.getMessage());
            }

            ((SenderViewHolder) holder).senderTime.setText(messages.getTimestamp().toString());
            ((SenderViewHolder) holder).senderImage.setImageResource(R.drawable.person);

            ((SenderViewHolder) holder).msgParentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String id = messages.getMessageID();
                    OkHttpClient okhttpclient = new OkHttpClient();
                    RequestBody body1 = new FormBody.Builder().add("id", id).build();
                    Request request1 = new Request.Builder().url("http://10.0.2.2:5000/deleteMsg").post(body1).build();
                    okhttpclient.newCall(request1).enqueue(new Callback() {
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d("error", e.getMessage());
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            // remove from list
                            messagesList.remove(position);
                            // notify adapter
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    return true;
                }
            });
        } else {
            if (messages.getMessageType().equals("Image")){
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).receiverImageMsg.setVisibility(View.VISIBLE);
                Glide.with(context).load(messages.getMessage()).into(((ReceiverViewHolder) holder).receiverImageMsg);
            } else if (messages.getMessageType().equals("Text")) {
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverImageMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).receiverMsg.setText(messages.getMessage());
            }

            ((ReceiverViewHolder) holder).receiverTime.setText(messages.getTimestamp().toString());
            ((ReceiverViewHolder) holder).receiverImage.setImageResource(R.drawable.ic_baseline_person_outline_24);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg, receiverTime;
        ImageView receiverImage, receiverImageMsg;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverMsg);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receiverImage = itemView.findViewById(R.id.receiverImage);
            receiverImageMsg = itemView.findViewById(R.id.receiverImageMsg);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg, senderTime;
        ImageView senderImage, senderImageMsg;
        RelativeLayout msgParentLayout;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderMsg);
            senderTime = itemView.findViewById(R.id.senderTime);
            senderImage = itemView.findViewById(R.id.senderImage);
            msgParentLayout = itemView.findViewById(R.id.msgParentLayout);
            senderImageMsg = itemView.findViewById(R.id.senderImageMsg);
        }
    }

}
