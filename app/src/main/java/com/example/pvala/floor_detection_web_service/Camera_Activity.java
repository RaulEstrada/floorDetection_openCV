package com.example.pvala.floor_detection_web_service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Camera_Activity extends Activity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CAMERA = 113;

    boolean canWrite = false;
    boolean useCamera = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWrite = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    useCamera = true;
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("state", "onCreate from camera activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Button btn = (Button) findViewById(R.id.button);
        //if(isMyServiceRunning(Camera2Service.class)) btn.setText("Stop");
        final Intent processImage = new Intent(this,Camera2Service.class);

        startService(processImage);

//        boolean camPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
//        if (!camPermission){
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_WRITE_STORAGE);
//        }


//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Button bt = (Button) v;
//                if (bt.getText().toString().equals("Start")) {
//                    startService(processImage);
//                    bt.setText("Stop");
//                }
//                else {
//                    stopService(processImage);
//                    bt.setText("Start");
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   stopService(processImage);
    }


    /** ADDED*/
    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
