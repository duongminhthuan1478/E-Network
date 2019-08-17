package com.thuanduong.education.network.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thuanduong.education.network.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MissionRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView nameTv,amountTv;
    public MissionRecyclerViewHolder(View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.mission_name_item);
        amountTv = itemView.findViewById(R.id.mission_name_amount);
    }
}