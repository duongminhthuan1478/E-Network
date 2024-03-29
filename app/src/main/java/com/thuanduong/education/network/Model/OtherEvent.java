package com.thuanduong.education.network.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.thuanduong.education.network.Event.EventMission;
import com.thuanduong.education.network.Ultil.Time;

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

    public OtherEvent(String id,String createUser, ArrayList<String> imgs, long startTime, long endTime, int limit, ArrayList<EventMission> missions, String name, String detail, String org, String address) {
        super(id,createUser, imgs, startTime, endTime, limit);
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
                    for(EventMission eventMission:participantsUser1.getMission()){
                        if (eventMission.getName().equals(mission.getName())) {
                            count+=eventMission.getAmount();
                        }
                    }
                }
                if(count>= mission.getAmount())
                    return false;
            }
        }
        return true;
    }

    public int countMissionSlotRemaning(String missionName) {
        int result = 0;
        for (EventMission mission:missions) {
            if(mission.getName().equals(missionName)) {
                int count = 0;
                for (ParticipantsUser participantsUser1 : participantsUser) {
                    for(EventMission eventMission:participantsUser1.getMission()){
                        if (eventMission.getName().equals(mission.getName())) {
                            count+=eventMission.getAmount();
                        }
                    }
                }
                result = mission.getAmount() - count;
            }
        }
        return result;
    }

    public int countMissionPartner(String missionName) {
        int count = 0;
        for (EventMission mission:missions) {
            if(mission.getName().equals(missionName)) {
                for (ParticipantsUser participantsUser1 : participantsUser) {
                    for(EventMission eventMission:participantsUser1.getMission()){
                        if (eventMission.getName().equals(mission.getName())) {
                            count+=eventMission.getAmount();
                        }
                    }
                }
            }
        }
        return count;
    }

    public ArrayList<EventMission> vacantMissionList() {
        ArrayList<EventMission> vacantMission = new ArrayList<>();
        for (EventMission mission:missions) {
            if(isMissionHasSpace(mission.getName())){
                vacantMission.add(mission);
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
        String summary = "<!-- #######  YAY, I AM THE SOURCE EDITOR! #########-->\n" +
                "<h2 style=\"text-align: center;\"><strong><img src=\""+getEventImage()+"\" alt=\"Default\" width=\"70%\" height=\"200\" /></strong></h2>\n" +
                "<h3>&nbsp;"+getName()+"</h3>\n" +
                "<p><strong>&nbsp;</strong></p>\n" +
                "<p>&nbsp;Th&ocirc;ng tin chi tiết: "+getDetail()+"</p>\n" +
                "<p>&nbsp;Đơn vị tổ chức: "+getOrg()+"</p>\n" +
                "<p>&nbsp;Địa chỉ: "+getAddress()+"</p>\n" +
                "<p>&nbsp;Ng&agrave;y bắt đầu: "+ Time.LongtoTime(getStartTime())+"</p>\n" +
                "<p>&nbsp;Ng&agrave;y kết th&uacute;c: "+Time.LongtoTime(getEndTime())+"</p>";
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

    public String getType(){
        return eventType;
    }

    public String getMissionList(){
        String result ="";
        for (EventMission eventMission:missions) {
            result +="\n-"+eventMission.getName()+"("+countMissionPartner(eventMission.getName())+"/"+eventMission.getAmount()+")";
        }
        return result;
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

    public ArrayList<EventMission> getMissions() {
        return missions;
    }

    public void setMissions(ArrayList<EventMission> missions) {
        this.missions = missions;
    }
}
