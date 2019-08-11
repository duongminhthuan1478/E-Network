package com.thuanduong.education.network.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Event {
    public static final String EVENT_REF = "Events";
    public static final String EVENT_TYPE = "eventType";
    public static final String CREATE_USER = "createUser";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String LIMIT = "limit";
    public static final String IMGS = "imgs";
    public static final String PARTICIPANTS_USER = "participantsUser";

    DatabaseReference eventRef;
    String id = null,createUser;
    long startTime,endTime;
    int limit ;
    ArrayList<String> imgs = new ArrayList<>();
    ArrayList<ParticipantsUser> participantsUser = new ArrayList<>();


    public Event(String createUser,ArrayList<String> imgs, long startTime, long endTime, int limit) {
        eventRef = FirebaseDatabase.getInstance().getReference(EVENT_REF);
        this.id = eventRef.push().getKey();
        this.createUser = createUser;
        this.imgs = imgs;
        this.startTime = startTime;
        this.endTime = endTime;
        this.limit = limit;
    }

    public Event(DataSnapshot dataSnapshot) {
        eventRef = FirebaseDatabase.getInstance().getReference(EVENT_REF);
        this.id = dataSnapshot.getKey();
        this.createUser = dataSnapshot.child(CREATE_USER).getValue().toString();
        this.startTime = Long.parseLong(dataSnapshot.child(START_TIME).getValue().toString());
        this.endTime = Long.parseLong(dataSnapshot.child(END_TIME).getValue().toString());
        this.limit = Integer.parseInt(dataSnapshot.child(LIMIT).getValue().toString());
        for(DataSnapshot img : dataSnapshot.child(IMGS).getChildren()){
            imgs.add(img.getValue().toString());
        }
        for (DataSnapshot participantsUsers: dataSnapshot.child(PARTICIPANTS_USER).getChildren() ) {
            participantsUser.add(new ParticipantsUser(participantsUsers));
        }
    }

    public void submit(){
    }

    protected HashMap participantsUserHashMap(){
        HashMap map = new HashMap();
        for (ParticipantsUser participantsUser1:participantsUser) {
            map.put(participantsUser1.id,participantsUser1.toHashMap());
        }
        return map;
    }

    protected HashMap imgsHashMap(){
        HashMap map = new HashMap();
        int index = 0;
        for (String img:imgs) {
            map.put(index++ + "",img);
        }
        return map;
    }


    public boolean isCreator(String userId){
        return this.createUser.equals(userId);
    }

    public boolean isJoined(String userId){
        boolean check = false;
        for (ParticipantsUser participantsUser1:participantsUser) {
            check |= participantsUser1.getId().equals(userId);
        }
        return check;
    }


    public boolean isUserAlarmDisable(String userId){
        boolean check = false;
        for (ParticipantsUser participantsUser1:participantsUser) {
            if(participantsUser1.getId().equals(userId)){
                check = participantsUser1.isAlarmDisable();
            }
        }
        return check;
    }

    public int partnerCount(){
        return participantsUser.size();
    }

    public HashMap getSummary(){
        HashMap m = new HashMap();
        return m;
    }

    public String getEventName(){
        return "null";
    }
    public String getEventTitle(){
        return "null";
    }
    public String getEventContent(){
        return "null";
    }
    public String getEventImage(){
        return imgs.size() > 0 ? imgs.get(0) : "null";
    }
    // getter & setter


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public ArrayList<ParticipantsUser> getParticipantsUser() {
        return participantsUser;
    }

    public void setParticipantsUser(ArrayList<ParticipantsUser> participantsUser) {
        this.participantsUser = participantsUser;
    }
}
