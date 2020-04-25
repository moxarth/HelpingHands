package com.example.helpinghands.ui.home;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.helpinghands.BGDatabaseListenerService;
import com.example.helpinghands.BackgroundService;
import com.example.helpinghands.MainActivity;
import com.example.helpinghands.R;
import com.example.helpinghands.User;
import com.example.helpinghands.emergencycontacts;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ImageButton call100;
    private ImageButton call108;
    private ImageButton call101;
    private ImageButton test;
    private ToggleButton sosalert;
    int cntflag = 0;
    static  LatLng cur_position;
    LocationManager locationManager;
    private static final String TAG = "HomeLOG";

    static FirebaseFirestore db;
    static Address address;
    static User user;

    public LatLng locationfetch(){
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        final Location currentLoc;
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        }
        currentLoc = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        Log.v(TAG,"Last known location(NETWORK): " + currentLoc);
        if(currentLoc == null){ return  new LatLng(0,0); }
        else{return new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());}
    }

    public void setUserLocation(User myuser,LatLng my_position){
        Log.v(TAG,"Setting User location: "+my_position);
        myuser.setLatitude(Double.toString(my_position.latitude));
        myuser.setLongitude(Double.toString(my_position.longitude));
        db.collection("user_details").document(myuser.getUserid()).update("latitude",my_position.latitude, "longitude",my_position.longitude).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG,"ERROR UPDATING LOCATION IN DATABASE");
                Intent in = new Intent(getActivity(), MainActivity.class);
                startActivity(in);
            }
        });
    }

    public Address findLocality(LatLng my_position){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        Address myaddress = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    my_position.latitude, my_position.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                myaddress = addressList.get(0);
                Log.v(TAG,"Finding Locality: "+myaddress.getLocality());
            }
        } catch (IOException e) {
            myaddress = null;
            Log.e(TAG, "Unable to connect to Geocoder(locality error)", e);
        }
        return myaddress;
    }

    public void updateLocality(User myuser,Address myaddress){
        myuser.setlcity(myaddress.getLocality());
        Log.v(TAG,"Setting Locality: "+myaddress.getLocality());
        db.collection("user_details").document(myuser.getUserid()).update("lcity",myaddress.getLocality());
    }

    public void GPS_DisableAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("It seems GPS is disabled. You need to enable GPS location to access map")
                .setCancelable(false)
                .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

    public void Internet_DisableAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Internet Connection is required to perform the following task.");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 122: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("request_status","accepted");
                    sosalert.setChecked(false);
                    Toast.makeText(getActivity(),"You can trigger emergency alert now.",Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("request_status","rejected");
                    sosalert.setChecked(false);
                }
                return;
            }

            case 120: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("request_status","accepted");
                    call100.callOnClick();
                } else {
                    Log.v("request_status","rejected");
                }
                return;
            }

            case 124: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("request_status","accepted");
                    call108.callOnClick();
                } else {
                    Log.v("request_status","rejected");
                }
                return;
            }

            case 125: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("request_status","accepted");
                    call101.callOnClick();
                } else {
                    Log.v("request_status","rejected");
                }
                return;
            }
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Location permission granted");
                    locationfetch();
                    Intent in = new Intent(getContext(),MainActivity.class);
                    startActivity(in);
                    ((Activity)getContext()).overridePendingTransition(0,0);
                } else {
                    Log.v(TAG,"Location permission rejected");
                }
                return;
            }
        }
    }

    public void sendsms(String number,String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
    }

    public void alert_contact(User user) {
        int flag = 0;
        String message = "Emergency Alert\n"+user.getFName()+" "+user.getLName()+" has just triggered an emergency call.\n"+user.getFName()+" has listed you as an emergency contact.";
        Log.v("Message",message);
        String lastloc = "";
        if(user.getLatitude() != "" && user.getLongitude() != ""){
            flag = 1;
            //https://www.google.com/maps/@22.683591,72.880947,18z
            lastloc = "Last Known Location is in this area :\n";
            lastloc =lastloc + "https://www.google.com/maps/@"+user.getLatitude()+","+user.getLongitude()+",18z";
            Log.v("location",lastloc);
        }
        if(user.getEcon1() != 0){
            Log.v("status","sending message to "+user.getEcon1());
            sendsms(user.getEcon1().toString(),message);
            Log.v("status","Message sent to "+user.getEcon1());
            if(flag == 1){sendsms(user.getEcon1().toString(),lastloc);}
        }
        if(user.getEcon2() != 0){
            Log.v("status","sending message to "+user.getEcon2());
            sendsms(user.getEcon2().toString(),message);
            Log.v("status","Message sent to "+user.getEcon2());
            if(flag == 1){sendsms(user.getEcon2().toString(),lastloc);}
        }
        if(user.getEcon3() != 0){
            Log.v("status","sending message to "+user.getEcon3());
            sendsms(user.getEcon3().toString(),message);
            Log.v("status","Message sent to "+user.getEcon3());
            if(flag == 1){sendsms(user.getEcon3().toString(),lastloc);}
        }
        Toast.makeText(getActivity(),"Emergency contacts have been notified via text message",Toast.LENGTH_SHORT).show();
    }

    public void makecall(User user,String number){
        Intent callIntent =new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));
        startActivity(callIntent);
    }

    public void initiate_emergency(User user){
        final User myuser = user;
        Thread sos = new Thread(){
            public void run(){
                Log.v("SOSThread","Thread Started");
                new CountDownTimer(5000,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if(myuser.getSosflag() == 0){this.cancel();Log.v("SOSThread","Thread Ended");}
                    }
                    @Override
                    public void onFinish() {
                        if(myuser.getLatitude() != "" && myuser.getLongitude() != ""){
                            String lastloc = "Last Known Location is in this area :\n";
                            lastloc = lastloc + "https://www.google.com/maps/@"+myuser.getLatitude()+","+myuser.getLongitude()+",18z";
                            Log.v("SOSThread","Sending Message = "+lastloc);
                            if(myuser.getEcon1()!=0){sendsms(myuser.getEcon1().toString(),lastloc);}
                            if(myuser.getEcon2()!=0){sendsms(myuser.getEcon2().toString(),lastloc);}
                            if(myuser.getEcon3()!=0){sendsms(myuser.getEcon3().toString(),lastloc);}
                        }
                        Log.v("SOSThread","SOS flag = "+myuser.getSosflag());
                        if(myuser.getSosflag() == 1){this.start();}
                    }
                }.start();
            }
        };
        sos.run();
    }

    public void fetchlocation(){

        final ProgressDialog progressBar1;
        progressBar1 = new ProgressDialog(getContext());
        progressBar1.setMessage("Finding current location...");
        progressBar1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar1.setCancelable(true);
        progressBar1.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        Log.v(TAG,"Initializing fetch location thread");

        if(checkInternetStatus()){
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
            else{
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    cur_position = locationfetch();
                    if(cur_position.latitude == 0){

                        progressBar1.show();
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                locationManager.removeUpdates(this);
                                Log.v(TAG,"current location: "+location);
                                cur_position = new LatLng(location.getLatitude(),location.getLongitude());
                                setUserLocation(user, cur_position);
                                address = findLocality(cur_position);
                                updateLocality(user, address);
                                progressBar1.dismiss();
                            }
                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {}
                            @Override
                            public void onProviderEnabled(String provider) {}
                            @Override
                            public void onProviderDisabled(String provider) {}
                        });
                    }
                    else{
                        setUserLocation(user, cur_position);
                        address = findLocality(cur_position);
                        updateLocality(user, address);
                    }

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            progressBar1.dismiss();
                            if (cur_position.latitude == location.getLatitude() && cur_position.longitude == location.getLongitude()) {
                                Log.v(TAG, "Location updated (SAME VALUE)");
                            } else {
                                cur_position = new LatLng(location.getLatitude(), location.getLongitude());
                                setUserLocation(user, cur_position);
                                Log.v(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                            }
                        }
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}
                        @Override
                        public void onProviderEnabled(String provider) {}
                        @Override
                        public void onProviderDisabled(String provider) {}
                    });
                }
            }

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final LayoutInflater myinflater = inflater;
        final ViewGroup mycontainer = container;
        db = FirebaseFirestore.getInstance();
        user = new User(getActivity());
        call100 = root.findViewById(R.id.button7);
        call108 = root.findViewById(R.id.imageButton);
        call101 = root.findViewById(R.id.imageButton2);
        test = root.findViewById(R.id.button8);
        sosalert = root.findViewById(R.id.toggleButton);
        if(user.getSosflag() == 1){sosalert.setChecked(true);}

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fetchlocation();
                Log.v(TAG,"Starting Location Thread");
            }
        });

        call100.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Log.v("status", "asking for permission");
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},120);
                }
                else{
                    makecall(user,"100");
                }
            }
        });

        call108.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Log.v("status", "asking for permission");
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},124);
                }
                else{
                    makecall(user,"108");
                }
            }
        });

        call101.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Log.v("status", "asking for permission");
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},125);
                }
                else{
                    makecall(user,"101");
                }
            }
        });

        sosalert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                        Log.v("status", "asking for permission");
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},122);
                    }
                    else{
                        if(user.getEcon1() == 0 && user.getEcon2() == 0 && user.getEcon3() == 0){
                            sosalert.setChecked(false);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setCancelable(true);
                            builder.setTitle("Emergency Contacts is not set");
                            builder.setMessage("You haven't set any Emergency contacts yet ! Do you want to add Emergency contacts ?");
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), emergencycontacts.class);
                                    startActivity(intent);
                                    ((Activity)getContext()).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                    return;
                                }
                            });
                            builder.show();
                        }
                        else{
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("Sending Emergency Message").setMessage("Waiting...").setCancelable(false);
                            dialog.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            cntflag = 1;
                                            dialog.dismiss();
                                        }
                                    });
                            final AlertDialog alert = dialog.create();
                            alert.show();
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    Log.v("status",(int)((millisUntilFinished+1000)/1000)+"");
                                    alert.setMessage("Alerting Emergency Contact via text message in "+(int)((millisUntilFinished+1000)/1000)+" sec...");
                                    if(cntflag == 1){
                                        cntflag=0;
                                        this.cancel();
                                        sosalert.setChecked(false);
                                        Toast.makeText(getActivity(),"SOS Alert is stopped",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFinish() {
                                    alert.dismiss();
                                    cntflag = 0;
                                    alert_contact(user);
                                    user.setSosflag(1);
                                    initiate_emergency(user);
                                }
                            }.start();
                        }
                    }
                }
                else{
                    Log.v("status","unchecked");
                    Toast.makeText(getActivity(),"SOS Alert is stopped",Toast.LENGTH_SHORT).show();
                    user.setSosflag(0);
                }
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getLatitude() != "" && user.getLongitude() != "") {
                    if (!checkInternetStatus()) { Internet_DisableAlert(); }
                    else {
                        final User user = new User(getActivity());
                        db.collection("emergency_requests").whereEqualTo("userId",user.getUserid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.getResult().size() > 0){
                                    Toast.makeText(getActivity(), "Cannot Broadcast Emergency Signal", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> ERequest = new HashMap<>();
                                    ERequest.put("ContactNumber", user.getContactnumber());
                                    ERequest.put("UserType", user.getType());
                                    ERequest.put("Status", "Active");
                                    ERequest.put("VolunteerID", "");
                                    ERequest.put("VolunteerNo", 0);
                                    ERequest.put("Latitude", user.getLatitude());
                                    ERequest.put("Longitude", user.getLongitude());
                                    ERequest.put("lcity", user.getlcity());
                                    ERequest.put("userId", user.getUserid());
                                    ERequest.put("created", FieldValue.serverTimestamp());
                                    db.collection("emergency_requests").add(ERequest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getActivity(), "Emergency Request Broadcasted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
                else{
                    Intent in = new Intent(getActivity(),MainActivity.class);
                    startActivity(in);
                    ((Activity)getContext()).overridePendingTransition(0,0);
                }
            }
        });

        Intent in = new Intent(getActivity(), BGDatabaseListenerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v(TAG, "Starting in foreground");
            getActivity().startForegroundService(new Intent(getActivity(), BGDatabaseListenerService.class));
        } else {
            Log.v(TAG, "Starting in background");
            getActivity().startService(new Intent(getActivity(), BGDatabaseListenerService.class));
        }
        Log.v(TAG, "After starting");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v(TAG, "Starting in foreground");
            getActivity().startForegroundService(new Intent(getActivity(), BackgroundService.class));
        } else {
            Log.v(TAG, "Starting in background");
            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }
        Log.v(TAG, "After starting");

        return root;
    }
}