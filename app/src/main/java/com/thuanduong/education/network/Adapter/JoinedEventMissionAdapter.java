package com.thuanduong.education.network.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thuanduong.education.network.Adapter.ViewHolder.JoinedEventMissionViewHolder;
import com.thuanduong.education.network.Event.EventMission;
import com.thuanduong.education.network.Model.ChatBotMess;
import com.thuanduong.education.network.R;

import java.util.ArrayList;


public class JoinedEventMissionAdapter extends RecyclerView.Adapter<JoinedEventMissionViewHolder>{
    ViewAdapterInterface ViewAdapterInterface;
    private ArrayList<EventMission> eventMissions ;
    public JoinedEventMissionAdapter(ArrayList<EventMission> eventMissions, ViewAdapterInterface ViewAdapterInterface) {
        this.eventMissions = eventMissions;
        this.ViewAdapterInterface = ViewAdapterInterface;
    }
    @Override
    public JoinedEventMissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new JoinedEventMissionViewHolder(inflater.inflate(R.layout.recyclerview_joined_event_mission_item, parent, false));
    }

    @Override
    public void onBindViewHolder(JoinedEventMissionViewHolder holder, int position) {
        ViewAdapterInterface.onBindViewHolder(holder,eventMissions,position);
    }

    @Override
    public int getItemCount() {
        return eventMissions.size();
    }
    public interface ViewAdapterInterface {
        void onBindViewHolder(JoinedEventMissionViewHolder holder, ArrayList<EventMission> eventMissions, int position);
    }
}