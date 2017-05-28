package com.dahai.yourprofile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class MyService extends Service {
    public MyService() {
//        Handler handler=new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            public void run() {
//                Toast.makeText(getApplicationContext(), "服务已创建", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
