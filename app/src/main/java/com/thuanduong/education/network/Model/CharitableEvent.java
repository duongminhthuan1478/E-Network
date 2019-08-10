package com.thuanduong.education.network.Model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class CharitableEvent extends Event  {
    public static final String NAME = "name";
    public static final String DETAIL = "detail";
    public static final String ORG = "org";
    public static final String SCHEDULE = "schedule";
    public static final String ADDRESS = "address";
    public static final String PARTICIPANTS_REQ = "participantsRequire";

    String name,detail,org,schedule,address;
    int participantsRequire;
    public static final String eventType = "CharitableEvent";

    public CharitableEvent(String createUser, ArrayList<String> imgs, long startTime, long endTime, int limit, String name, String detail, String org, String schedule, String address, int participantsRequire) {
        super(createUser, imgs, startTime, endTime, limit);
        this.name = name;
        this.detail = detail;
        this.org = org;
        this.schedule = schedule;
        this.address = address;
        this.participantsRequire = participantsRequire;
    }

    public CharitableEvent(DataSnapshot dataSnapshot) {
        super(dataSnapshot);
        this.name = dataSnapshot.child(NAME).getValue().toString();
        this.detail = dataSnapshot.child(DETAIL).getValue().toString();
        this.org =dataSnapshot.child(ORG).getValue().toString();
        this.schedule = dataSnapshot.child(SCHEDULE).getValue().toString();
        this.address = dataSnapshot.child(ADDRESS).getValue().toString();
        this.participantsRequire = Integer.parseInt(dataSnapshot.child(PARTICIPANTS_REQ).getValue().toString());
    }

    @Override
    public void submit(){
        HashMap map = new HashMap();
        map.put(EVENT_TYPE,eventType);
        map.put(CREATE_USER,createUser);
        map.put(IMGS,imgsHashMap());
        map.put(START_TIME,startTime);
        map.put(END_TIME,endTime);
        map.put(LIMIT,limit);
        map.put(PARTICIPANTS_USER,participantsUserHashMap());
        map.put(NAME,name);
        map.put(DETAIL,detail);
        map.put(ORG,org);
        map.put(SCHEDULE,schedule);
        map.put(ADDRESS,address);
        map.put(PARTICIPANTS_REQ,participantsRequire);
        eventRef.child(id+"").setValue(map);
    }

    //getter & setter


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getParticipantsRequire() {
        return participantsRequire;
    }

    public void setParticipantsRequire(int participantsRequire) {
        this.participantsRequire = participantsRequire;
    }

    public static String getEventType() {
        return eventType;
    }
}
