package com.thuanduong.education.network.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.thuanduong.education.network.Event.EventMission;

import java.util.ArrayList;
import java.util.HashMap;

public class OtherEvent extends Event {
    public static final String NAME = "name";
    public static final String DETAIL = "detail";
    public static final String ORG = "org";
    public static final String ADDRESS = "address";

    ArrayList<EventMission> missions = new ArrayList<>();
    String name,detail,org,address;
    public static final String eventType = "OtherEvent";

    public OtherEvent(String createUser, ArrayList<String> imgs, long startTime, long endTime, int limit, ArrayList<EventMission> missions, String name, String detail, String org, String address) {
        super(createUser, imgs, startTime, endTime, limit);
        this.missions = missions;
        this.name = name;
        this.detail = detail;
        this.org = org;
        this.address = address;
    }

    public OtherEvent(DataSnapshot dataSnapshot) {
        super(dataSnapshot);
        if(dataSnapshot.hasChild(EventMission.MISSON_REF))
            for (DataSnapshot mission:dataSnapshot.child(EventMission.MISSON_REF).getChildren())
                this.missions.add(new EventMission(mission));
        if(dataSnapshot.hasChild(NAME))
            this.name = dataSnapshot.child(NAME).getValue().toString();
        if(dataSnapshot.hasChild(DETAIL))
            this.detail = dataSnapshot.child(DETAIL).getValue().toString();
        if(dataSnapshot.hasChild(ORG))
            this.org =dataSnapshot.child(ORG).getValue().toString();
        if(dataSnapshot.hasChild(ADDRESS))
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
        map.put(EventMission.MISSON_REF,getMissionsHashMap());
        eventRef.child(id).setValue(map);
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

    public boolean isMissionHasSpace(String missionName) {
        for (EventMission mission:missions) {
            if(mission.getName().equals(missionName)) {
                int count = 0;
                for (ParticipantsUser participantsUser1 : participantsUser) {
                    if (participantsUser1.getMission().equals(mission.getName())) {
                        count++;
                    }
                }
                if(count>= mission.getAmount())
                    return false;
            }
        }
        return true;
    }


    public int countMissionPartner(String missionName) {
        int count = 0;
        for (EventMission mission:missions) {
            if(mission.getName().equals(missionName)) {

                for (ParticipantsUser participantsUser1 : participantsUser) {
                    if (participantsUser1.getMission().equals(mission.getName())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public ArrayList<String> vacantMissionList() {
        ArrayList<String> vacantMission = new ArrayList<>();
        for (EventMission mission:missions) {
            if(isMissionHasSpace(mission.getName())){
                vacantMission.add(mission.getName());
            }
        }
        return vacantMission;
    }

    @Override
    public int getLimit() {
        int count = 0;
        for (EventMission mission:missions) {
            count+=mission.getAmount();
        }
        return count;
    }
    @Override
    public String getSummary() {
        String summary = "sự kiện : "+getEventName()
                +"\n" + getEventTitle()
                +"\n\n\n nội dung : " + getEventContent()
                +"\n địa chỉ : " + getEventTitle()
                +"\n nhiệm vụ : ";
        for(EventMission mission : missions)
            summary += "\n - " + mission.getName() + "     " + mission.getAmount() + "người";
        return summary;
    }
    @Override
    public boolean canJoin(String uid) {
        return vacantMissionList().size() > 0;
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
        return detail;
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
