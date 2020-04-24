package com.example.helpinghands;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Restarter", "Service Tried to stop");
        //Toast.makeText(context,"Service Restarted", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Restarter", "Starting in foreground");
            context.startForegroundService(new Intent(context, BackgroundService.class));
        } else {
            Log.v("Restarter", "Starting in background");
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}
