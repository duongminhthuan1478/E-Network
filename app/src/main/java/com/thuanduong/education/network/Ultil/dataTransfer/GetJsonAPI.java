package com.thuanduong.education.network.Ultil.dataTransfer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetJsonAPI {
    static String apiUrl ="";
    ApiCall apiCall =null;

    private GetJsonAPI(String apiUrl) {
        this.apiUrl = apiUrl;
    }


    public static GetJsonAPI setUrl(String apiUrl1) {
        return new GetJsonAPI(apiUrl1.replace(" ","%20"));
    }
    private String addParams(String apiUrl,String key, String value){
        if(apiUrl.indexOf("?") == -1)
            return apiUrl+"?"+key+"="+value;
        return apiUrl+"&"+key+"="+value;
    }
    public GetJsonAPI addParams(String key, String value){
        return new GetJsonAPI(addParams(apiUrl,key,value));
    }
    public GetJsonAPI addArrayParams(String key, ArrayList<String> values){
        for (int i = 0;i < values.size();i++) {
            apiUrl = addParams(apiUrl,"["+i+"]"+key,values.get(i));
        }
        return new GetJsonAPI(apiUrl);
    }
    public void interruptApiCall(){
        if(apiCall==null)
            return;
        apiCall.cancel(true);
        apiCall.isRunning = false;
        return;
    }
    public boolean isRunning(){
        if(apiCall==null)
            return false;
        return apiCall.isRunning;
    }
    public void get(ApiCall.AsyncApiCall asyncApiCall){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("GET",asyncApiCall));
            apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void get(ApiCall.AsyncApiCall asyncApiCall,String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("GET",asyncApiCall,token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void post(ApiCall.AsyncApiCall asyncApiCall){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("POST",asyncApiCall));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void post(ApiCall.AsyncApiCall asyncApiCall,JsonSnapshot body){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("POST",asyncApiCall,body));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void post(ApiCall.AsyncApiCall asyncApiCall,String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("POST", asyncApiCall, token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void post(ApiCall.AsyncApiCall asyncApiCall,JsonSnapshot body ,String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("POST", asyncApiCall, body,token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void put(ApiCall.AsyncApiCall asyncApiCall){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("PUT", asyncApiCall));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void put(ApiCall.AsyncApiCall asyncApiCall,JsonSnapshot body){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("PUT", asyncApiCall, body));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void put(ApiCall.AsyncApiCall asyncApiCall,String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("PUT", asyncApiCall, token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void put(ApiCall.AsyncApiCall asyncApiCall,JsonSnapshot body ,String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("PUT", asyncApiCall, body,token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void delete(ApiCall.AsyncApiCall asyncApiCall){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("DELETE", asyncApiCall));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void delete(ApiCall.AsyncApiCall asyncApiCall,JsonSnapshot body){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("DELETE", asyncApiCall, body));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void delete(ApiCall.AsyncApiCall asyncApiCall , String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("DELETE", asyncApiCall,token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void delete(ApiCall.AsyncApiCall asyncApiCall, JsonSnapshot body , String token){
        try {
            URL url = new URL(apiUrl);
            apiCall = (new ApiCall("DELETE", asyncApiCall, body,token));apiCall.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

