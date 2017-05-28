package com.dahai.yourprofile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.dahai.yourprofile.helper.OneProfile;
import com.dahai.yourprofile.models.DaoMaster;
import com.dahai.yourprofile.models.DaoSession;
import com.dahai.yourprofile.models.Profile;
import com.dahai.yourprofile.models.ProfileDao;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Long id = intent.getLongExtra("id", 0L);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "profile-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ProfileDao profileDao = daoSession.getProfileDao();

        Profile profile = profileDao.queryBuilder()
                .where(ProfileDao.Properties.Id.eq(id))
                .limit(1)
                .build().unique();
        if (profile != null) {
            OneProfile.changeAudioModel(context, profile.getAudioModel());

            try {
                OneProfile.playSound(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OneProfile.setNextAlarm(context);

        Toast.makeText(context, "情景模式已启用" + profile.getTitle(), Toast.LENGTH_LONG).show();
    }
}
