package com.thuanduong.education.network.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thuanduong.education.network.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class eventImageRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView img;
    public eventImageRecyclerViewHolder(View itemView) {
        super(itemView);
        img =itemView.findViewById(R.id.create_image_recycler_view);
    }
}