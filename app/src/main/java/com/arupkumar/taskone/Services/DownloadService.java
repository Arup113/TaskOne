package com.arupkumar.taskone.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.arupkumar.taskone.Activities.MainActivity;
import com.arupkumar.taskone.R;
import com.arupkumar.taskone.Extras.Constants;
import com.arupkumar.taskone.Utils.ShowLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.arupkumar.taskone.Extras.Constants.IS_SERVICE_RUNNING;
import static com.arupkumar.taskone.Extras.Constants.NOTIFICATION;
import static com.arupkumar.taskone.Extras.Constants.NOTIFICATIONID.FOREGROUND_SERVICE;
import static com.arupkumar.taskone.Extras.Constants.PERCENTAGE;
import static com.arupkumar.taskone.Extras.Constants.RESULTCODE;

public class DownloadService extends Service {
    private static final String LOG_TAG = DownloadService.class.getName().toString();
    private int result = Activity.RESULT_CANCELED;
    private AsyncTask mMyTask;

    @Override
    public void onCreate() {
        ShowLog.e(LOG_TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();
            IS_SERVICE_RUNNING = true;
            String url = intent.getStringExtra("url");
            mMyTask = new TestAsync().execute(url);
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            IS_SERVICE_RUNNING = false;
            stopForeground(true);
            stopSelf();
            mMyTask.cancel(true);
        }
        return START_STICKY;
    }


    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("File Downloading...")
                .setTicker("File Downloading...")
                .setContentText("Your file is downloading. Please Wait.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(FOREGROUND_SERVICE, notification);

    }

    class TestAsync extends AsyncTask<String, Integer, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... arg0) {

            int count;
            String fileName = arg0[0].substring(arg0[0].lastIndexOf('/') + 1);
            File output = new File(Environment.getExternalStorageDirectory(), fileName);
            if (output.exists()) {
                output.delete();
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                URL url = new URL(arg0[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                inputStream = new BufferedInputStream(url.openStream());
                outputStream = new FileOutputStream(output.getPath());
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    outputStream.write(data, 0, count);

                    // If the AsyncTask cancelled
                    if (isCancelled()) {
                        break;
                    }
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



            return null;
        }

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            // successfully finished
            result = Activity.RESULT_OK;
            ShowLog.e("Count", String.valueOf(a[0]));
            publishResults(a[0], result);
        }

        protected void onPostExecute(String result) {
            IS_SERVICE_RUNNING = false;
            stopForeground(true);
            stopSelf();
            super.onPostExecute(result);
            ShowLog.e(LOG_TAG, "onPostExecute");
        }
    }

    private void publishResults(int percentage, int resultCode) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(PERCENTAGE, percentage);
        intent.putExtra(RESULTCODE, resultCode);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShowLog.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }
}