package com.example.helpinghands;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.helpinghands.ui.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String FragmentName = getIntent().getStringExtra("FragmentName");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (FragmentName != null) {

            if (FragmentName.equals("MapFrag")) {
                Log.v("position","in changing room");
                NavController nc = Navigation.findNavController(this, R.id.nav_host_fragment);
                PendingIntent Pin = nc.createDeepLink().setDestination(R.id.navigation_map).createPendingIntent();
                try {
                    Pin.send();
                    this.overridePendingTransition(0,0);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_requests, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        final WorkManager mWorkManager = WorkManager.getInstance();
        final PeriodicWorkRequest mRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class,5, TimeUnit.MINUTES).build();
        mWorkManager.getInstance(getApplicationContext()).enqueue(mRequest);
    }
}
