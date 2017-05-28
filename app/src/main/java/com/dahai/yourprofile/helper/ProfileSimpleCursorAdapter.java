package com.dahai.yourprofile.helper;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;

import com.dahai.yourprofile.R;
import com.dahai.yourprofile.helper.Common;
import com.dahai.yourprofile.helper.OneProfile;
import com.dahai.yourprofile.models.Profile;
import com.dahai.yourprofile.models.ProfileDao;

/**
 * Created by Administrator on 2014/8/19.
 */
public class ProfileSimpleCursorAdapter extends SimpleCursorAdapter {
//    public Context mContext;

    public ProfileSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);

        ((Switch)v.findViewById(R.id.swEnabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ProfileDao profileDao = Common.getProfileDao(v.getContext());

                Cursor cursor = getCursor();
                cursor.moveToPosition(position);
                int id = cursor.getInt(cursor.getColumnIndex(ProfileDao.Properties.Id.columnName));

                Profile profile = profileDao.queryBuilder()
                        .where(ProfileDao.Properties.Id.eq(id))
                        .limit(1)
                        .build().unique();
                profile.setEnabled(isChecked);
                profileDao.update(profile);

                OneProfile.setNextAlarm(v.getContext());
            }
        });

        return v;
    }
}
