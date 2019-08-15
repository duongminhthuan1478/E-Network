package com.thuanduong.education.network.Model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterClassEvent extends Event{
    public static final String NAME = "name";
    public static final String CLASS_ID = "classId";
    public static final String CONTENT = "content";
    public static final String MIN = "min";

    String name,classId,content;
    int min;
    public static final String eventType = "RegisterClassEvent";

    public RegisterClassEvent(String createUser, ArrayList<String> imgs, long startTime, long endTime, int limit, String name, String classId, String content, int min) {
        super(createUser, imgs, startTime, endTime, limit);
        this.name = name;
        this.classId = classId;
        this.content = content;
        this.min = min;
    }

    public RegisterClassEvent(DataSnapshot dataSnapshot) {
        super(dataSnapshot);
        this.name = dataSnapshot.child(NAME).getValue().toString();
        this.classId = dataSnapshot.child(CLASS_ID).getValue().toString();
        this.content =dataSnapshot.child(CONTENT).getValue().toString();
        this.min = Integer.parseInt(dataSnapshot.child(MIN).getValue().toString());
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
        map.put(CLASS_ID,classId);
        map.put(CONTENT,content);
        map.put(MIN,min);
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
        return classId;
    }

    @Override
    public String getEventContent(){
        return content;
    }

    //getter & setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public static String getEventType() {
        return eventType;
    }
}
