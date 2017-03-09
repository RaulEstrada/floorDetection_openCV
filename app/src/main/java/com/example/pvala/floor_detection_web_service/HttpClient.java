package com.example.pvala.floor_detection_web_service;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by yuengdelahoz on 11/13/16.
 * This class was created to send json-formated strings with the holder/user information to the livoxStats server (Django server).
 *
 */

public class HttpClient {
    private final String DEBUG_TAG = "HttpClient";
//    public static enum REQUEST {HOLDER, USER};


    private Context context = null;

    public boolean UpdateRemote(String myurl, String data, Context context) throws IOException {  //took out [] from data
        InputStream instream = null;
        OutputStreamWriter outstream = null;
        this.context = context;
        int response_code = 0;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("content-type","application/json; charset=utf-8");

            // Starts the query
            conn.connect();

//           Get output stream to send json file
            outstream = new OutputStreamWriter(conn.getOutputStream());

            String jsonfile = "";
            jsonfile = generateHolderJSONString(data);
            write(outstream,jsonfile);

            String response_msg = conn.getResponseMessage();
            response_code = conn.getResponseCode();
            Log.d(DEBUG_TAG,"response code "+ response_code);

            Log.d(DEBUG_TAG,""+response_msg);

            // Makes sure that all streams are closed after the app is
            // finished using it.
        } finally {
            if (instream != null) {
                instream.close();
            }
            if (outstream !=null){
                outstream.close();
            }
        }
        return response_code>=200 && response_code <300? true: false;
    }

    private String generateHolderJSONString(String data) {    //took out [] from data
        /** As the name implies, this method reads the information in data about the holder and creates a Json-formated string.
         * This is the string that is sent to the django server
         *  */

        final long timestamp = System.currentTimeMillis();  //creates a timestamp, and then converts it to a string
        String fileName = String.format("%d.jpg", timestamp);
        String pic = data;    //hold data from pic    //took out [] from data

        Log.d(DEBUG_TAG, "ref 1 ");
        String device_name = android.os.Build.MODEL;

        Log.d(DEBUG_TAG, "device name: " + device_name);


        JSONObject json = new JSONObject();

        try {
            json.put("name",fileName);
            json.put("pic",pic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }


    void write(OutputStreamWriter wr, String jsonFile) throws IOException {
        wr.write(jsonFile);
        wr.flush();
    }
}
