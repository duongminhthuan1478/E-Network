package com.thuanduong.education.network.Model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class SeminarEvent extends Event {
    public static final String NAME = "name";
    public static final String ORG = "org";
    public static final String SPEAKERS = "speakers";
    public static final String RECMD_AUDIEN = "recmdAudien";
    public static final String CONTENT = "content";
    public static final String ADDRESS = "address";

    String name,org,speakers,recmdAudien,content,address;
    public static final String eventType = "SeminarEvent";


    public SeminarEvent(String createUser, ArrayList<String> imgs, long startTime, long endTime, int limit, String name, String org, String speakers, String recmdAudien, String content, String address) {
        super(createUser, imgs, startTime, endTime, limit);
        this.name = name;
        this.org = org;
        this.speakers = speakers;
        this.recmdAudien = recmdAudien;
        this.content = content;
        this.address = address;
    }

    public SeminarEvent(DataSnapshot dataSnapshot) {
        super(dataSnapshot);
        this.name = dataSnapshot.child(NAME).getValue().toString();
        this.org = dataSnapshot.child(ORG).getValue().toString();
        this.speakers = dataSnapshot.child(SPEAKERS).getValue().toString();
        this.recmdAudien =dataSnapshot.child(RECMD_AUDIEN).getValue().toString();
        this.content = dataSnapshot.child(CONTENT).getValue().toString();
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
        map.put(ORG,org);
        map.put(SPEAKERS,speakers);
        map.put(RECMD_AUDIEN,recmdAudien);
        map.put(CONTENT,content);
        map.put(ADDRESS,address);
        eventRef.child(id+"").setValue(map);
    }
    @Override
    public String getSummary() {
        String summary = getEventName()
                +"\n" + getEventTitle()
                +"\n" + getEventContent();
        return summary;
    }
    @Override
    public String getEventName(){
        return name;
    }

    @Override
    public String getEventTitle(){
        return org;
    }

    @Override
    public String getEventContent(){
        return content;
    }


    // getter & setter
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSpeakers() {
        return speakers;
    }

    public void setSpeakers(String speakers) {
        this.speakers = speakers;
    }

    public String getRecmdAudien() {
        return recmdAudien;
    }

    public void setRecmdAudien(String recmdAudien) {
        this.recmdAudien = recmdAudien;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
