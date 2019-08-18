package com.thuanduong.education.network.Event;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.R;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView summary;
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
                    EventDetailActivity.this.event = event;
                    viewSetup();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void viewSetup(){
        actionBar();
        summary = findViewById(R.id.event_detail_summary);
        summary.setText(event.getSummary());
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
