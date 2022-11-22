package com.ass3.i190417_i192048.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import java.util.List;

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
        if (messagesList.get(position).getUserID().equals(emailStr)) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            if (messages.getMsgType().equals("Image")){
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderImageMsg.setVisibility(View.VISIBLE);
                Glide.with(context).load(messages.getMessage()).into(((SenderViewHolder) holder).senderImageMsg);
            } else if (messages.getMsgType().equals("Text")){
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderImageMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setText(messages.getMessage());
            }

            ((SenderViewHolder) holder).senderTime.setText(messages.getTimestamp().toString());
            ((SenderViewHolder) holder).senderImage.setImageResource(R.drawable.person);




            ((SenderViewHolder) holder).msgParentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO: Delete message
                    return true;
                }
            });
        } else {
            if (messages.getMsgType().equals("Image")){
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).receiverImageMsg.setVisibility(View.VISIBLE);
                Glide.with(context).load(messages.getMessage()).into(((ReceiverViewHolder) holder).receiverImageMsg);
            } else if (messages.getMsgType().equals("Text")) {
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
