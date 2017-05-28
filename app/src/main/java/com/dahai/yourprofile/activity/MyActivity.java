package com.dahai.yourprofile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dahai.yourprofile.helper.BootComplete;
import com.dahai.yourprofile.R;
import com.dahai.yourprofile.helper.Common;
import com.dahai.yourprofile.helper.OneProfile;
import com.dahai.yourprofile.models.DaoMaster;
import com.dahai.yourprofile.models.DaoSession;
import com.dahai.yourprofile.models.Profile;
import com.dahai.yourprofile.models.ProfileDao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyActivity extends Activity {



    @Override
    protected void onResume() {
        super.onResume();

        Common.updateList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        setContentView(R.layout.activity_my);

        ProfileDao profileDao = Common.getProfileDao(this);

        ListView lv = (ListView)findViewById(R.id.lvProfile);

        Common.updateList(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyActivity.this, ProfileForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
//                new AlertDialog.Builder(MyActivity.this).setTitle("提示")
//                    .setMessage("确定删除?")
////                    .setIcon(Android.R.DR.drawable.)
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            setResult(RESULT_OK);//确定按钮事件
//                            Common.getProfileDao(MyActivity.this).deleteByKey(id);
//
//                            Common.updateList(MyActivity.this);
//                            OneProfile.setNextAlarm(MyActivity.this);
//
//                            Toast.makeText(MyActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            //取消按钮事件
////                            return false;
//                        }
//                    })
//                    .show();
//
//
//                return true;
//            }
//        });

        super.registerForContextMenu(lv);

        new BootComplete().showNotification(this);
        OneProfile.setNextAlarm(this);

        setOverflowShowingAlways();

        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClass(MyActivity.this, ProfileForm.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("操作");

        menu.add(Menu.NONE, Menu.FIRST, 1, "应用");
        menu.add(Menu.NONE, Menu.FIRST + 1, 2, "删除");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
//        getMenuInflater().inflate(R.menu.list_buttom, menu);

        return true;
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final long id = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;

        switch(item.getItemId()) {
            case Menu.FIRST:
                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "profile-db", null);
                SQLiteDatabase db = helper.getWritableDatabase();
                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession daoSession = daoMaster.newSession();
                ProfileDao profileDao = daoSession.getProfileDao();

                Profile profile = profileDao.queryBuilder()
                        .where(ProfileDao.Properties.Id.eq(id))
                        .limit(1)
                        .build().unique();
                if (profile != null) {
                    OneProfile.changeAudioModel(this, profile.getAudioModel());

                    try {
                        OneProfile.playSound(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(this, "情景模式已启用", Toast.LENGTH_LONG).show();

                break;

            case Menu.FIRST + 1:
                new AlertDialog.Builder(MyActivity.this).setTitle("提示")
                    .setMessage("确定删除?")
//                    .setIcon(Android.R.DR.drawable.)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);//确定按钮事件
                            Common.getProfileDao(MyActivity.this).deleteByKey(id);

                            Common.updateList(MyActivity.this);
                            OneProfile.setNextAlarm(MyActivity.this);

                            Toast.makeText(MyActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //取消按钮事件
//                            return false;
                        }
                    })
                    .show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(MyActivity.this, SettingActivity.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.list_add) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(MyActivity.this, ProfileForm.class);
            startActivity(intent);
        } else if (id == R.id.action_bug) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(MyActivity.this, BugActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(MyActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_exit) {
            this.finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
