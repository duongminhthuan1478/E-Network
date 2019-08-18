package com.thuanduong.education.network.Event;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thuanduong.education.network.Adapter.JoinedEventMissionAdapter;
import com.thuanduong.education.network.Adapter.ParticipantsEventRecyclerViewAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.JoinedEventMissionViewHolder;
import com.thuanduong.education.network.MainActivity;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.ParticipantsUser;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;

import org.json.JSONException;

import java.util.ArrayList;

public class JoinEventActivity extends AppCompatActivity implements View.OnClickListener, JoinedEventMissionAdapter.ViewAdapterInterface {
    String eventId = "";
    TextView missionTv;
    RadioButton male,female;
    EditText phoneNumberET,nameET,mssvET;
    Button submit,cancel;
    RecyclerView recyclerView;
    LinearLayout missionCol;
    //
    ArrayList<EventMission> eventMissions = new ArrayList<>();
    ArrayList<EventMission> myMissions = new ArrayList<>();
    JoinedEventMissionAdapter Adapter;
    //firebase
    FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseRef;
    //
    ParticipantsUser participantsUser;
    String userId,sdt,name,userName=null,mssv;
    boolean isMale,isOtherEvent = false;
    OtherEvent otherEvent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
        mAuth = FirebaseAuth.getInstance();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        getEventId();
        getEventData();
    }
    void viewSetup(){
        missionTv = findViewById(R.id.join_event_mission_tv);
        missionCol = findViewById(R.id.join_event_mission_col);
        recyclerView = findViewById(R.id.join_event_mission_recyclerview);
        if(isOtherEvent) {
            eventMissions.addAll(otherEvent.vacantMissionList());
            missionTv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            missionCol.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        Adapter = new JoinedEventMissionAdapter(eventMissions,this);
        recyclerView.setAdapter(Adapter);
        male = findViewById(R.id.radioButton_male);
        female = findViewById(R.id.radioButton_female);
        phoneNumberET = findViewById(R.id.join_event_phonenumber);
        nameET = findViewById(R.id.join_event_fullname);
        mssvET = findViewById(R.id.join_event_mssv);
        submit = findViewById(R.id.join_event_submit);
        cancel = findViewById(R.id.join_event_cancel);

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMale = true;
            }
        });
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMale = false;
            }
        });
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }
    void getEventData(){
        FirebaseDatabase.getInstance().getReference(Event.EVENT_REF).child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isOtherEvent = dataSnapshot.child(Event.EVENT_TYPE).getValue().toString().equals(OtherEvent.eventType)){
                    otherEvent = new OtherEvent(dataSnapshot);
                }
                viewSetup();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    boolean checkInputData(){
        boolean check = true;
        check &= phoneNumberET.getText().toString().length()> 0
                && mssvET.getText().toString().length() > 0
                && nameET.getText().toString().length() > 0;
        return check;
    }
    void getEventId(){
        Intent intent = getIntent();
        if(intent.hasExtra("eventId")){
            eventId = intent.getStringExtra("eventId");
            getUsername();
        }else
        {
            Toast.makeText(this," this event no longer exist",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void getData(){
        sdt = phoneNumberET.getText().toString();
        name = nameET.getText().toString();
        userName = userName==null ?nameET.getText().toString() :userName;
        mssv = mssvET.getText().toString();
        participantsUser = new ParticipantsUser(userId,sdt,name,isMale,userName,mssv,myMissions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.join_event_submit:
                if(isOtherEvent) {
                    if(checkData()){
                        getData();
                        submit();
                        participantsUser.submit(eventId);
                        finish();
                    }
                }
                else if(checkInputData()){
                    getData();
                    participantsUser.submit(eventId);
                    finish();
                }
                break;
            case R.id.join_event_cancel:
                finish();
                break;
        }
    }
    void getUsername(){
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    userName = dataSnapshot.child("fullname").getValue().toString();
                else ShowToast.showToast(JoinEventActivity.this, "Profile name do not exists..");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    boolean checkData(){
        boolean check = true;
        for (EventMission e:myMissions) {
            check &= otherEvent.countMissionSlotRemaning(e.getName()) >= e.amount;
        }
        return check;
    }

    void submit(){

    }

    @Override
    public void onBindViewHolder(final JoinedEventMissionViewHolder holder, ArrayList<EventMission> eventMissions, int position) {
        final EventMission eventMission = eventMissions.get(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.input.setFocusable(isChecked);
                if(!isChecked) deleteMission(eventMission.getName());
            }
        });
        holder.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int number = 0;
                if(text.length() > 0 && TextUtils.isDigitsOnly(s))
                {
                    number = Integer.parseInt(text);
                }
                addMyMission(eventMission.getName(),number);
            }
        });
        holder.name.setText(eventMission.getName());
        holder.amount.setText(eventMission.getAmount()+"");
        holder.had.setText(this.otherEvent.countMissionPartner(eventMission.getName())+"");
    }
    void addMyMission(String missionName,int amount){
        for(EventMission e:myMissions){
            if(e.getName().equals(missionName)){
                e.setAmount(amount);
                return;
            }
        }
        myMissions.add(new EventMission(missionName,amount));
    }
    void deleteMission(String missionName){
        for(int i=0;i<myMissions.size();i++){
            if(myMissions.get(i).getName().equals(missionName)){
                myMissions.remove(i);
                return;
            }
        }
    }
}
