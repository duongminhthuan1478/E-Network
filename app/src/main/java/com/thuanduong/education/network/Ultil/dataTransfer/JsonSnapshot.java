package com.thuanduong.education.network.Ultil.dataTransfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonSnapshot {
    Object snapshot=null;
    String key = "json";
    public JsonSnapshot() {
    }
    public JsonSnapshot(JSONObject snapshot) {
        this.snapshot = snapshot;
    }
    private JsonSnapshot(Object snapshot, String key) {
        this.snapshot =  snapshot;
        this.key = key;
    }
    private JsonSnapshot(JSONObject snapshot, String key) {
        this.snapshot = snapshot;
        this.key = key;
    }
    public JsonSnapshot(String json) {
        try {
            this.snapshot = new JSONObject(json);
        } catch (JSONException e) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("array",jsonArray);
                this.snapshot = jsonObject;
            } catch (JSONException ex) {
                ex.printStackTrace();
                e.printStackTrace();
            }
        }
    }
    @Override
    public String toString() {
        try{
            if(snapshot != null && snapshot != "null")
            {
                return snapshot.toString();
            }else return "";
        }catch (ClassCastException e){
            return "";
        }
    }
    public int toInt() {
        try{
            if(snapshot != null && snapshot != "null")
                return  (int)(snapshot) ;
            else return 0;
        }catch (ClassCastException e){
            return 0;
        }
    }
    public long toLong() {
        try{
            if(snapshot != null && snapshot != "null")
                return  (long)(snapshot) ;
            else return 0;
        }catch (ClassCastException e){
            return 0;
        }
    }
    public double toDouble() {
        try {
            if(snapshot != null && snapshot != "null")
                return  (double)(snapshot) ;
            else return 0;
        }catch (ClassCastException e){
            return 0;
        }
    }
    public boolean toBoolean() {
        try{
            if(snapshot != null && snapshot != "null")
                return  Boolean.parseBoolean(snapshot.toString()) ;
            else return false;
        }catch (ClassCastException e){
            return false;
        }
    }
    private JsonSnapshot getChild(String childname)
    {
        try {
            if(snapshot!=null)
                return new JsonSnapshot(((JSONObject) snapshot).get(childname),childname);
            else         return new JsonSnapshot();
        } catch (JSONException e) {
                return new JsonSnapshot();
        }
    }
    public JsonSnapshot child(String childname)
    {
        // có thể nhập link nodeA/nodeB/nodeC thay vì dùng 3 lệnh child
        String[] uri = childname.split("/");
        JsonSnapshot jsonSnapshot = new JsonSnapshot((JSONObject) snapshot);
        for(String child:uri)
        {
            jsonSnapshot = jsonSnapshot.getChild(child);
        }
        return jsonSnapshot;
    }
    public double getNumberOfChild(String childname)
    {
        try {
            return Double.parseDouble(((JSONObject) snapshot).getString(childname));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public ArrayList<JsonSnapshot> getArrayOfChild()
    {
        ArrayList<JsonSnapshot> jsonSnapshotArrayList =  new ArrayList<>();
        try {
            JSONArray jsonArray = ((JSONArray) snapshot);
            for(int i=0 ;i < jsonArray.length();i++){
                jsonSnapshotArrayList.add( new JsonSnapshot(jsonArray.get(i).toString()));
            }
            return jsonSnapshotArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<JsonSnapshot> getChildrents()
    {
        ArrayList<JsonSnapshot> jsonSnapshotArrayList = new ArrayList<>();
        for (String key:getKeys()) {
            jsonSnapshotArrayList.add(child(key));
        }
        return jsonSnapshotArrayList;
    }
    public ArrayList<String> getKeys()
    {
        ArrayList<String> jsonSnapshotArrayList = new ArrayList<>();
        Iterator<String> iter = ((JSONObject) snapshot).keys();
        while (iter.hasNext()) {
            String key = iter.next();
            jsonSnapshotArrayList.add(key);
        }
        return jsonSnapshotArrayList;
    }
    public int getChildentsCount()
    {
        return ((JSONObject) snapshot).length();
    }

    public boolean hasChild(String childname)
    {
        if((new JsonSnapshot(((JSONObject) snapshot)).getChildentsCount()>0))
            return   (new JsonSnapshot(((JSONObject) snapshot)).child(childname).exists());
        return false;
    }
    private boolean exists()
    {
        if(snapshot==null)
            return false;
        return true;
    }
    public String getKey()
    {
        return key;
    }
    public boolean isNotNull()
    {
        return exists();
    }

    // getter setter
    private JSONObject getSnapshot() {
        return ((JSONObject) snapshot);
    }

    private void setSnapshot(JSONObject snapshot) {
        this.snapshot = snapshot;
    }


}
