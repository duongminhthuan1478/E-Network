package com.thuanduong.education.network.Event.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Adapter.EventRecyclerViewAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.EventRecyclerViewHolder;
import com.thuanduong.education.network.Event.CreateCharitableActivity;
import com.thuanduong.education.network.Event.CreateClassRegisterActivity;
import com.thuanduong.education.network.Event.CreateEventActivity;
import com.thuanduong.education.network.Event.CreateOtherActivity;
import com.thuanduong.education.network.Event.CreateSeminarActivity;
import com.thuanduong.education.network.Event.EventDetailActivity;
import com.thuanduong.education.network.Event.ListUserEventActivity;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.Time;

import java.util.ArrayList;

public class EventListFragment extends Fragment implements EventRecyclerViewAdapter.EventRecyclerViewAdapterInterface {

    public EventListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try{
            context = getActivity();
        }catch (NullPointerException e){}
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }
    RecyclerView recyclerView;
    EventRecyclerViewAdapter eventRecyclerViewAdapter;
    ArrayList<Event> events = new ArrayList<>();
    FirebaseAuth mAuth;
    Context context;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        mAuth = FirebaseAuth.getInstance();
        recyclerViewSetup(view);
        getEvent();
    }
    void recyclerViewSetup(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.event_list_recyclerview);
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
        h.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToEventDetailActivity(event.getId());
            }
        });
    }

    private void sendUserToEventDetailActivity(String eventId){
        Intent Intent = new Intent(context, EventDetailActivity.class);
        Intent.putExtra("eventId",eventId);
        startActivity(Intent);
    }


}