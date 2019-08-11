package com.thuanduong.education.network.Event;

import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.thuanduong.education.network.Model.ParticipantsUser;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.Time;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean active = false;
    //view
    TextView nameTv,titleTv,startTimeTv;
    Button stopBtn,sleepBtn;
    //
    Ringtone r;
    String eventId,eventName,title,startTime;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Log.d("doAlarm", "start");
        viewSetup();
        getEventData();
        doAlarm();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    void getEventData(){
        Intent intent = getIntent();
        if(intent.hasExtra("eventId")
                && intent.hasExtra("eventName")
                && intent.hasExtra("title")
                && intent.hasExtra("startTime"))
        {
            eventId = intent.getStringExtra("eventId");
            eventName = intent.getStringExtra("eventName");
            title = intent.getStringExtra("title");
            startTime = Time.timeRemaining(intent.getLongExtra("startTime",0));
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        else finish();
        //
        nameTv.setText(eventName);
        titleTv.setText(title);
        startTimeTv.setText(startTime);
    }
    void viewSetup(){
        nameTv = findViewById(R.id.event_alarm_name);
        titleTv = findViewById(R.id.event_alarm_title);
        startTimeTv = findViewById(R.id.event_alarm_start_time);
        stopBtn = findViewById(R.id.event_alarm_stop_btn);
        sleepBtn = findViewById(R.id.event_alarm_sleep_btn);
        stopBtn.setOnClickListener(this);
        sleepBtn.setOnClickListener(this);
    }
    void doAlarm(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 50000);

    }
    void stopAlarm(){
        r.stop();
        ParticipantsUser.disableAlarm(eventId,userId);
        finish();
    }
    void sleepAlarm(){
        r.stop();
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event_alarm_stop_btn:
                stopAlarm();
                break;
            case R.id.event_alarm_sleep_btn:
                sleepAlarm();
                break;
        }
    }
}
