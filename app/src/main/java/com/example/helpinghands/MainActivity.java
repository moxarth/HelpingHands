package com.example.helpinghands;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.helpinghands.ui.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.v("position","NewMethod Here");
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("FragmentName")){
                String str = extras.getString("FragmentName");
                Log.v("position","NewMethod" + str);
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //onNewIntent(getIntent());
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

        // get the data from intent
        String FragmentName = getIntent().getStringExtra("FragmentName");
        Log.v("position","First" + FragmentName);
        if (FragmentName != null) {
            Log.v("position","Inside");
            if (FragmentName.equals("MapFrag")) {
                Log.v("position","in changing room");
                /*Fragment fragment = new MapFragment();
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack("Name");
                fragmentTransaction.commit();*/
                /*NavController nc = Navigation.findNavController(this, R.id.nav_host_fragment);
                PendingIntent Pin = nc.createDeepLink().setDestination(R.id.navigation_map).createPendingIntent();
                try {
                    Pin.send();
                    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }*/

            }
        }

    }
}
