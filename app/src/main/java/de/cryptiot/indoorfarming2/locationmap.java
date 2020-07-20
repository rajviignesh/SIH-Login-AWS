package de.cryptiot.indoorfarming2;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.telephony.SmsManager;

//import com.example.hacktest.R;
import de.cryptiot.indoorfarming.R;
import de.cryptiot.indoorfarming2.location;
//import com.example.hacktest.location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class locationmap extends Fragment implements OnMapReadyCallback {

    GoogleMap gmap;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_location
                , container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //SupportMapFragment supportmapFragment = (SupportMapFragment)
        //       getSupportFragmentManager().findFragmentById(R.id.google_map);
        //supportmapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getActivity().getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(locationmap.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        final Marker marker;

        setupGoogleMapScreenSettings(googleMap);
        final LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        final MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(100)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
        circle.setCenter(latLng);
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Device"));

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("loco");
        //databaseReference.child("loco").setValue(lo);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot loco : dataSnapshot.getChildren()) {
                    location value = loco.getValue(location.class);
                    Log.d("Val", "Value is: " + value.getLat());
                    LatLng latLng1 = new LatLng(Double.valueOf(value.getLat()), Double.valueOf(value.getLng()));
                    //MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1).title("Device");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17));
                    //googleMap.addMarker(markerOptions1);
                    marker.setPosition(latLng1);
                    float[] results = new float[1];
                    Location.distanceBetween(latLng.latitude, latLng.longitude, latLng1.latitude, latLng1.longitude, results);
                    Toast.makeText(getActivity().getApplicationContext(), "" + results[0], Toast.LENGTH_SHORT).show();
                    if (results[0] > 100) {
                        notificationDialog();

                        try {
                            SmsManager smgr = SmsManager.getDefault();
                            String mobile = "+918939635828";
                            String mobile1 = "+919080854314";
                            String body = "Location Alert! Sriraam is outside the safezone!";
                            //String body = "Abnormal Vital Detected. Immediate medical attention required! ";
                            //String body = "null";
                            smgr.sendTextMessage(mobile, null, body, null, null);
                            smgr.sendTextMessage(mobile1, null, body, null, null);
                            Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();

                            //Intent callIntent = new Intent(Intent.ACTION_CALL);
                            //callIntent.setData(Uri.parse("tel:08939282997"));

                            //if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            //      Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            //return;
                            //}
                            //startActivity(callIntent);


                        } catch (Exception e) {
                            //Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("val", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void notificationDialog() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Location Alert")
                .setContentText("Sriraam is outside the safezone")
                .setContentInfo("Alert level : 1");
        notificationManager.notify(1, notificationBuilder.build());
    }
}