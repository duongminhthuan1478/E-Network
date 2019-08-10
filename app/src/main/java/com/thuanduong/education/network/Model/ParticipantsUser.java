package com.thuanduong.education.network.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ParticipantsUser{
    String id,sdt,name,userName,mssv;
    boolean isMale,alarmDisable = false;
    public ParticipantsUser(String id, String sdt, String name, boolean isMale, String userName,String mssv) {
        this.id = id;
        this.sdt = sdt;
        this.name = name;
        this.isMale = isMale;
        this.userName = userName;
        this.mssv = mssv;
    }
    public ParticipantsUser(DataSnapshot dataSnapshot) {
        this.id = dataSnapshot.getKey();
        this.sdt = dataSnapshot.child("sdt").getValue().toString();
        this.name = dataSnapshot.child("name").getValue().toString();
        this.isMale =Boolean.parseBoolean(dataSnapshot.child("isMale").getValue().toString());
        this.userName = dataSnapshot.child("userName").getValue().toString();
        this.alarmDisable = Boolean.parseBoolean(dataSnapshot.child("alarmDisable").getValue().toString());
        if(dataSnapshot.hasChild("mssv"))
        this.mssv = dataSnapshot.child("mssv").getValue().toString();
    }

    public HashMap toHashMap(){
        HashMap map = new HashMap();
        map.put("sdt",sdt);
        map.put("name",name);
        map.put("isMale",isMale);
        map.put("alarmDisable",alarmDisable);
        map.put("userName",userName);
        map.put("mssv",mssv);
        return map;
    }

    public void submit(String eventId){
        FirebaseDatabase.getInstance()
                .getReference("Events")
                .child(eventId)
                .child("participantsUser")
                .child(id)
                .setValue(toHashMap());
    }

    public static void disableAlarm(String eventId,String userId){
        FirebaseDatabase.getInstance()
                .getReference("Events")
                .child(eventId)
                .child("participantsUser")
                .child(userId)
                .child("alarmDisable")
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
}