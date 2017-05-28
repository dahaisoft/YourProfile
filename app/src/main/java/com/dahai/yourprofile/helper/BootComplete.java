package com.dahai.yourprofile.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dahai.yourprofile.R;
import com.dahai.yourprofile.activity.MyActivity;

/**
 * Created by 龙海威 on 2014/8/4.
 */
public class BootComplete {
    public String title = "智能情景正在后台运行";

    public void showNotification(Context context) {
        NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        Intent localIntent = new Intent(context,MyActivity.class);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIndent = PendingIntent.getActivity(context, 0, localIntent, 0);
        builder . setContentIntent(contentIndent) .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))
                .setTicker("智能情景启动中")
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setContentTitle("智能情景")
                .setContentText(title);
        nm.notify(R.string.app_name, builder.build());
    }

    public BootComplete setTitle(String title) {
        this.title = title;

        return this;
    }
}
