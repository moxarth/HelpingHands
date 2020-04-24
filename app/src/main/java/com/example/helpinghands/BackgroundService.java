package com.example.helpinghands;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.helpinghands.R;

import java.util.Calendar;
import java.util.Date;

public class BackgroundService extends Service {
    static int counter;

    private void showNotification(String task) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_channel1";
        String channelName = "task_name1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Date currentTime = Calendar.getInstance().getTime();
        String time = currentTime.getHours()+" : "+currentTime.getMinutes();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(task)
                .setContentText(time)
                .setSmallIcon(R.drawable.ic_launcher_background);
        manager.notify(Integer.parseInt(currentTime.getMinutes()+""+currentTime.getSeconds()), builder.build());
    }

    @Override
    public void onCreate() {
        counter = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showNotification("WorkManager");
        if(counter == 0) {
            Thread t = new Thread() {
                public void run() {
                    while (true) {
                        showNotification("Helllo");
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
            counter++;
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.v("BackgroundService","Destroyed");
        counter--;
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
