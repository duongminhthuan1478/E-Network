package com.thuanduong.education.network.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thuanduong.education.network.Adapter.ViewHolder.EventRecyclerViewHolder;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.R;

import java.util.ArrayList;


public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewHolder>{
    EventRecyclerViewAdapterInterface EventRecyclerViewAdapterInterface;
    private ArrayList<Event> events ;
    public EventRecyclerViewAdapter(ArrayList<Event> events, EventRecyclerViewAdapterInterface EventRecyclerViewAdapterInterface) {
        this.events = events;
        this.EventRecyclerViewAdapterInterface = EventRecyclerViewAdapterInterface;
    }
    @Override
    public int getItemViewType(int position) {
        return position ;
    }
    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new EventRecyclerViewHolder(inflater.inflate(R.layout.recyclerview_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(EventRecyclerViewHolder holder, int position) {
        EventRecyclerViewAdapterInterface.onBindViewHolder(holder,events,position);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
    public interface EventRecyclerViewAdapterInterface {
        void onBindViewHolder(EventRecyclerViewHolder holder, ArrayList<Event> events, int position);
    }
}