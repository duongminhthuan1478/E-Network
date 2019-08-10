package com.thuanduong.education.network.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class OtherEvent extends Event {
    public static final String NAME = "name";
    public static final String DETAIL = "detail";
    public static final String ORG = "org";
    public static final String ADDRESS = "address";

    String name,detail,org,address;
    public static final String eventType = "OtherEvent";

    public OtherEvent(String createUser, ArrayList<String> imgs, long startTime, long endTime, int limit, String name, String detail, String org, String address) {
        super(createUser, imgs, startTime, endTime, limit);
        this.name = name;
        this.detail = detail;
        this.org = org;
        this.address = address;
    }

    public OtherEvent(DataSnapshot dataSnapshot) {
        super(dataSnapshot);
        this.name = dataSnapshot.child(NAME).getValue().toString();
        this.detail = dataSnapshot.child(DETAIL).getValue().toString();
        this.org =dataSnapshot.child(ORG).getValue().toString();
        this.address = dataSnapshot.child(ADDRESS).getValue().toString();
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
        map.put(ADDRESS,address);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static String getEventType() {
        return eventType;
    }
}
