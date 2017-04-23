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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.text.DecimalFormat;

import static org.opencv.imgcodecs.Imgcodecs.imread;


public class Main extends Activity{

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CAMERA = 113;
    private static final int REQUEST_READ_STORAGE = 114;

    boolean canWrite = false;
    boolean useCamera = false;
    boolean canRead = false;

    private Messenger mService = null;
    private Intent deployAgent;
    boolean mBound = false;

    private static DecimalFormat fmt = new DecimalFormat("#.#");
    private Button btn;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWrite = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    Log.i("requests", "write request");
                }
                /** ****************************************************************/
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    useCamera = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    Log.i("requests", "camera request");
                }
                /** ****************************************************************/
            case REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canRead = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    Log.i("requests", "read request");
                }
                /** ****************************************************************/

        }
    }


//    public Mat CVTest()
//    {
//        String s = "jkl";
//        final Mat img = imread(s);
//
//        if (img.empty())
//        {
//            Log.i("OpenCVLoad", "img is null");
//            return null;
//        }
//        return img;
//    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCVLoad", "OpenCV loaded successfully");
                    // mOpenCvCameraView.enableView();
                    btn.setEnabled(true);
                    //CVTest();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        boolean hasReadPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasReadPermission){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
        }
        /** ****************************************************************/
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
/** ****************************************************************/
        boolean camPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!camPermission){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }


//        if (!OpenCVLoader.initDebug()) {
//            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
//        } else {
//            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }



        deployAgent = new Intent(this, HiddenAgent.class);
        // bindService(deployAgent, mConnection, Context.BIND_AUTO_CREATE);

        final Intent processImage = new Intent(this,Camera2Service.class);

        //startService(processImage);

        btn = (Button) findViewById(R.id.Start);
       // btn.setEnabled(false);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mBound) {
                    if (btn.getText().toString().equals("Start")) {  //Start

                      //  startActivity(new Intent(getApplicationContext(), Camera_Activity.class));

                        startService(processImage);

                        btn.setText("Stop");
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

                        btn.setText("Start");
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

    @Override
    protected void onResume() {
        //load libraries for CV
        //callback method
        //super.onResume();
       // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);

        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
//        } else {
//            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
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