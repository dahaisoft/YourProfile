package com.dahai.yourprofile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dahai.yourprofile.helper.BootComplete;
import com.dahai.yourprofile.helper.OneProfile;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        //通知栏
        new BootComplete().showNotification(context);

        //启动服务
//        Intent newIntent = new Intent(context, MyService.class);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(newIntent);

        //下一个情景
        OneProfile.setNextAlarm(context);

        //时间到时，执行PendingIntent，只执行一次
        //AlarmManager.RTC_WAKEUP休眠时会运行，如果是AlarmManager.RTC,在休眠时不会运行
        //am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 10000, pi);
        //如果需要重复执行，使用上面一行的setRepeating方法，倒数第二参数为间隔时间,单位为毫秒

        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
