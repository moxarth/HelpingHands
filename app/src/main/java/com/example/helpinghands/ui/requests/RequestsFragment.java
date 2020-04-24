package com.example.helpinghands.ui.requests;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.helpinghands.BackgroundService;
import com.example.helpinghands.MainActivity;
import com.example.helpinghands.R;
import com.example.helpinghands.User;
import com.example.helpinghands.editprofile;
import com.example.helpinghands.login;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;


import static java.lang.Thread.currentThread;

public class RequestsFragment extends Fragment{
    static User user ;
    static ArrayList<String> requestId = new ArrayList<String>();
    static ArrayList<String> timestamp = new ArrayList<String>();
    static ArrayList<String> latitude = new ArrayList<String>();
    static ArrayList<String> longitude = new ArrayList<String>();
    static ArrayList<String> status = new ArrayList<String>();
    static ArrayList<String> volunteerno = new ArrayList<String>();
    static ArrayList<String> city = new ArrayList<String>();
    private static final String TAG = "RequestLOG";
    private RequestsViewModel requestsViewModel;

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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Incoming Request Notification";
            String description = "Notification for Incoming Emergency Request";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("001", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void notifyUser(Context context,FragmentActivity myactivity){
        Date currentTime = Calendar.getInstance().getTime();
        String time = currentTime.getHours()+" : "+currentTime.getMinutes();

        NavController nc = Navigation.findNavController(myactivity, R.id.nav_host_fragment);
        PendingIntent pendingIntent = nc.createDeepLink().setDestination(R.id.navigation_map).createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"001")
                .setSmallIcon(R.drawable.ic_helpinghands)
                .setContentTitle("Incoming Request")
                .setContentText("Someone within your area needs your help")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(Integer.parseInt(currentTime.getMinutes()+""+currentTime.getSeconds()),builder.build());
    }

    public static void listenRequests(final Context context,final FragmentActivity myactivity){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("emergency_requests").whereEqualTo("lcity",user.getlcity()).whereEqualTo("Status","Active").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            notifyUser(context,myactivity);
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        user = new User(getActivity());
        requestsViewModel =
                ViewModelProviders.of(this).get(RequestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_requests, container, false);
        final FragmentActivity myactivity = getActivity();
        createNotificationChannel();

        final ListView list = root.findViewById(R.id.reqlist);
        requestId.clear();
        status.clear();
        latitude.clear();
        longitude.clear();
        volunteerno.clear();
        timestamp.clear();
        city.clear();

        if (!checkInternetStatus()) { Internet_DisableAlert(); }
        else {
            final ProgressDialog progressBar;
            progressBar = new ProgressDialog(getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Fetching data from the database...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();
            final FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("emergency_requests").whereEqualTo("userId", user.getUserid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            requestId.add(document.getId());
                            timestamp.add(document.get("created")+"");
                            latitude.add(document.get("Latitude")+"");
                            longitude.add(document.get("Longitude")+"");
                            status.add(document.get("Status")+"");
                            volunteerno.add(document.get("VolunteerNo")+"");
                            city.add(document.get("lcity")+"");
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Error connecting Database", Toast.LENGTH_SHORT).show();
                    }
                    MyCustomListAdapter adapter = new MyCustomListAdapter(myactivity,getActivity(),requestId,timestamp,latitude,longitude,status,volunteerno,city);
                    list.setAdapter(adapter);
                    progressBar.dismiss();
                }
            });
        }
         /*Intent in = new Intent(getActivity(), BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Restarter", "Starting in foreground");
            getActivity().startForegroundService(new Intent(getActivity(), BackgroundService.class));
        } else {
            Log.v("Restarter", "Starting in background");
            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }
        Log.v("Restarter", "After starting");
        */

        listenRequests(getContext(),myactivity);
        return root;
    }
}

class MyCustomListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> requestId;
    private final ArrayList<String> timestamp;
    private final ArrayList<String> latitude;
    private final ArrayList<String> longitude;
    private final ArrayList<String> status;
    private final ArrayList<String> volunteerno;
    private final ArrayList<String> city;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FragmentActivity myactivity;

    public MyCustomListAdapter(FragmentActivity myactivity,Activity context,ArrayList<String> requestId, ArrayList<String> timestamp, ArrayList<String> latitude,ArrayList<String> longitude,ArrayList<String> status,ArrayList<String> volunteerno,ArrayList<String> city){

        super(context,R.layout.requestlist , requestId);
        this.context=context;
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.volunteerno = volunteerno;
        this.city = city;
        this.myactivity = myactivity;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.requestlist, null,true);

        TextView requestIdtext = (TextView) rowView.findViewById(R.id.requestid);
        TextView timestamptext = (TextView) rowView.findViewById(R.id.timestamp);
        final TextView statustext = (TextView) rowView.findViewById(R.id.status);
        TextView latitudetext = (TextView) rowView.findViewById(R.id.latitude);
        TextView longitudetext = (TextView) rowView.findViewById(R.id.longitude);
        TextView volunteertext = (TextView) rowView.findViewById(R.id.volunteerno);
        TextView citytext = (TextView) rowView.findViewById(R.id.lcity);
        final Button stopbutton = (Button)rowView.findViewById(R.id.stopreq);
        final String reqid = requestId.get(position);

        requestIdtext.setText(requestId.get(position));
        timestamptext.setText(timestamp.get(position));
        statustext.setText(status.get(position));
        latitudetext.setText(longitude.get(position));
        longitudetext.setText(latitude.get(position));
        citytext.setText("("+city.get(position)+")");
        if(volunteerno.get(position).toString().equals("0")){ volunteertext.setText("Not Assigned"); }
        else{volunteertext.setText(volunteerno.get(position)); }
        if(!status.get(position).toString().equals("Active")){ stopbutton.setVisibility(View.GONE); }
        else{
            stopbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   db.collection("emergency_requests").document(reqid).update("Status","Aborted").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                stopbutton.setVisibility(View.GONE);
                                statustext.setText("Aborted");
                                Toast.makeText(myactivity, "Request stopped successfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(myactivity, "Error connecting Database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        return rowView;

    };

}
