package com.thuanduong.education.network.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thuanduong.education.network.Adapter.ViewHolder.eventImageRecyclerViewHolder;
import com.thuanduong.education.network.R;

import java.util.ArrayList;

public class EventImageAdapter extends RecyclerView.Adapter<eventImageRecyclerViewHolder>{
    OtherEventRecyclerViewAdapterInterface OtherEventRecyclerViewAdapterInterface;
    private ArrayList<String> imgs ;
    public EventImageAdapter(ArrayList<String> imgs, OtherEventRecyclerViewAdapterInterface OtherEventRecyclerViewAdapterInterface) {
        this.imgs = imgs;
        this.OtherEventRecyclerViewAdapterInterface = OtherEventRecyclerViewAdapterInterface;
    }
    @Override
    public eventImageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new eventImageRecyclerViewHolder(inflater.inflate(R.layout.recyclerview_create_event_image_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(eventImageRecyclerViewHolder holder, int position) {
        OtherEventRecyclerViewAdapterInterface.onBindViewHolder(holder,imgs,position);
    }

    @Override
    public int getItemCount() {
        return imgs.size() + 1;
    }
    public interface OtherEventRecyclerViewAdapterInterface {
        void onBindViewHolder(eventImageRecyclerViewHolder holder, ArrayList<String> imgs, int position);
    }
}
