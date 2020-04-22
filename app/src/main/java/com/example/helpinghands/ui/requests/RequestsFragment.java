package com.example.helpinghands.ui.requests;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.helpinghands.MainActivity;
import com.example.helpinghands.R;
import com.example.helpinghands.User;
import com.example.helpinghands.editprofile;
import com.example.helpinghands.login;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static java.lang.Thread.currentThread;

public class RequestsFragment extends Fragment{
    static Location mlocation;
    LocationManager locationManager;
    private RequestsViewModel requestsViewModel;
    static String hello = "hmmm";

    public void locationfetch(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){}

        mlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.v("MapLOG","ReqFrg Network: "+mlocation);
        Toast.makeText(getActivity(),"Last Location: "+mlocation,Toast.LENGTH_SHORT).show();
        if(mlocation == null){
            final ProgressDialog progressBar;
            progressBar = new ProgressDialog(getContext());
            progressBar.setCancelable(true);
            progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
            });
            progressBar.setMessage("Finding current location...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            Log.v("MapLOG","Location is null");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.v("MapLOG","Found in new: "+location.getLatitude()+", "+location.getLongitude());
                    Toast.makeText(getActivity(),location.getLatitude()+", "+location.getLongitude(),Toast.LENGTH_SHORT).show();
                    mlocation = location;
                }
                @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override public void onProviderEnabled(String provider) {}
                @Override public void onProviderDisabled(String provider) {}
            });
            int i=0;
            while(i<30){
                i++;
                if(mlocation != null){break;}
                else{Log.v("MapLOG","Trying my best: "+i);}
            }
            Log.v("MapLOG","Finally: "+mlocation);
            /*new CountDownTimer(5000,1000){
                @Override
                public void onFinish() {
                    progressBar.dismiss();
                    Log.v("MapLOG","In the end : "+mlocation);
                    if(mlocation == null){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("We could not find your location. Please try again.")
                                .setCancelable(false)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();
                                        Intent intent = new Intent(getActivity(),MainActivity.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else{
                    }
                }
                @Override
                public void onTick(long millisUntilFinished) {
                    if(mlocation != null){this.cancel();progressBar.dismiss();Log.v("MapLOG",""+mlocation);}
                    Log.v("MapLOG",""+(millisUntilFinished+1000)/1000);
                }
            }.start();*/
        }
        else {
                Log.v("MapLOG","Location = "+mlocation);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        requestsViewModel =
                ViewModelProviders.of(this).get(RequestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_requests, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){}

        mlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.v("MapLOG","ReqFrg Network: "+mlocation);
       // locationfetch();
        //Log.v("MapLOG","Outside the loop"+mlocation);
        return root;
    }




}