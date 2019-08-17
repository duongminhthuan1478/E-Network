package com.thuanduong.education.network.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thuanduong.education.network.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBotRecyclerViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView avt;
    public TextView name,msg;
    public ChatBotRecyclerViewHolder(View itemView) {
        super(itemView);
        name =itemView.findViewById(R.id.chat_item_name);
        msg = itemView.findViewById(R.id.chat_item_mess);
        avt = itemView.findViewById(R.id.chat_item_avt);
    }
}