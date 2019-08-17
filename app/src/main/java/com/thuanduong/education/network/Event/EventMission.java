package com.thuanduong.education.network.Event;

import com.google.firebase.database.DataSnapshot;

public class EventMission {
    public static String MISSON_REF = "mission";
    public static String NAME = "name";
    public static String AMOUNT = "amount";
    String name ;
    int amount ;

    public EventMission(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public EventMission(DataSnapshot dataSnapshot) {
        this.name = dataSnapshot.child(NAME).getValue().toString();
        this.amount = Integer.parseInt(dataSnapshot.child(AMOUNT).getValue().toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
