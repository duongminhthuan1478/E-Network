package com.thuanduong.education.network.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thuanduong.education.network.Event.EventMission;

import java.util.ArrayList;
import java.util.HashMap;

public class ParticipantsUser{
    public static final String PARTICIPANTS_USER = "participantsUser";
    public static final String SDT = "sdt";
    public static final String NAME = "name";
    public static final String USERNAME = "userName";
    public static final String MSSV = "mssv";
    public static final String ALARM_DISABLE = "alarmDisable";
    public static final String IS_MALE = "isMale";
    String id,sdt,name,userName,mssv;
    boolean isMale,alarmDisable = false;
    ArrayList<EventMission> missions = new ArrayList<>();
    public ParticipantsUser(String id, String sdt, String name, boolean isMale, String userName,String mssv) {
        this.id = id;
        this.sdt = sdt;
        this.name = name;
        this.isMale = isMale;
        this.userName = userName;
        this.mssv = mssv;
        this.missions = new ArrayList<EventMission>();
    }
    public ParticipantsUser(String id, String sdt, String name, boolean isMale, String userName,String mssv,ArrayList<EventMission> mission) {
        this.id = id;
        this.sdt = sdt;
        this.name = name;
        this.isMale = isMale;
        this.userName = userName;
        this.mssv = mssv;
        this.missions = mission;
    }
    public ParticipantsUser(DataSnapshot dataSnapshot) {
        this.id = dataSnapshot.getKey();
        if(dataSnapshot.hasChild(SDT))
            this.sdt = dataSnapshot.child(SDT).getValue().toString();
        if(dataSnapshot.hasChild(NAME))
            this.name = dataSnapshot.child(NAME).getValue().toString();
        if(dataSnapshot.hasChild(IS_MALE))
            this.isMale =Boolean.parseBoolean(dataSnapshot.child(IS_MALE).getValue().toString());
        if(dataSnapshot.hasChild(USERNAME))
            this.userName = dataSnapshot.child(USERNAME).getValue().toString();
        if(dataSnapshot.hasChild(ALARM_DISABLE))
            this.alarmDisable = Boolean.parseBoolean(dataSnapshot.child(ALARM_DISABLE).getValue().toString());
        if(dataSnapshot.hasChild(MSSV))
            this.mssv = dataSnapshot.child(MSSV).getValue().toString();
        if(dataSnapshot.hasChild(EventMission.MISSON_REF))
            for(DataSnapshot missionSnapshot:dataSnapshot.child(EventMission.MISSON_REF).getChildren()){
                EventMission eventMission = new EventMission(missionSnapshot);
                this.missions.add(eventMission);
            }
    }

    public HashMap toHashMap(){
        HashMap map = new HashMap();
        map.put(SDT,sdt);
        map.put(NAME,name);
        map.put(IS_MALE,isMale);
        map.put(ALARM_DISABLE,alarmDisable);
        map.put(USERNAME,userName);
        map.put(MSSV,mssv);
        map.put(EventMission.MISSON_REF,getMissionsHashMap());
        return map;
    }
    protected HashMap getMissionsHashMap(){
        HashMap map = new HashMap();
        int count = 0;
        for (EventMission mission:missions) {
            HashMap map1 = new HashMap();
            map1.put(EventMission.NAME,mission.getName());
            map1.put(EventMission.AMOUNT,mission.getAmount());
            map.put(count++ +"",map1);
        }
        return map;
    }
    public void submit(String eventId){
        FirebaseDatabase.getInstance()
                .getReference(Event.EVENT_REF)
                .child(eventId)
                .child(PARTICIPANTS_USER)
                .child(id)
                .setValue(toHashMap());
    }

    public static void disableAlarm(String eventId,String userId){
        FirebaseDatabase.getInstance()
                .getReference(Event.EVENT_REF)
                .child(eventId)
                .child(PARTICIPANTS_USER)
                .child(userId)
                .child(ALARM_DISABLE)
                .setValue(true);
    }
    public boolean isAlarmDisable() {
        return alarmDisable;
    }

    public void setAlarmDisable(boolean alarmDisable) {
        this.alarmDisable = alarmDisable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public ArrayList<EventMission> getMission() {
        return missions;
    }

    public void setMission(ArrayList<EventMission> mission) {
        this.missions = mission;
    }
}
