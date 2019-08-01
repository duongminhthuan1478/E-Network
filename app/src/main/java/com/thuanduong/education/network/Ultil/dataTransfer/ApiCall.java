package com.thuanduong.education.network.Ultil.dataTransfer;


import android.os.AsyncTask;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import android.util.Log;


public class ApiCall extends AsyncTask<URL, Integer, String> {
    // respone time
    long milisecStart = 0 ;
    // respone
    int responeCode = 0;
    String message ="OK";
    //
    public boolean isRunning = false;
    // data result
    private String ApiData="";

    //interface
    AsyncApiCall asyncApiCall;

    // call api
    JsonSnapshot body;
    String requestMethod="",token="";
    final String authorizationKey = "authorization";

    public interface AsyncApiCall {
        void onSuccess(long resTime, JsonSnapshot resultJson);
        void onFail(int responeCode, String mess);
    }

    public ApiCall(String requestMethod,AsyncApiCall asyncApiCall) {
        this.requestMethod = requestMethod;
        this.asyncApiCall = asyncApiCall;
        body = new JsonSnapshot();
    }
    public ApiCall(String requestMethod,AsyncApiCall asyncApiCall,JsonSnapshot body) {
        this.requestMethod = requestMethod;
        this.asyncApiCall = asyncApiCall;
        this.body = body;
    }
    public ApiCall(String requestMethod,AsyncApiCall asyncApiCall,String token) {
        this.requestMethod = requestMethod;
        this.asyncApiCall = asyncApiCall;
        this.token = token;
        body = new JsonSnapshot();
    }
    public ApiCall(String requestMethod,AsyncApiCall asyncApiCall,JsonSnapshot body,String token) {
        this.requestMethod = requestMethod;
        this.asyncApiCall = asyncApiCall;
        this.body = body;
        this.token = token;
    }

    @Override
    protected String doInBackground(URL... urls) {
        isRunning = true;
        milisecStart = System.currentTimeMillis();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = urls[0];
            connection = (HttpURLConnection) url.openConnection();
            // header này tùy đứa đặt tên
            if(token.length()>0)
                connection.setRequestProperty(authorizationKey,token);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestMethod(requestMethod);
            submitBody(connection);
            connection.setConnectTimeout(5000);
            connection.connect();
            Log.d("api url",url.toString());

            // kiểm tra kết nối có fail hay k
            getResponeStatus(connection);

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }
            ApiData = buffer.toString();

            return buffer.toString();


        } catch (MalformedURLException e) {
            Log.d("api call error : ",e.getMessage());
        } catch (IOException e) {
            Log.d("api call error : ",e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.d("api url : ",e.getMessage());
            }
        }

        return ApiData;
    }

    protected void onProgressUpdate(Integer... progress) {
        // cập nhật progress tại đây
    }

    // Được gọi sau khi doInBackground kết thúc
    protected void onPostExecute(String lng) {
        isRunning = false;
        getResponseMess();
        long responeTime = System.currentTimeMillis() - milisecStart;
        if(responeCode < 200 || responeCode > 299 || ApiData.length()==0 ){
            asyncApiCall.onFail(responeCode,message);
        }else {
            asyncApiCall.onSuccess(responeTime ,new JsonSnapshot(ApiData));
        }
    }
    void submitBody(HttpURLConnection conn) throws IOException {
        if(body.isNotNull()) {
            String json = body.toString().replace((char)92+"","");

            conn.setRequestProperty("Content-Length", "" + json.getBytes().length);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
            Log.d("body api", json);
            writer.write(json);
            writer.close();
            wr.close();
        }
    }
    void getResponeStatus(HttpURLConnection connection) throws IOException {
        responeCode = connection.getResponseCode();
    }
    void getResponseMess(){
        if(responeCode == 0)
            message = "Connection : No network connection";
        else if(responeCode <200)
            message = "Informational : Communicates transfer protocol-level information.";
        else if(responeCode >299 && responeCode <= 399)
            message = "Redirection : Indicates that the client must take some additional action in order to complete their request.";
        else if(responeCode >399 && responeCode <= 499)
            message = "Client error : This category of error status codes points the finger at clients.";
        else if(responeCode >499 && responeCode <= 599)
            message = "Server Error : The server takes responsibility for these error status codes.";
    }
}
