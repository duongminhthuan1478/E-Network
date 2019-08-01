package com.thuanduong.education.network.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thuanduong.education.network.Adapter.ViewHolder.ChatBotRecyclerViewHolder;
import com.thuanduong.education.network.Model.ChatBotMess;
import com.thuanduong.education.network.R;

import java.util.ArrayList;


public class ChatBotRecyclerAdapter extends RecyclerView.Adapter<ChatBotRecyclerViewHolder>{
    ColorRecyclerViewAdapterInterface colorRecyclerViewAdapterInterface;
    private ArrayList<ChatBotMess> chatBotMesses ;
    public ChatBotRecyclerAdapter(ArrayList<ChatBotMess> chatBotMesses, ColorRecyclerViewAdapterInterface colorRecyclerViewAdapterInterface) {
        this.chatBotMesses = chatBotMesses;
        this.colorRecyclerViewAdapterInterface = colorRecyclerViewAdapterInterface;
    }
    @Override
    public int getItemViewType(int position) {
        return chatBotMesses.get(position).isBot() ? 0 : 1;
    }
    @Override
    public ChatBotRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0)
            return new ChatBotRecyclerViewHolder(inflater.inflate(R.layout.recyclerview_chatbot_item, parent, false));
        return new ChatBotRecyclerViewHolder(inflater.inflate(R.layout.recyclerview_chatbot_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatBotRecyclerViewHolder holder, int position) {
        colorRecyclerViewAdapterInterface.onBindViewHolder(holder,chatBotMesses,position);
    }

    @Override
    public int getItemCount() {
        return chatBotMesses.size();
    }
    public interface ColorRecyclerViewAdapterInterface {
        void onBindViewHolder(ChatBotRecyclerViewHolder holder, ArrayList<ChatBotMess> chatBotMesses, int position);
    }
}