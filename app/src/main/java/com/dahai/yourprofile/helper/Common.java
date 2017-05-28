package com.dahai.yourprofile.helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.dahai.yourprofile.R;
import com.dahai.yourprofile.models.DaoMaster;
import com.dahai.yourprofile.models.DaoSession;
import com.dahai.yourprofile.models.ProfileDao;

/**
 * Created by Administrator on 2014/8/19.
 */
public class Common {
    private static SQLiteDatabase db;

    private static Cursor cursor;

    private static ProfileDao profileDao = null;

    private static SimpleCursorAdapter adapter;

    public static ProfileDao getProfileDao (Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "profile-db", null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getProfileDao();
    }

    public static void updateList(Activity activity) {
        if (profileDao == null)
            profileDao = getProfileDao(activity.getBaseContext());

        cursor = db.query(profileDao.getTablename(), profileDao.getAllColumns(), null, null, null, null, ProfileDao.Properties.StartTotalMinute.columnName + " asc");

        String[] from = {ProfileDao.Properties.Title.columnName, ProfileDao.Properties.StartHour.columnName
                , ProfileDao.Properties.StartMinute.columnName, ProfileDao.Properties.DaysOfWeek.columnName, ProfileDao.Properties.Enabled.columnName};
        int[] to = {R.id.title, R.id.startHour, R.id.startMinute, R.id.daysOfWeek, R.id.swEnabled};

        adapter = new ProfileSimpleCursorAdapter(activity, R.layout.list_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, final Cursor cursor, int columnIndex) {
                if (cursor.getColumnIndex(ProfileDao.Properties.StartHour.columnName) == columnIndex && cursor.getString(columnIndex).length() < 2) {
                    ((TextView)view.findViewById(R.id.startHour)).setText("0" + cursor.getString(columnIndex));

                    return true;
                }

                if (cursor.getColumnIndex(ProfileDao.Properties.StartMinute.columnName) == columnIndex && cursor.getString(columnIndex).length() < 2) {
                    ((TextView)view.findViewById(R.id.startMinute)).setText("0" + cursor.getString(columnIndex));

                    return true;
                }

                if (cursor.getColumnIndex(ProfileDao.Properties.DaysOfWeek.columnName) == columnIndex) {
                    String summary = "";
                    summary = new DaysOfWeek(cursor.getInt(columnIndex)).toString(true);
                    if (summary == "abc")
                        summary = "每天";

                    ((TextView)view.findViewById(R.id.daysOfWeek)).setText(summary);

                    return true;
                }

                if (cursor.getColumnIndex(ProfileDao.Properties.Enabled.columnName) == columnIndex) {
                    ((Switch)view.findViewById(R.id.swEnabled)).setChecked( cursor.getInt(columnIndex) > 0 ? true : false);

                    return true;
                }

                return false;
            }
        });

        ListView lv = (ListView)(activity.findViewById(R.id.lvProfile));
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
