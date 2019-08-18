package com.thuanduong.education.network.Event;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thuanduong.education.network.Adapter.ParticipantsEventRecyclerViewAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.ParticipantsEventRecyclerViewHolder;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.ParticipantsUser;
import com.thuanduong.education.network.R;

import java.util.ArrayList;

public class ListUserEventActivity extends AppCompatActivity implements ParticipantsEventRecyclerViewAdapter.ParticipantsEventRecyclerViewAdapterInterface {

    private RecyclerView recyclerView;
    ParticipantsEventRecyclerViewAdapter participantsEventRecyclerViewAdapter;
    ArrayList<ParticipantsUser> participantsUsers = new ArrayList<>();

    //
    String eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_event);
        recyclerViewSetup();
        getIntentData();
        getData();
    }
    void recyclerViewSetup(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.user_of_event_list_recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        participantsEventRecyclerViewAdapter = new ParticipantsEventRecyclerViewAdapter(participantsUsers,this);
        recyclerView.setAdapter(participantsEventRecyclerViewAdapter);
    }
    void getIntentData(){
        Intent intent = getIntent();
        if(intent.hasExtra("eventId"))
            eventId = intent.getStringExtra("eventId");
        else finish();
    }
    void getData(){
        FirebaseDatabase
                .getInstance()
                .getReference(Event.EVENT_REF)
                .child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {
                        participantsUsers.clear();
                        Event event = new Event(dataSnapshot);
                        for(ParticipantsUser participantsUser : event.getParticipantsUser()){
                            participantsUsers.add(participantsUser);
                        }
                        participantsEventRecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public void onBindViewHolder(ParticipantsEventRecyclerViewHolder holder, ArrayList<ParticipantsUser> participantsUsers, int position) {
        ParticipantsUser participantsUser = participantsUsers.get(position);
        holder.name.setText(participantsUser.getName());
        holder.phone.setText(participantsUser.getSdt());
        holder.mssv.setText(participantsUser.getMssv());
        if(participantsUser.getIsMale())
            holder.gender.setText("male");
        else    holder.gender.setText("female");
        //holder.note.setText(participantsUser.getMission());
    }
}
