package com.example.helpinghands;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.helpinghands.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.lang.Thread.sleep;

public class BackgroundService extends Service {
    static int counter;
    static FirebaseFirestore db;
    public LatLng locationfetch(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        final Location currentLoc;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        currentLoc = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        Log.v(TAG,"Last known location(NETWORK): " + currentLoc);
        if(currentLoc == null){ return  new LatLng(0,0); }
        else{return new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());}
    }

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
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        User user = new User(getApplicationContext());
        //showNotification("WorkManager");
        if(counter == 0) {


            LatLng location = locationfetch(getApplicationContext());


            db.collection("user_details").document(user.getUserid()).update("latitude", location.latitude, "longitude", location.longitude);
            //showNotification("Start Location Upload" + location.toString());
            counter++;
            /*try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.v("Restarter","Exception");
            }*/

        }
        return Service.START_REDELIVER_INTENT;
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
