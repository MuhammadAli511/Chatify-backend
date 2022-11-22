package com.ass3.i190417_i192048.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ass3.i190417_i192048.ChatDetailActivity;
import com.ass3.i190417_i192048.Models.Users;
import com.ass3.i190417_i192048.R;
import com.bumptech.glide.Glide;

import java.util.List;

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
