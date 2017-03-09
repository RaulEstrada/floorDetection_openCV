package com.example.pvala.floor_detection_web_service;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;



public class Main extends Activity{

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CAMERA = 113;
    private static final int REQUEST_INTERNET = 114;
    private static final int REQUEST_WIFI = 115;

    boolean canWrite = false;
    boolean useCamera = false;
    boolean hasInternet = false;
    boolean connectWifi = false;


    private Messenger mService = null;
    private Intent deployAgent;
    boolean mBound = false;

    private static DecimalFormat fmt = new DecimalFormat("#.#");


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWrite = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                /** ****************************************************************/
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    useCamera = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                /** ****************************************************************/
            case REQUEST_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasInternet = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                /** ****************************************************************/
            case REQUEST_WIFI:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectWifi = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                /** ****************************************************************/


        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
/** ****************************************************************/
        boolean camPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!camPermission){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_WRITE_STORAGE);
        }
/** ****************************************************************/
        boolean internetPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        if (!internetPermission){
            requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_WRITE_STORAGE);
        }
/** ****************************************************************/

        deployAgent = new Intent(this, HiddenAgent.class);
        // bindService(deployAgent, mConnection, Context.BIND_AUTO_CREATE);

        final Intent processImage = new Intent(this,Camera2Service.class);

        //startService(processImage);

        final Button btn = (Button) findViewById(R.id.Start);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mBound) {
                    if (btn.getText().toString().equals("Start")) {  //Start

                      //  startActivity(new Intent(getApplicationContext(), Camera_Activity.class));

                        startService(processImage);

                        btn.setText(R.string.buttonClicked);
                        Message message = Message.obtain(null, HiddenAgent.Start);
                        message.replyTo = new Messenger(new ResponseHandler());
                        try {
                            mService.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

//                        startActivity(new Intent(getApplicationContext(), Camera_Activity.class));

                        /** Code for camera to start doing its thing goes here? */

                    } else if (btn.getText().toString().equals("Stop")) { //Stop

                        stopService(processImage);

                        btn.setText(R.string.buttonUnClicked);
                        Message message = Message.obtain(null, HiddenAgent.Stop);
                        message.replyTo = new Messenger(new ResponseHandler());
                        try {
                            mService.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else bindService(deployAgent, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case HiddenAgent.briefing:
                    Bundle incomingData = msg.getData();
                   // UpdateWidgets(incomingData);
                    break;
                case HiddenAgent.dismissed:
                    unbindService(mConnection);
                    mService = null;
                    mBound = false;
                    break;
                default: super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            mService = new Messenger(service);
            mBound = true;
            Toast.makeText(getApplicationContext(),"All set!", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

            Toast.makeText(getApplicationContext(),"Service Crashed", Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    public void onStop(){
        mBound = false;
        mService = null;
        unbindService(mConnection);
        super.onStop();
    }


}