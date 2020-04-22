package com.example.helpinghands;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Date;

public class BackgroundWorker extends Worker {


    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification("WorkManager");
        Log.v("Background","Timepass");
        return Result.success();
    }

    private void showNotification(String task) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_channel";
        String channelName = "task_name";
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
                .setSmallIcon(R.drawable.ic_helpinghands);
        manager.notify(Integer.parseInt(currentTime.getMinutes()+""+currentTime.getSeconds()), builder.build());
    }
}
