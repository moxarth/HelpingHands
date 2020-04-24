package com.example.helpinghands;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BGDatabaseListenerService extends Service {
    static int counter;
    static User user;
    static FirebaseFirestore db;
    public static void notifyUser(Context context){

        Date currentTime = Calendar.getInstance().getTime();

        /*NavController nc = Navigation.findNavController(myactivity, R.id.nav_host_fragment);
        PendingIntent pendingIntent = nc.createDeepLink().setDestination(R.id.navigation_map).createPendingIntent();*/
        Intent notifyIntent = new Intent(context,MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);
        notifyIntent.putExtra("FragmentName","MapFrag");
        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(notifyIntent);
        PendingIntent notifyPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notify_001")
                .setSmallIcon(R.drawable.ic_helpinghands)
                .setContentTitle("Incoming Request")
                .setContentText("Someone within your area needs your help")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notifyPendingIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        nm.notify(0, builder.build());


        //nm.notify(001,builder.build());
        Log.v("BGData", "Inside Notify");
    }

    public static void listenRequests(final Context context){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("emergency_requests").whereEqualTo("lcity",user.getlcity()).whereEqualTo("Status","Active").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            notifyUser(context);
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }






    @Override
    public void onCreate() {
        counter = 0;
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
        user = new User(getApplicationContext());
    }



    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        notifyUser(getApplicationContext());
        //showNotification("WorkManager");
        if(counter == 0) {

            listenRequests(getApplicationContext());



            
            counter++;

        }
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        Log.v("BGListenerService","Destroyed");
        counter--;
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice1");
        broadcastIntent.setClass(this, Restarter1.class);
        this.sendBroadcast(broadcastIntent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
