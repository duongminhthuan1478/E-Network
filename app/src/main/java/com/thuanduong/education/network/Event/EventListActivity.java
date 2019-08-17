package com.thuanduong.education.network.Event;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Adapter.EventRecyclerViewAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.EventRecyclerViewHolder;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.Time;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity implements EventRecyclerViewAdapter.EventRecyclerViewAdapterInterface, View.OnClickListener {
    RecyclerView recyclerView;
    EventRecyclerViewAdapter eventRecyclerViewAdapter;
    ArrayList<Event> events = new ArrayList<>();
    FloatingActionButton fab;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        mAuth = FirebaseAuth.getInstance();
        viewSetup();
        recyclerViewSetup();
        getEvent();
    }
    void viewSetup(){
        fab = findViewById(R.id.event_list_fab);
        fab.setOnClickListener(this);
    }
    void recyclerViewSetup(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.event_list_recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(events,this);
        recyclerView.setAdapter(eventRecyclerViewAdapter);
    }
    void getEvent(){
        FirebaseDatabase.getInstance().getReference(Event.EVENT_REF).orderByChild(Event.END_TIME).startAt(Time.getCur()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                Log.d("data", dataSnapshot.toString());
                for(DataSnapshot eventSnapshot:dataSnapshot.getChildren()){
                    Event event = new Event(eventSnapshot);
                    switch(eventSnapshot.child(Event.EVENT_TYPE).getValue().toString()){
                        case CharitableEvent.eventType:
                            event = new CharitableEvent(eventSnapshot);
                            break;
                        case RegisterClassEvent.eventType:
                            event = new RegisterClassEvent(eventSnapshot);
                            break;
                        case SeminarEvent.eventType:
                            event = new SeminarEvent(eventSnapshot);
                            break;
                        case OtherEvent.eventType:
                            event = new OtherEvent(eventSnapshot);
                            break;
                    }
                    events.add(event);
                }
                eventRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBindViewHolder(EventRecyclerViewHolder h, ArrayList<Event> events, int position) {
        final Event event = events.get(position);
        h.name.setText(event.getEventName());
        h.title.setText(event.getEventTitle());
        h.description.setText(event.getEventContent());
        Picasso.get().load(event.getEventImage()).into(h.image);
        // limit = 0 mean infinity
        h.member.setText(event.partnerCount() + "/" + (event.getLimit() != 0 ? event.getLimit() : "∞"));
        h.startTime.setText(Time.timeRemaining(event.getStartTime()));
        h.accept_btn.setVisibility(View.VISIBLE);
        h.accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToEventDetailActivity(event.getId());
            }
        });
        if(event.isCreator( mAuth.getCurrentUser().getUid())){
            h.get_list_btn.setVisibility(View.VISIBLE);
            h.get_list_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendUserToListEventActivity(event.getId());
                }
            });
            h.edit_button.setVisibility(View.VISIBLE);
            h.edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            h.del_button.setVisibility(View.VISIBLE);
            h.del_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    event.remove();
                }
            });
        }else {
            h.get_list_btn.setVisibility(View.GONE);
            h.edit_button.setVisibility(View.GONE);
            h.del_button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event_list_fab:
                sendUserToCreateEventActivity();
                break;
        }
    }
    private void sendUserToCreateEventActivity(){
        Intent Intent = new Intent(this, CreateEventActivity.class);
        startActivity(Intent);
    }
    private void sendUserToEventDetailActivity(String eventId){
        Intent Intent = new Intent(this, EventDetailActivity.class);
        Intent.putExtra("eventId",eventId);
        startActivity(Intent);
    }
    private void sendUserToListEventActivity(String eventId){
        Intent Intent = new Intent(this, ListUserEventActivity.class);
        Intent.putExtra("eventId",eventId);
        startActivity(Intent);
    }
}
