package com.thuanduong.education.network.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thuanduong.education.network.R;

public class EventRecyclerViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public ImageButton image;
    public TextView name,title,startTime,member,status,description;
    public Button accept_btn,get_list_btn,edit_button,del_button;
    public LinearLayout layout;
    public EventRecyclerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        name = mView.findViewById(R.id.name_event_list);
        title = mView.findViewById(R.id.title_event_list);
        startTime = mView.findViewById(R.id.event_start_time);
        member = mView.findViewById(R.id.member_event);
        status = mView.findViewById(R.id.join_event_status);
        description = mView.findViewById(R.id.description_event_textview);
        accept_btn = mView.findViewById(R.id.accept_button);
        get_list_btn = mView.findViewById(R.id.get_list_button);
        edit_button = mView.findViewById(R.id.edit_button);
        del_button = mView.findViewById(R.id.del_button);
        image = mView.findViewById(R.id.image_event_list);
        layout = mView.findViewById(R.id.event_layout);
    }
}