package com.arupkumar.taskone.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arupkumar.taskone.Extras.Constants;
import com.arupkumar.taskone.R;
import com.arupkumar.taskone.Services.DownloadService;
import com.arupkumar.taskone.Utils.CommonUtils;
import com.arupkumar.taskone.Utils.ShowLog;

import static com.arupkumar.taskone.Extras.Constants.IS_SERVICE_RUNNING;
import static com.arupkumar.taskone.Extras.Constants.NOTIFICATION;
import static com.arupkumar.taskone.Extras.Constants.NOTIFICATION_ID;
import static com.arupkumar.taskone.Extras.Constants.PERCENTAGE;
import static com.arupkumar.taskone.Extras.Constants.REQUEST_ID_FOR_STORAGE_WRITE_PERMISSIONS;
import static com.arupkumar.taskone.Extras.Constants.RESULTCODE;


/**
 * Created by Arup Kumar Pramanik on 04/10/2018.
 */

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName().toString();
    private Activity mActivity;
    private Context mContext;
    private NotificationManager manager;

    // UI references.
    private TextView activity_main_tv_status;
    private ProgressBar activity_main_pb_progress;
    private Button activity_main_btn_start_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setContentView() {
        setContentView(R.layout.activity_main);
        mActivity = this;
        mContext = getApplicationContext();
    }

    @Override
    void setupActionBar() {

    }


    @Override
    void initializeEditTextComponents() {

    }

    @Override
    void initializeButtonComponents() {
        activity_main_btn_start_download = findViewById(R.id.activity_main_btn_start_download);
    }

    @Override
    void initializeTextViewComponents() {
        activity_main_tv_status = findViewById(R.id.activity_main_tv_status);
    }

    @Override
    void initializeImageViewComponents() {

    }

    @Override
    void initializeOtherViewComponents() {
        activity_main_pb_progress = findViewById(R.id.activity_main_pb_progress);
    }

    @Override
    void initializeViewComponentsEventListeners() {
        activity_main_btn_start_download.setOnClickListener(this);

    }

    @Override
    void removeViewComponentsEventListeners() {
        activity_main_btn_start_download.setOnClickListener(null);
    }

    @Override
    void exitThisWithAnimation() {
        finish();
        overridePendingTransition(R.anim.trans_right_in,
                R.anim.trans_right_out);
    }

    @Override
    void startNextWithAnimation(Intent intent, boolean isFinish) {
        if (isFinish) {
            finish();
        }
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_btn_start_download:
                if (isWriteInStorageAllowed()) {
                    startService();
                } else {
                    //If the app has not the permission then asking for the permission
                    requestWriteInStoragePermission(REQUEST_ID_FOR_STORAGE_WRITE_PERMISSIONS);
                }
                break;
        }
    }

    private void startService() {
        if (CommonUtils.isNetworkConnected(mContext)) {
            Intent intent = new Intent(mActivity, DownloadService.class);
            intent.putExtra("url", getResources().getString(R.string.download_url));
            if (IS_SERVICE_RUNNING) {
                ShowLog.e("Activity", "Service Is Running");
                intent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                IS_SERVICE_RUNNING = false;
                changeText();
                try {
                    unregisterReceiver(receiver);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                activity_main_pb_progress.setProgress(0);
                activity_main_tv_status.setText("");
            } else {
                ShowLog.e("Activity", "Service Not Running");
                intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                IS_SERVICE_RUNNING = true;
                changeText();
                try{
                    registerReceiver(receiver, new IntentFilter(NOTIFICATION));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            startService(intent);
        } else {
            Toast.makeText(mActivity, R.string.connect_to_internet, Toast.LENGTH_LONG).show();
        }

    }

    private void changeText() {
        if (IS_SERVICE_RUNNING) {
            activity_main_tv_status.setText(R.string.status_downloading);
            activity_main_btn_start_download.setText(R.string.stop_service);
        } else {
            activity_main_tv_status.setText(R.string.status_completed);
            activity_main_btn_start_download.setText(R.string.start_service);

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int percentage = bundle.getInt(PERCENTAGE);
                int resultCode = bundle.getInt(RESULTCODE);
                if (resultCode == RESULT_OK) {
                    activity_main_pb_progress.setProgress(percentage);
                    if (percentage == 100) {
                        changeText();
                        try {
                            if (manager == null) {
                                addNotification();
                                manager.cancel(NOTIFICATION_ID);
                            } else {
                                manager.cancel(NOTIFICATION_ID);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    Log.e(TAG, "Download failed");
                    changeText();
                    try {
                        if (manager == null) {
                            addNotification();
                            manager.cancel(NOTIFICATION_ID);
                        } else {
                            manager.cancel(NOTIFICATION_ID);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try{
            registerReceiver(receiver, new IntentFilter(NOTIFICATION));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (IS_SERVICE_RUNNING) {
            changeText();
            if (manager == null) {
                addNotification();
                manager.cancel(NOTIFICATION_ID);
            } else {
                manager.cancel(NOTIFICATION_ID);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (IS_SERVICE_RUNNING) {
            addNotification();
        }
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Downloading...")
                        .setTicker("Downloading...")
                        .setContentText("Your file is downloading from background...")
                        .setOngoing(true);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_FOR_STORAGE_WRITE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService();
            }
        }
    }

    private boolean isWriteInStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestWriteInStoragePermission(int REQUEST_ID) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //if asked ny second time

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
    }
}
