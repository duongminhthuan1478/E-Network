package com.thuanduong.education.network.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.thuanduong.education.network.Event.AlarmActivity;
import com.thuanduong.education.network.MainActivity;
import com.thuanduong.education.network.Model.CharitableEvent;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EventNotificationService extends Service {
    DatabaseReference eventRef;
    FirebaseAuth mAuth;

    final long NOTICE_TIME = 30*60000;
    final long PERIOD = 60000;
    ArrayList<Event> events = new ArrayList<>();
    public EventNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "ddm nos chayj roi");
        eventRef = FirebaseDatabase.getInstance().getReference(Event.EVENT_REF);
        createRuntime();
        return super.onStartCommand(intent, flags, startId);
    }

    void createRuntime(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getParticipatingEventsList();
                    }
                },500,PERIOD);
            }
        });
    }

    void getParticipatingEventsList(){
        FirebaseDatabase.getInstance().getReference(Event.EVENT_REF).orderByChild(Event.START_TIME).startAt(Time.getCur()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                Log.d("service data", dataSnapshot.toString());
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
                    String userid = mAuth.getCurrentUser().getUid();
                    if(event.isJoined(userid)&&!event.isUserAlarmDisable(userid)){
                        events.add(event);
                    }
                    getAlarm();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getAlarm(){
        Date time = Calendar.getInstance().getTime();
        long curTime = time.getTime();
        for(Event ev:events){
            long gapOfTime = ev.getStartTime() - curTime ;
            Log.d("gapOfTime", gapOfTime+"");
            if( gapOfTime < NOTICE_TIME && gapOfTime > 0 ){
                alarmClock(ev);
            }
        }
    }

    void alarmClock(Event event){
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("eventId",event.getId());
        intent.putExtra("eventName",event.getEventName());
        intent.putExtra("title",event.getEventTitle());
        intent.putExtra("startTime",event.getStartTime());
        if(AlarmActivity.active == false ) startActivity(intent);
    }
    void doNotification(String title, String message , int id ) {
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.
                getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = id;
        String channelId = "channel-id";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.profile_icon)//R.mipmap.ic_launcher
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(new long[]{100, 250})
                .setLights(Color.YELLOW, 500, 5000)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
