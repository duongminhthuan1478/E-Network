package com.thuanduong.education.network.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thuanduong.education.network.R;

public class ParticipantsEventRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView name,phone,mssv,gender;
    public ParticipantsEventRecyclerViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.user_event_item_name);
        phone = itemView.findViewById(R.id.user_event_item_phone_number);
        mssv = itemView.findViewById(R.id.user_event_item_mssv);
        gender = itemView.findViewById(R.id.user_event_item_gender);
    }
}
