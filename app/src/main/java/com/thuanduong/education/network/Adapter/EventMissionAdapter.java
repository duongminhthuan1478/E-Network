package com.thuanduong.education.network.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thuanduong.education.network.Adapter.ViewHolder.MissionRecyclerViewHolder;
import com.thuanduong.education.network.Event.EventMission;
import com.thuanduong.education.network.R;

import java.util.ArrayList;

public class EventMissionAdapter  extends RecyclerView.Adapter<MissionRecyclerViewHolder>{
    EventMissionAdapter.OtherEventRecyclerViewAdapterInterface OtherEventRecyclerViewAdapterInterface;
    private ArrayList<EventMission> missions ;
    public EventMissionAdapter(ArrayList<EventMission> missions, EventMissionAdapter.OtherEventRecyclerViewAdapterInterface OtherEventRecyclerViewAdapterInterface) {
        this.missions = missions;
        this.OtherEventRecyclerViewAdapterInterface = OtherEventRecyclerViewAdapterInterface;
    }
    @Override
    public MissionRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MissionRecyclerViewHolder(inflater.inflate(R.layout.recyclerview_view_mission_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MissionRecyclerViewHolder holder, int position) {
        OtherEventRecyclerViewAdapterInterface.onBindViewHolder1(holder,missions,position);
    }

    @Override
    public int getItemCount() {
        return missions.size();
    }
    public interface OtherEventRecyclerViewAdapterInterface {
        void onBindViewHolder1(MissionRecyclerViewHolder holder, ArrayList<EventMission> missions, int position);
    }
}