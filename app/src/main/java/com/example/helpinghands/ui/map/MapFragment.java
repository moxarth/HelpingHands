package com.example.helpinghands.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.helpinghands.MainActivity;
import com.example.helpinghands.R;
import com.example.helpinghands.Splash;
import com.example.helpinghands.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.maps.GoogleMap.*;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

class Volunteer {
    boolean flag;
    String usertype;
    String userId;
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Marker getMarker() {
        return marker;
    }
    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    Marker marker;
    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}

class Requestor{
    boolean flag;
    String requestId;
    String userId;
    String Latitude;
    String longitude;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }



    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Marker getMarker() {
        return marker;
    }
    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    Marker marker;
}

public class MapFragment extends Fragment {
    static List<Volunteer> volunteerList = new ArrayList<Volunteer>();
    static List<Requestor> requestorList = new ArrayList<Requestor>();
    private MapViewModel dashboardViewModel;
    LocationManager locationManager;
    static  LatLng cur_position;
    static User user;
    static FirebaseFirestore db;
    private ImageButton recenter;
    static CameraPosition googlePlex;
    static Address address;
    static Marker marker;
    static boolean currflag = true;
    static boolean currflag1 = true;
    static int locflag = 1;
    static Circle circle;
    private static final String TAG = "MapLOG";
    static int focus = 1;
    static Context mycontext;
    public MapFragment(){}

    public int findVolunteer(String volunteerID){
        int i;
        for(i=0;i<volunteerList.size();i++){
            if(volunteerList.get(i).getUserId().equals(volunteerID)){
                return i;
            }
        }
        return -1;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static void generateEmergency(){
        Log.v(TAG,"Inside Map Fragment");
    }

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

    public void getVolunteerLocation(GoogleMap mMap1, LatLng my_cur_position, String myuserId, String Locality){
        final LatLng cur_position = my_cur_position;
        final GoogleMap mMap = mMap1;
        final String userId = myuserId;
        while(1 == 1) {
            if (volunteerList != null) {
                for (Volunteer volunteer : volunteerList ) {
                    if(currflag == volunteer.flag) {
                        volunteerList.remove(volunteer);
                        marker = volunteer.marker;
                        Log.d(TAG,"Volunteer Removed: "+ volunteer.userId);
                        if(getActivity()!=null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    marker.remove();
                                }
                            });
                        }
                    }
                }
            }
            db.collection("user_details").whereEqualTo("lcity", Locality).whereEqualTo("type","1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals(userId))
                                continue;
                            int i;
                            for(i = 0; i < volunteerList.size(); i++){
                                Volunteer volunteer = volunteerList.get(i);
                                if(volunteer.getUserId().equals(document.getId())){
                                    LatLng latLng = new LatLng(Double.parseDouble(document.get("latitude").toString()), Double.parseDouble(document.get("longitude").toString()));
                                    //Log.d(TAG, "Existing volunteer: "+document.get("firstname").toString());
                                    if (distance(latLng, cur_position) < 2.5) {
                                        Log.d(TAG, "Existing volunteer updated: "+document.get("firstname").toString());
                                        Marker marker = volunteer.getMarker();
                                        marker.setPosition(new LatLng(Double.parseDouble(document.get("latitude").toString()), Double.parseDouble(document.get("longitude").toString())));
                                        volunteer.flag = currflag;
                                    }
                                    break;
                                }
                            }
                            if(i == volunteerList.size()){
                                LatLng latLng = new LatLng(Double.parseDouble(document.get("latitude").toString()), Double.parseDouble(document.get("longitude").toString()));
                                //Log.d(TAG,"Cheking: "+document.get("firstname").toString());
                                if (distance(latLng, cur_position) < 2.5) {
                                    Volunteer volunteer = new Volunteer();
                                    volunteer.flag = currflag;
                                    volunteer.marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title("Volunteer")
                                            .icon(bitmapDescriptorFromVector(mycontext, R.drawable.baseline_volunteer_location_on_24)));
                                    volunteer.setUserId(document.getId());
                                    volunteerList.add(volunteer);
                                    Log.d(TAG,"Volunteer Added: "+document.get("firstname").toString());
                                }
                            }
                        }
                    } else {
                        Log.v(TAG, "Database Error in fetching");
                    }
                }
            });
            try {
                sleep(5000);
                currflag = !currflag;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getRequesterLocation(GoogleMap mMap1, final LatLng my_cur_position, String myuserId, String Locality){
        final LatLng cur_position = my_cur_position;
        final GoogleMap mMap = mMap1;
        final String userId = myuserId;
        while(1 == 1) {
            if (requestorList != null) {
                for (final Requestor requestor : requestorList ){
                    if(currflag1 == requestor.flag) {
                        requestorList.remove(requestor);
                        marker = requestor.marker;
                        Log.d(TAG,"Requestor Removed: "+ requestor.userId);
                        final int position = findVolunteer(requestor.getUserId());
                        final LatLng latlng = new LatLng(Double.parseDouble(requestor.getLatitude()),Double.parseDouble(requestor.getLongitude()));
                        if(position != -1){
                            if(getActivity()!= null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(position<volunteerList.size()){
                                        Log.d(TAG,"Requestor was Volunteer");
                                            marker.setIcon(bitmapDescriptorFromVector(mycontext,R.drawable.baseline_volunteer_location_on_24));
                                            volunteerList.get(position).marker = marker;
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            if(getActivity()!=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        marker.remove();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            db.collection("emergency_requests").whereEqualTo("lcity", Locality).whereEqualTo("Status","Active").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("userId").equals(userId))
                                continue;
                            int i;
                            for(i = 0; i < requestorList.size(); i++){
                                Requestor requestor = requestorList.get(i);
                                int position = findVolunteer(requestor.getUserId());
                                if(requestor.getUserId().equals(document.get("userId"))){
                                    if(position != -1){
                                        Log.v(TAG,"Already volunteer");
                                        volunteerList.get(position).setUsertype("volunteer");
                                        volunteerList.get(position).marker.setIcon(bitmapDescriptorFromVector(mycontext,R.drawable.trigger));
                                        requestor.marker = volunteerList.get(position).marker;
                                    }
                                    LatLng latLng = new LatLng(Double.parseDouble(document.get("Latitude").toString()), Double.parseDouble(document.get("Longitude").toString()));
                                    //Log.d(TAG, "Existing Requestor : "+document.get("userId").toString());
                                    if (distance(latLng, cur_position) < 2.5) {
                                        Log.v(TAG, "Requestor updated: "+document.get("userId").toString());
                                        requestor.setLatitude(document.get("Latitude").toString());
                                        requestor.setLongitude(document.get("Longitude").toString());
                                        Marker marker = requestor.getMarker();
                                        marker.setPosition(new LatLng(Double.parseDouble(document.get("Latitude").toString()), Double.parseDouble(document.get("Longitude").toString())));
                                        requestor.flag = currflag1;
                                    }
                                    break;
                                }
                            }
                            if(i == requestorList.size()){
                                LatLng latLng = new LatLng(Double.parseDouble(document.get("Latitude").toString()), Double.parseDouble(document.get("Longitude").toString()));
                                //Log.d(TAG,"Checking "+document.get("userId").toString());
                                if (distance(latLng, cur_position) < 2.5) {
                                    Requestor requestor = new Requestor();
                                    requestor.setRequestId(document.getId());
                                    requestor.setUserId(document.get("userId").toString());
                                    int position = findVolunteer(requestor.getUserId());
                                    requestor.setLatitude(document.get("Latitude").toString());
                                    requestor.setLongitude(document.get("Longitude").toString());
                                    requestor.flag = currflag1;
                                    if(position != -1){
                                        Log.v(TAG,"Already volunteer");
                                        volunteerList.get(position).setUsertype("volunteer");
                                        volunteerList.get(position).marker.setPosition(latLng);
                                        volunteerList.get(position).marker.setIcon(bitmapDescriptorFromVector(mycontext,R.drawable.trigger));
                                        requestor.marker = volunteerList.get(position).marker;
                                    }
                                    else{
                                    requestor.marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title("In Emergency")
                                            .icon(bitmapDescriptorFromVector(mycontext, R.drawable.trigger)));
                                    }
                                    requestorList.add(requestor);
                                    Log.d(TAG,"Requestor Added: "+document.get("userId").toString());
                                }
                            }
                        }
                    }
                    else {
                        Log.d(TAG, "Error fetching Requestor");
                    }
                }
            });
            try {
                sleep(5000);
                currflag1 = !currflag1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Location permission granted");
                    locationfetch();
                    NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    PendingIntent Pin = nc.createDeepLink().setDestination(R.id.navigation_map).createPendingIntent();
                     try {
                        Pin.send();
                        getActivity().overridePendingTransition(0,0);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.v(TAG,"Location permission rejected");
                    }
                return;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Marker setMarker(GoogleMap map,LatLng my_position){
        Marker location_marker = map.addMarker(new MarkerOptions()
                .position(my_position)
                .title("You are here")
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.baseline_location_on_24)));
        return location_marker;
    }

    public Circle drawCircle(LatLng my_position,GoogleMap map){
        CircleOptions circleOptions = new CircleOptions()
                .center(my_position)
                .radius(2500)
                .fillColor(Color.argb(50,255,0,0)).strokeWidth(0);
        Circle circle = map.addCircle(circleOptions);
        return circle;
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
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        builder.show();
    }

    public void startVolunteerThread(GoogleMap map){
        final GoogleMap mymap = map;
        Log.v(TAG,"(VT)ThreadId1: "+currentThread().getName() + currentThread().getId());
        Thread thread = new Thread(){
            public void run(){
                Log.v(TAG,"Starting Volunteer Thread: "+currentThread().getName() + currentThread().getId());
                getVolunteerLocation(mymap, cur_position, user.getUserid(), address.getLocality());
            }
        };
        thread.start();
    }

    public void startRequesterThread(GoogleMap map){
        final GoogleMap mymap = map;
        Log.v(TAG,"(VT)ThreadId1: "+currentThread().getName() + currentThread().getId());
        Thread thread = new Thread(){
            public void run(){
                Log.v(TAG,"Starting Requester Thread: "+currentThread().getName() + currentThread().getId());
                getRequesterLocation(mymap, cur_position, user.getUserid(), address.getLocality());
            }
        };
        thread.start();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_map, container, false);
        volunteerList = new ArrayList<Volunteer>();
        recenter = (ImageButton)root.findViewById(R.id.button5);
        recenter.setVisibility(View.INVISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        final ProgressDialog progressBar1;
        mycontext = getActivity();
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
        db = FirebaseFirestore.getInstance();
        user = new User(getActivity());

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {

                if (!checkInternetStatus()) { Internet_DisableAlert(); }
                else{
                    locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                    }
                    else {
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { GPS_DisableAlert(); }
                        else {
                            if(user.getLongitude() != "" && user.getLatitude() != ""){
                            cur_position = new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude()));
                            }
                            else{cur_position = locationfetch();}

                            mMap.setMapType(MAP_TYPE_NORMAL);
                            mMap.clear();
                            googlePlex = CameraPosition.builder().target(cur_position).zoom((float) 13.5).bearing(0).build();
                            circle = drawCircle(cur_position, mMap);//draw the circle of 2.5km radius
                            final Marker mark = setMarker(mMap, cur_position);//set the marker attribute
                            mMap.setOnCameraMoveStartedListener(new OnCameraMoveStartedListener() {
                                @Override
                                public void onCameraMoveStarted(int i) {
                                    if (i == 1) {
                                        focus = 0;
                                        recenter.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10, null);
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
                                        mark.setPosition(cur_position);
                                        circle.setCenter(cur_position);
                                        googlePlex = CameraPosition.builder().target(cur_position).zoom((float) 13.5).bearing(0).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                                        progressBar1.dismiss();
                                        startVolunteerThread(mMap);
                                        if(user.getType() == 1){startRequesterThread(mMap);}
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
                                startVolunteerThread(mMap);
                                if(user.getType() == 1){startRequesterThread(mMap);}
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
                                        mark.setPosition(cur_position);
                                        circle.setCenter(cur_position);
                                        if(focus == 1){
                                        googlePlex = CameraPosition.builder().target(cur_position).zoom((float) 13.5).bearing(0).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                                        }
                                    }
                                }
                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {}
                                @Override
                                public void onProviderEnabled(String provider) {}
                                @Override
                                public void onProviderDisabled(String provider) {}
                            });
                            recenter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    focus = 1;
                                    googlePlex = CameraPosition.builder().target(cur_position).zoom((float) 13.5).bearing(0).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                                    recenter.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                }
            }
        });
        return root;
    }
}