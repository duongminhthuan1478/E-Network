package com.thuanduong.education.network.Event;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.Time;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView img;
    TextView eventName,eventClassId,eventBeg,eventDetail,eventOrg,eventSpeaker,eventAudience,eventContent,eventSchedule,eventAddress,eventMin,eventLimit,eventJoined,eventStart,eventEnd,eventMission;
    LinearLayout eventClassIdLayout,eventBegLayout,eventDetailLayout,eventOrgLayout,eventSpeakerLayout,eventAudienceLayout,eventContentLayout,eventScheduleLayout,eventAddressLayout,eventMinLayout,eventLimitLayout,eventJoinedLayout,eventStartLayout,eventEndLayout;
    LinearLayout linearLayout;
    Button join,left,list,edit,del;
    Event event;
    String id;
    FirebaseAuth mAuth;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        mAuth = FirebaseAuth.getInstance();
        getDataIntent();
    }
    void getDataIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra("eventId")) {
            id = intent.getStringExtra("eventId");
            FirebaseDatabase.getInstance().getReference(Event.EVENT_REF).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot eventSnapshot) {
                    viewSetup();
                    Event event = new Event(eventSnapshot);
                    switch(eventSnapshot.child(Event.EVENT_TYPE).getValue().toString()){
                        case CharitableEvent.eventType:
                            event = new CharitableEvent(eventSnapshot);
                            displayCharitableEvent(event);
                            break;
                        case RegisterClassEvent.eventType:
                            event = new RegisterClassEvent(eventSnapshot);
                            displayRegisterClassEvent(event);
                            break;
                        case SeminarEvent.eventType:
                            event = new SeminarEvent(eventSnapshot);
                            displaySeminarEvent(event);
                            break;
                        case OtherEvent.eventType:
                            event = new OtherEvent(eventSnapshot);
                            displayOtherEvent(event);
                            break;
                    }
                    EventDetailActivity.this.event = event;
                    buttonSetup();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void displayCharitableEvent(Event event){
        CharitableEvent charitableEvent = (CharitableEvent)event;
        img.setVisibility(View.VISIBLE);
        Picasso.get().load(charitableEvent.getEventImage()).placeholder(R.drawable.app_icon).into(img);
        eventName.setText(charitableEvent.getName());
        eventDetailLayout.setVisibility(View.VISIBLE);
        eventDetail.setText(charitableEvent.getDetail());
        eventOrgLayout.setVisibility(View.VISIBLE);
        eventOrg.setText(charitableEvent.getOrg());
        eventScheduleLayout.setVisibility(View.VISIBLE);
        eventSchedule.setText(charitableEvent.getSchedule());
        eventAddressLayout.setVisibility(View.VISIBLE);
        eventAddress.setText(charitableEvent.getAddress());
        eventMinLayout.setVisibility(View.VISIBLE);
        eventMin.setText(charitableEvent.getParticipantsRequire()+"");
        eventLimitLayout.setVisibility(View.VISIBLE);
        eventLimit.setText(charitableEvent.getLimit()+"");
        eventJoinedLayout.setVisibility(View.VISIBLE);
        eventJoined.setText(charitableEvent.partnerCount()+"");
        eventStartLayout.setVisibility(View.VISIBLE);
        eventStart.setText(Time.LongtoTime(charitableEvent.getStartTime()));
        eventEndLayout.setVisibility(View.VISIBLE);
        eventEnd.setText(Time.LongtoTime(charitableEvent.getEndTime()));
    }
    void displaySeminarEvent(Event event){
        SeminarEvent seminarEvent = (SeminarEvent)event;
        Picasso.get().load(seminarEvent.getEventImage()).placeholder(R.drawable.app_icon).into(img);
        eventName.setText(seminarEvent.getName());
        eventOrgLayout.setVisibility(View.VISIBLE);
        eventOrg.setText(seminarEvent.getOrg());
        eventSpeakerLayout.setVisibility(View.VISIBLE);
        eventSpeaker.setText(seminarEvent.getSpeakers());
        eventAudienceLayout.setVisibility(View.VISIBLE);
        eventAudience.setText(seminarEvent.getRecmdAudien());
        eventContentLayout.setVisibility(View.VISIBLE);
        eventContent.setText(seminarEvent.getContent());
        eventAddressLayout.setVisibility(View.VISIBLE);
        eventAddress.setText(seminarEvent.getAddress());
        eventJoinedLayout.setVisibility(View.VISIBLE);
        eventJoined.setText(seminarEvent.partnerCount()+"");
        eventStartLayout.setVisibility(View.VISIBLE);
        eventStart.setText(Time.LongtoTime(seminarEvent.getStartTime()));
        eventEndLayout.setVisibility(View.VISIBLE);
        eventEnd.setText(Time.LongtoTime(seminarEvent.getEndTime()));
    }
    void displayRegisterClassEvent(Event event){
        RegisterClassEvent registerClassEvent = (RegisterClassEvent)event;
        Picasso.get().load(registerClassEvent.getEventImage()).placeholder(R.drawable.app_icon).into(img);
        eventName.setText(registerClassEvent.getName());
        eventClassIdLayout.setVisibility(View.VISIBLE);
        eventClassId.setText(registerClassEvent.getClassId());
        eventBegLayout.setVisibility(View.VISIBLE);
        eventBeg.setText(registerClassEvent.getContent());
        eventMinLayout.setVisibility(View.VISIBLE);
        eventMin.setText(registerClassEvent.getMin()+"");
        eventLimitLayout.setVisibility(View.VISIBLE);
        eventLimit.setText(registerClassEvent.getLimit()+"");
        eventJoinedLayout.setVisibility(View.VISIBLE);
        eventJoined.setText(registerClassEvent.partnerCount()+"");
        eventStartLayout.setVisibility(View.VISIBLE);
        eventStart.setText(Time.LongtoTime(registerClassEvent.getStartTime()));
        eventEndLayout.setVisibility(View.VISIBLE);
        eventEnd.setText(Time.LongtoTime(registerClassEvent.getEndTime()));
    }
    void displayOtherEvent(Event event){
        OtherEvent otherEvent = (OtherEvent)event;
        Picasso.get().load(otherEvent.getEventImage()).placeholder(R.drawable.app_icon).into(img);
        eventName.setText(otherEvent.getName());
        eventDetailLayout.setVisibility(View.VISIBLE);
        eventDetail.setText(otherEvent.getDetail());
        eventOrgLayout.setVisibility(View.VISIBLE);
        eventOrg.setText(otherEvent.getOrg());
        eventLimitLayout.setVisibility(View.VISIBLE);
        eventLimit.setText(otherEvent.getLimit()+"");
        eventJoinedLayout.setVisibility(View.VISIBLE);
        eventJoined.setText(otherEvent.partnerCount()+"");
        eventAddressLayout.setVisibility(View.VISIBLE);
        eventAddress.setText(otherEvent.getAddress());
        eventStartLayout.setVisibility(View.VISIBLE);
        eventStart.setText(Time.LongtoTime(otherEvent.getStartTime()));
        eventEndLayout.setVisibility(View.VISIBLE);
        eventEnd.setText(Time.LongtoTime(otherEvent.getEndTime()));
        eventMission.setVisibility(View.VISIBLE);
        eventMission.setText(otherEvent.getMissionList());
    }
    void viewSetup(){
        actionBar();
        img = findViewById(R.id.event_detail_img);
        eventName = findViewById(R.id.event_detail_name_event);
        eventClassId = findViewById(R.id.event_detail_class_id);
        eventBeg= findViewById(R.id.event_detail_beg);
        eventDetail= findViewById(R.id.event_detail_detail);
        eventOrg= findViewById(R.id.event_detail_org);
        eventSpeaker= findViewById(R.id.event_detail_speaker);
        eventAudience   = findViewById(R.id.event_detail_recmd_audience);
        eventContent= findViewById(R.id.event_detail_content);
        eventSchedule = findViewById(R.id.event_detail_schedule);
        eventAddress = findViewById(R.id.event_detail_address);
        eventJoined = findViewById(R.id.event_detail_joined);
        eventMin = findViewById(R.id.event_detail_minimum);
        eventLimit = findViewById(R.id.event_detail_limit);
        eventStart = findViewById(R.id.event_detail_start_time);
        eventEnd = findViewById(R.id.event_detail_end_time);
        eventClassIdLayout = findViewById(R.id.event_detail_class_id_layout);
        eventBegLayout= findViewById(R.id.event_detail_beg_layout);
        eventDetailLayout= findViewById(R.id.event_detail_detail_layout);
        eventOrgLayout= findViewById(R.id.event_detail_org_layout);
        eventSpeakerLayout= findViewById(R.id.event_detail_speaker_layout);
        eventAudienceLayout   = findViewById(R.id.event_detail_recmd_audience_layout);
        eventContentLayout= findViewById(R.id.event_detail_content_layout);
        eventScheduleLayout = findViewById(R.id.event_detail_schedule_layout);
        eventAddressLayout = findViewById(R.id.event_detail_address_layout);
        eventJoinedLayout = findViewById(R.id.event_detail_joined_layout);
        eventMinLayout = findViewById(R.id.event_detail_minimum_layout);
        eventLimitLayout = findViewById(R.id.event_detail_limit_layout);
        eventStartLayout = findViewById(R.id.event_detail_start_time_layout);
        eventEndLayout = findViewById(R.id.event_detail_end_time_layout);
        eventMission = findViewById(R.id.event_detail_mission);
        join = findViewById(R.id.event_detail_join_btn);
        join.setOnClickListener(this);
        left = findViewById(R.id.event_detail_left_btn);
        left.setOnClickListener(this);
        linearLayout = findViewById(R.id.edit_layout);
        list = findViewById(R.id.detail_event_list);
        list.setOnClickListener(this);
        edit = findViewById(R.id.detail_event_edit);
        edit.setOnClickListener(this);
        del = findViewById(R.id.detail_event_del);
        del.setOnClickListener(this);
    }
    void buttonSetup(){
        if(event.isCreator(mAuth.getCurrentUser().getUid())){
            linearLayout.setVisibility(View.VISIBLE);
        }else linearLayout.setVisibility(View.GONE);

        if(event.isJoined(mAuth.getCurrentUser().getUid())){
            join.setVisibility(View.INVISIBLE);
            left.setVisibility(View.VISIBLE);
        }else {
            join.setVisibility(View.VISIBLE);
            left.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event_detail_join_btn:
                joinEvent();
                break;
            case R.id.event_detail_left_btn:
                leftEvent();
                break;
            case R.id.detail_event_list:
                sendUserToListEventActivity(event.getId());
                break;
            case R.id.detail_event_edit:
                editEvent(event);
                break;
            case R.id.detail_event_del:
                delete();
                break;
        }
    }

    void joinEvent(){
        Intent intent = new Intent(this,JoinEventActivity.class);
        intent.putExtra("eventId",id);
        startActivity(intent);
        finish();
    }

    void leftEvent(){
        FirebaseDatabase.getInstance().getReference(Event.EVENT_REF).child(id).child(Event.PARTICIPANTS_USER).child(mAuth.getCurrentUser().getUid()).setValue(null);
        finish();
    }
    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.event_detail_activity_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chi Tiết Sự Kiện");
    }

    private void sendUserToListEventActivity(String eventId){
        Intent intent = new Intent(this, ListUserEventActivity.class);
        intent.putExtra("eventId",eventId);
        startActivity(intent);
    }
    void sendUsertoCreateOtherActivity(){
        Intent intent = new Intent(this, CreateOtherActivity.class);
        intent.putExtra("eventId",event.getId());
        startActivity(intent);
    }
    void sendUsertoCreateCharitableActivity(){
        Intent intent = new Intent(this, CreateCharitableActivity.class);
        intent.putExtra("eventId",event.getId());
        startActivity(intent);
    }
    void sendUsertoCreateClassRegisterActivity(){
        Intent intent = new Intent(this, CreateClassRegisterActivity.class);
        intent.putExtra("eventId",event.getId());
        startActivity(intent);
    }
    void sendUsertoCreateSeminarActivity(){
        Intent intent = new Intent(this, CreateSeminarActivity.class);
        intent.putExtra("eventId",event.getId());
        startActivity(intent);
    }
    private void editEvent(Event event) {
        switch (event.getType()){
            case CharitableEvent.eventType:
                sendUsertoCreateCharitableActivity();
                break;
            case RegisterClassEvent.eventType:
                sendUsertoCreateClassRegisterActivity();
                break;
            case SeminarEvent.eventType:
                sendUsertoCreateSeminarActivity();
                break;
            case OtherEvent.eventType:
                sendUsertoCreateOtherActivity();
                break;
        }
    }
    private void delete(){
        new AlertDialog.Builder(this)
                .setTitle("Xóa sự kiện")
                .setMessage("bạn có chắc chắn muốn hủy sự kiện k ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        event.remove();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
