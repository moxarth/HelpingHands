package com.example.helpinghands;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class BackgroundService extends Service {
    static int counter;
    static FirebaseFirestore db;

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
        if(counter == 0) {
            LocationManager locationManager1 = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (locationManager1.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && checkInternetStatus() && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LatLng location = locationfetch(getApplicationContext());
                user.setLatitude(Double.toString(location.latitude));
                user.setLongitude(Double.toString(location.longitude));
                db.collection("user_details").document(user.getUserid()).update("latitude", location.latitude, "longitude", location.longitude);
            }
            else{Log.v("BackgroundService","Something went wrong");}
            counter++;
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
