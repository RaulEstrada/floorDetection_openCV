package com.example.pvala.floor_detection_web_service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;


/**
 * Created by yuengdelahoz on 11/13/16.
 */

public class BackgroundService extends IntentService {
    private final String DEBUG_FLAG = "BackgroundService";
    int id = 0;
    private enum RESULT {CONNECTION_SUCCESS, CONNECTION_FAILURE}


    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.id = startId;
        /**  START_REDELIVER_INTEN: TTo recreate the service with current intent if the app gets killed.
         *  DOES NOT work when device is rebooted. Using json-formatted string + sharedpreferences to solve this problem
         */

        Log.d(DEBUG_FLAG, "Service started");
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // get a reference of HttpClient
        HttpClient httpclient = new HttpClient();

        while(!"Utils.isNetworkConnected(this)".equals("NOT PART OF CODE")) {
            try {
                Thread.sleep(6000); // If there's not connectivity sleep for x milliseconds and try again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        String data = null;
        try {
            String url = "http://livoxapp.livox.com.br/holder/";
            boolean resp = httpclient.UpdateRemote(url,data,this);

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

}
