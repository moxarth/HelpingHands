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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;


import static java.lang.Thread.currentThread;

public class RequestsFragment extends Fragment{
    static User user ;
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

    public static void listenRequests(final Context context){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("emergency_Requests").whereEqualTo("lcity",user.getlcity()).whereEqualTo("Status","ongoing").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New city: " + dc.getDocument().getData());
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                            Toast.makeText(context, "Modified", Toast.LENGTH_SHORT).show();
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }
    private static final String TAG = "RequestLOG";
    private RequestsViewModel requestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        user = new User(getActivity());
        requestsViewModel =
                ViewModelProviders.of(this).get(RequestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_requests, container, false);
        FragmentActivity myactivity = getActivity();
        createNotificationChannel();
        String[] requestId = new String[]{"abc","def","ghi","uuu"};
        String[] timestamp = new String[]{"hhh","hhh","hhh","uuu"};
        String[] latitude = new String[]{"fff","fff","fff","uuu"};
        String[] longitude = new String[]{"hhh","hhh","hhh","uuu"};
        String[] status = new String[]{"lll","lll","lll","uuu"};
        String[] volunteerno = new String[]{"ttt","ttt","ttt","uuu"};
        final ListView list = root.findViewById(R.id.reqlist);
        MyCustomListAdapter adapter = new MyCustomListAdapter(getActivity(),requestId,timestamp,latitude,longitude,status,volunteerno);
        list.setAdapter(adapter);
        //notifyUser(getContext(),myactivity);

        Intent in = new Intent(getActivity(), BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Restarter", "Starting in foreground");
            getActivity().startForegroundService(new Intent(getActivity(), BackgroundService.class));
        } else {
            Log.v("Restarter", "Starting in background");
            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }
        Log.v("Restarter", "After starting");
        listenRequests(getContext());

        return root;
    }

}

class MyCustomListAdapter extends ArrayAdapter {

    private final Activity context;
    private final String[] requestId;
    private final String[] timestamp;
    private final String[] latitude;
    private final String[] longitude;
    private final String[] status;
    private final String[] volunteerno;

    public MyCustomListAdapter(Activity context,String[] requestId, String[] timestamp, String[] latitude,String[] longitude,String[] status,String[] volunteerno){

        super(context,R.layout.requestlist , requestId);
        this.context=context;
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.volunteerno = volunteerno;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.requestlist, null,true);

        TextView requestIdtext = (TextView) rowView.findViewById(R.id.requestid);
        TextView timestamptext = (TextView) rowView.findViewById(R.id.timestamp);
        TextView statustext = (TextView) rowView.findViewById(R.id.status);
        TextView latitudetext = (TextView) rowView.findViewById(R.id.latitude);
        TextView longitudetext = (TextView) rowView.findViewById(R.id.longitude);
        TextView volunteertext = (TextView) rowView.findViewById(R.id.volunteerno);

        requestIdtext.setText(requestId[position]);
        timestamptext.setText(timestamp[position]);
        statustext.setText(latitude[position]);
        latitudetext.setText(longitude[position]);
        longitudetext.setText(status[position]);
        volunteertext.setText(volunteerno[position]);

        return rowView;

    };

}
