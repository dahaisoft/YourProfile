package com.dahai.yourprofile.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dahai.yourprofile.R;
import com.dahai.yourprofile.helper.Common;
import com.dahai.yourprofile.helper.DaysOfWeek;
import com.dahai.yourprofile.helper.OneProfile;
import com.dahai.yourprofile.models.Profile;
import com.dahai.yourprofile.models.ProfileDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileForm extends ListActivity {

    private Profile profile;

    private EditText mEditText;

    private Long profileID = 0L;

    ProfileDao profileDao;

    boolean[] _selections =  new boolean[ 7 ];

    DaysOfWeek daysOfWeek = new DaysOfWeek(0);

    boolean[] checkedItems = new boolean[7];

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            //保存数据
            if (profile.getTitle() == "" || profile.getTitle() == null)
                profile.setTitle("未命名");
            profile.setStartTotalMinute((short)(profile.getStartHour() * 60 + profile.getStartMinute()));

            profile.setDaysOfWeek(daysOfWeek.getBitSet());

            if (profileID > 0)
                profileDao.update(profile);
            else {
                profile.setEnabled(true);

                profileDao.insert(profile);
            }

            finish();
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

            OneProfile.setNextAlarm(this);

            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_form);

        ActionBar actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        profileDao = Common.getProfileDao(this);

        profileID =  getIntent().getLongExtra("id", 0L);
        if ( profileID > 0) {
            profile = profileDao.queryBuilder()
                    .where(ProfileDao.Properties.Id.eq(profileID))
                    .limit(1)
                    .build().unique();

            daysOfWeek.setBitSet(profile.getDaysOfWeek() == null ? 0 : profile.getDaysOfWeek());
        } else {
            profile = new Profile();
        }

        SimpleAdapter sa = new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_2, new String[]{"title","summary"}, new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(sa);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        HashMap<String, Object> data = (HashMap<String, Object>)l.getItemAtPosition(position);

        if (data.get("type") =="edittext") {
            mEditText =  new EditText(this);
            mEditText.setText(profile.getTitle());

            DialogInterface.OnClickListener dialogInterface =  new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    profile.setTitle(mEditText.getText().toString());

                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this).setTitle("请输入").setView(mEditText)
                        .setPositiveButton("确定", dialogInterface)
                         .setNegativeButton("取消", null)
                        .show();
        } else if (data.get("type") =="timepicker") {
            int mHour = 0, mMinute = 0;

            if (profileID > 0) {
                mHour = profile.getStartHour();
                mMinute = profile.getStartMinute();
            } else {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
            }

            new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    Calendar c = Calendar.getInstance();

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {

                        profile.setStartHour((short) hourOfDay);
                        profile.setStartMinute((short) minute);
                    }
                }, mHour, mMinute, true).show();
        } else if (data.get("type") =="checkbox") {
            DialogInterface.OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i])
                            daysOfWeek.setDaysOfWeek(true, i+2);
                        else
                            daysOfWeek.setDaysOfWeek(false, i+2);
                    }
                }
            };

            DialogInterface.OnMultiChoiceClickListener checkboxClickListener = new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    _selections[which] = isChecked;
                }
            };

            for (int i = 0; i < 7; i++) {
                checkedItems[i] = ((daysOfWeek.getBitSet() & (1 << i)) > 0);
            }

            new AlertDialog.Builder(this).setTitle("重复")
                    .setMultiChoiceItems(R.array.daysOfWeek, checkedItems, checkboxClickListener)
                    .setPositiveButton("确定", positiveClickListener)
                    .setNegativeButton("取消", null).show();

        } else if (data.get("type") =="radiogroup") {
            DialogInterface.OnClickListener dialogInterface =  new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    profile.setAudioModel(which);

                    dialog.dismiss();
                }
            };

            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileForm.this);
            AlertDialog show = dialog.setTitle("请选择")
                    .setSingleChoiceItems(R.array.audioModel, profileID > 0 ? profile.getAudioModel() : 0, dialogInterface)
                    .setNegativeButton("取消", null)
                    .show();

        }

    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "情景名称");
        map.put("type", "edittext");
        map.put("summary", profileID > 0 ? profile.getTitle() : "");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "时间");
        map.put("type", "timepicker");
        map.put("summary", profileID > 0 ? profile.getStartHour() + ":" + profile.getStartMinute() : "");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "重复");
        map.put("type", "checkbox");

        String summary = "";
        if (profileID > 0) {
            summary = new DaysOfWeek(profile.getDaysOfWeek()).toString(true);
            if (summary == "abc")
                summary = "每天";
        }
        map.put("summary", summary);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "铃声");
        map.put("type", "radiogroup");
        map.put("summary", profileID > 0 ? getResources().getStringArray(R.array.audioModel)[profile.getAudioModel()] : "");
        list.add(map);

        return list;
    }
}
