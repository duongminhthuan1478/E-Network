package com.thuanduong.education.network.Event;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thuanduong.education.network.MainActivity;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.ParticipantsUser;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;

import org.json.JSONException;

import java.util.ArrayList;

public class JoinEventActivity extends AppCompatActivity implements View.OnClickListener {
    String eventId = "";
    RadioButton male,female;
    EditText phoneNumberET,nameET,mssvET;
    Button submit,cancel;
    //firebase
    FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseRef;
    //
    ParticipantsUser participantsUser;
    String userId,sdt,name,userName=null,mssv,mission=null;
    boolean isMale,isOtherEvent = false;
    OtherEvent otherEvent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
        mAuth = FirebaseAuth.getInstance();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        viewSetup();
        getEventId();
        getEventData();
    }
    void viewSetup(){
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
        participantsUser = new ParticipantsUser(userId,sdt,name,isMale,userName,mssv,mission);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.join_event_submit:
                if(isOtherEvent)
                    missionOption();
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


    void missionOption(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,otherEvent.vacantMissionList());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("option :");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(checkInputData()){
                    mission = otherEvent.vacantMissionList().get(i);
                    getData();
                    participantsUser.submit(eventId);
                }
                finish();
            }
        });
        builder.show();
    }
}
