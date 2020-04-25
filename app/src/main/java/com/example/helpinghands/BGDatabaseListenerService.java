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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;





public class BGDatabaseListenerService extends Service {
    static int counter;
    static User user;
    static Requestor requestor;
    static FirebaseFirestore db;



    static double toRadians(double angleIn10thofaDegree) {
        return (angleIn10thofaDegree * Math.PI) / 180;
    }

    static double distance(LatLng point1, LatLng point2){
        double lon1 = toRadians(point1.longitude);
        double lon2 = toRadians(point2.longitude);
        double lat1 = toRadians(point1.latitude);
        double lat2 = toRadians(point2.latitude);
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;
        return (c * r);
    }

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
                    "Incoming Request Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        nm.notify(Integer.parseInt(currentTime.getMinutes()+""+currentTime.getSeconds()), builder.build());
        Log.v("BGData", "Inside Notify");
    }

    public static void listenRequests(final Context context){
        Log.v("BGData","Log");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("emergency_requests").whereEqualTo("lcity",user.getlcity()).whereEqualTo("Status","Active").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.v("BGData","Log1");
                    return;
                }
                Log.v("BGData","Log1 Success");
                List<Requestor> temp = user.getRequestorList();
                List<Requestor> toRemove = new ArrayList<Requestor>();
                if (temp != null) {
                    for (Requestor requestor : temp ) {
                        Log.v("BGData","Checking to remove: " + requestor.getRequestId() + "CurrFlag: " + user.getCurrflag() + " Requestor Flag:" + requestor.flag);
                        if(user.getCurrflag() == requestor.flag || ( distance( new LatLng(Double.parseDouble(requestor.getLatitude()), Double.parseDouble(requestor.getLongitude()))  , new LatLng(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()))  ) > 2.5 ) ) {
                            Log.v("BGData","Removed "+ requestor.getRequestId());
                            toRemove.add(requestor);
                        }
                    }
                    for(Requestor requestor : toRemove){
                        temp.remove(requestor);
                    }
                    user.setRequestorList(temp);
                }
                for (QueryDocumentSnapshot dc : task.getResult()) {


                            int i;
                            String reqId = dc.getId();
                            List<Requestor> reqList = user.getRequestorList();
                            Log.v("BGData","Size when checking:" + reqList.size());
                            for(i = 0; i < reqList.size(); i++){
                                Requestor requestor = reqList.get(i);
                                if(requestor.getRequestId().equals(reqId)){
                                    Log.v("BGData","Checked In on: " + requestor.getRequestId());
                                    requestor.flag = user.getCurrflag();
                                    break;
                                }
                            }
                            user.setRequestorList(reqList);
                            if(i == user.getRequestorList().size()){
                                LatLng latLng = new LatLng(Double.parseDouble(dc.get("Latitude").toString()), Double.parseDouble(dc.get("Longitude").toString()));
                                if(distance(latLng, new LatLng(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()))) < 2.5){
                                    Requestor requestor = new Requestor();
                                    requestor.setRequestId(dc.getId());
                                    requestor.setUserId(dc.get("userId").toString());
                                    requestor.setLatitude(dc.get("Latitude").toString());
                                    requestor.setLongitude(dc.get("Longitude").toString());
                                    requestor.flag = user.getCurrflag();
                                    user.addToRequestorList(requestor);
                                    Log.v("BGData","Size after adding:" + user.getRequestorList().size());
                                    Log.v("BGData","Added" + user.getRequestorList().toString());
                                    if(!requestor.getUserId().equals(user.getUserid()))
                                        notifyUser(context);
                                }
                            }
                            //Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();




                }
                user.setCurrflag(!user.getCurrflag());
            }
        });
    }

    public boolean checkInternetStatus(){
        boolean status = false;
        try {
            final String command = "ping -c 1 google.com";
            status = (Runtime.getRuntime().exec(command).waitFor() == 0);
            Log.v(TAG, "Network Status: " + status);
        } catch (Exception e) {
            Log.e(TAG,"Network Error: "+e.toString());
        }
        return status;
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
        //notifyUser(getApplicationContext());
        //showNotification("WorkManager");
        if(counter == 0) {
            LocationManager locationManager1 = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (user.getFName() != "" && locationManager1.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && checkInternetStatus() && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                listenRequests(getApplicationContext());
            }


            
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
