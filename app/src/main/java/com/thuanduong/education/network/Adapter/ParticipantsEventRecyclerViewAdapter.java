package com.thuanduong.education.network.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thuanduong.education.network.Adapter.ViewHolder.ParticipantsEventRecyclerViewHolder;
import com.thuanduong.education.network.Model.ParticipantsUser;
import com.thuanduong.education.network.Model.ParticipantsUser;
import com.thuanduong.education.network.R;

import java.util.ArrayList;



public class ParticipantsEventRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantsEventRecyclerViewHolder>{
    ParticipantsEventRecyclerViewAdapterInterface participantsEventRecyclerViewAdapterInterface;
    private ArrayList<ParticipantsUser> ParticipantsUsers ;
    public ParticipantsEventRecyclerViewAdapter(ArrayList<ParticipantsUser> ParticipantsUsers, ParticipantsEventRecyclerViewAdapterInterface participantsEventRecyclerViewAdapterInterface) {
        this.ParticipantsUsers = ParticipantsUsers;
        this.participantsEventRecyclerViewAdapterInterface = participantsEventRecyclerViewAdapterInterface;
    }
    @Override
    public int getItemViewType(int position) {
        return position ;
    }
    @Override
    public ParticipantsEventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ParticipantsEventRecyclerViewHolder(inflater.inflate(R.layout.recyclerview_list_user_event_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ParticipantsEventRecyclerViewHolder holder, int position) {
        participantsEventRecyclerViewAdapterInterface.onBindViewHolder(holder,ParticipantsUsers,position);
    }

    @Override
    public int getItemCount() {
        return ParticipantsUsers.size();
    }
    public interface ParticipantsEventRecyclerViewAdapterInterface {
        void onBindViewHolder(ParticipantsEventRecyclerViewHolder holder, ArrayList<ParticipantsUser> ParticipantsUsers, int position);
    }
}