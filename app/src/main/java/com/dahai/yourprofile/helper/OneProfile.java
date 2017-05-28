package com.dahai.yourprofile.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.dahai.yourprofile.receiver.AlarmReceiver;
import com.dahai.yourprofile.models.Profile;
import com.dahai.yourprofile.models.ProfileDao;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 龙海威 on 2014/8/6.
 */
public class OneProfile {
    private static void addAlarm(Context context, Profile nextProfile, int day) {
        Calendar c = Calendar.getInstance();

        //有情景
        if (nextProfile != null) {
            c.add(Calendar.DATE, day);
            c.set(Calendar.HOUR_OF_DAY, nextProfile.getStartHour());
            c.set(Calendar.MINUTE, nextProfile.getStartMinute());
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            Intent localIntent = new Intent(context, AlarmReceiver.class);
            localIntent.putExtra("id", nextProfile.getId());

            PendingIntent pi=PendingIntent.getBroadcast(context, 0, localIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//            am.cancel(pi);
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

            //更新通知栏
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] dayList = dfs.getShortWeekdays();

            String title = "下次情景为：" + String.valueOf(dayList[c.get(Calendar.DAY_OF_WEEK)])
                    + " " + String.valueOf(c.get(Calendar.HOUR_OF_DAY)) + "点"
                    + " " +String.valueOf(c.get(Calendar.MINUTE)) + "分";
            new BootComplete().setTitle(title).showNotification(context);
        } else
            new BootComplete().showNotification(context);
    }

    public static void setNextAlarm(Context context) {
        Calendar c = Calendar.getInstance();

        ProfileDao profileDao = Common.getProfileDao(context);

        List rs = profileDao.queryBuilder()
                .where(ProfileDao.Properties.Enabled.eq(1))
                .orderAsc(ProfileDao.Properties.StartTotalMinute)
                .build().list();

        Profile nextProfile = null, addProfile = null;

        DaysOfWeek daysOfWeek = new DaysOfWeek(0);

        int addDays = 0, tmpAddDays = 0, tmpStartTotalMinute = 0, j = 0, k = 0, nextStartTotalMinute = 0;
        for (int i = 0; i < rs.size(); i++) {
            nextProfile = (Profile)rs.get(i);

            daysOfWeek.setBitSet(nextProfile.getDaysOfWeek() == null ? 0 : nextProfile.getDaysOfWeek());

            tmpAddDays = daysOfWeek.calculateDaysToNextAlarm(c);

            if (tmpAddDays == 0) {
                if (nextProfile.getStartTotalMinute() <= (c.get(Calendar.HOUR_OF_DAY) * 60 +c.get(Calendar.MINUTE))) {
                    //计算再下一次
                    boolean[] checkedItems = new boolean[7];

                    for (int l = 0; l < 7; l++) {
                        checkedItems[l] = ((daysOfWeek.getBitSet() & (1 << l)) > 0);
                    }

                    if (c.get(Calendar.DAY_OF_WEEK) - 2 < 0)
                        checkedItems[6] = false;
                    else
                        checkedItems[c.get(Calendar.DAY_OF_WEEK) - 2] = false;

                    for (int l = 0; l < checkedItems.length; l++) {
                        if (checkedItems[l])
                            daysOfWeek.setDaysOfWeek(true, l+2);
                        else
                            daysOfWeek.setDaysOfWeek(false, l+2);
                    }

                    tmpAddDays = daysOfWeek.calculateDaysToNextAlarm(c);

                    nextStartTotalMinute = tmpAddDays * 24 * 60 + nextProfile.getStartTotalMinute();
                } else {
                    nextStartTotalMinute = nextProfile.getStartTotalMinute();
                }
            } else if (tmpAddDays >= 1) {
                nextStartTotalMinute = tmpAddDays * 24 * 60 + nextProfile.getStartTotalMinute();
            }

            if (nextStartTotalMinute > (c.get(Calendar.HOUR_OF_DAY) * 60 +c.get(Calendar.MINUTE)) ) {
                if (j == 0)
                    tmpStartTotalMinute = nextStartTotalMinute;

                if (nextStartTotalMinute <= tmpStartTotalMinute) {
                    j++;
                    tmpStartTotalMinute = nextStartTotalMinute;
                    addProfile = nextProfile;
                }
            }
        }

        addAlarm(context, addProfile, nextStartTotalMinute / 24 / 60);
    }

    public static void playSound(Context context) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.dahai.yourprofile_preferences", Context.MODE_PRIVATE);
        String uri = sharedPreferences.getString("play_sound", "");

        Uri alert = Uri.parse(uri);

        //创建media player
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, alert);
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }

    }

    public static void changeAudioModel(Context context, int ringModel) {
        try {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            if (am != null && am.getRingerMode() != ringModel) {
                am.setRingerMode(ringModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeWIFI() {}

    public static void changeMobileData() {}

    public static void changeGPS() {}

    public static void changeAirplaneMode() {}
}
