package com.example.helpinghands;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.helpinghands.R;

import java.util.Calendar;
import java.util.Date;

public class BackgroundService extends Service {

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showNotification("WorkManager");
        Thread t = new Thread(){
            public void run(){
                while(true){
                    showNotification("Helllo");
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }};
        t.start();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
