package com.programmerbaper.skripsi.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.model.api.Lokasi;
import com.programmerbaper.skripsi.services.TrackingService;

import static com.programmerbaper.skripsi.config.Config.ID_PEMILIK;
import static com.programmerbaper.skripsi.config.Config.ID_USER;
import static com.programmerbaper.skripsi.config.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsi.config.Config.PASSWORD;
import static com.programmerbaper.skripsi.config.Config.USERNAME;
import static java.lang.Integer.parseInt;

public class DagangActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Marker mMarker;
    private LocationListener mLocationListener;
    private ProgressDialog progressDialog = null;
    private Button mKeliling;
    public static boolean berkeliling;
    private double latitude;
    private double longitude;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagang);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("Dagang Keliling App");
        berkeliling = false;

        initProgressDialog();
        initPermission();

        mKeliling = findViewById(R.id.keliling);
        mKeliling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!berkeliling) {

                    berkeliling = true;

                    //Check whether GPS tracking is enabled//

                    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        finish();
                    }

                    //Check whether this app has access to the location permission//

                    int permission = ContextCompat.checkSelfPermission(DagangActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

                    //If the location permission has been granted, then start the TrackerService//

                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        startService(new Intent(DagangActivity.this, TrackingService.class));
                    } else {
                        initPermission();
                    }

                    SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    String id = pref.getString(ID_USER, "");
                    String idPemilik = pref.getString(ID_PEMILIK, "");


                    DatabaseReference root = FirebaseDatabase.getInstance().getReference()
                            .child("pmk"+idPemilik).child("status").child("pdg"+id);

                    root.child("keliling").setValue(true) ;



                    mKeliling.setText("Stop Keliling");
                    mKeliling.setBackground(getResources().getDrawable(R.drawable.round_red_button));


                } else {

                    berkeliling = false;
                    //Check whether GPS tracking is enabled//

                    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        finish();
                    }

                    //Check whether this app has access to the location permission//

                    int permission = ContextCompat.checkSelfPermission(DagangActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

                    //If the location permission has been granted, then start the TrackerService//

                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        stopService(new Intent(DagangActivity.this, TrackingService.class));
                    } else {
                        initPermission();
                    }

                    SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    String id = pref.getString(ID_USER, "");
                    String idPemilik = pref.getString(ID_PEMILIK, "");


                    DatabaseReference root = FirebaseDatabase.getInstance().getReference()
                            .child("pmk"+idPemilik).child("status").child("pdg"+id);

                    root.child("keliling").setValue(false) ;

                    DatabaseReference rootStop = FirebaseDatabase.getInstance().getReference()
                            .child("pmk"+idPemilik).child("lokasi").child("pdg"+id);





                    mKeliling.setText("Mulai Keliling");
                    mKeliling.setBackground(getResources().getDrawable(R.drawable.round_button));


                }


            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dagang, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.owm: {
                Intent intent = new Intent(this, CuacaActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                startActivity(intent);
                return true;
            }
            case R.id.logout : {


                SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                String id = pref.getString(ID_USER, "");
                String idPemilik = pref.getString(ID_PEMILIK, "");


                DatabaseReference root = FirebaseDatabase.getInstance().getReference()
                        .child("pmk"+idPemilik).child("status").child("pdg"+id);

                root.child("login").setValue(false) ;

                //TODO turn off tracker


                //flush shared preferences
                SharedPreferences.Editor editor = pref.edit();


                editor.putString(ID_USER, "");
                editor.putString(ID_PEMILIK, "");
                editor.putString(USERNAME, "");
                editor.putString(PASSWORD, "");

                editor.commit();

                Intent intent = new Intent(DagangActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(10, 180, 10, 10);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        progressDialog.show();
        initMarkerToPhoneLocation();


    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang Memproses..");
        progressDialog.setCancelable(false);
    }

    private void initPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show();
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        } else {
            // Permission has already been granted
            Toast.makeText(this, "Izin Lokasi diberikan", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("MissingPermission")
    private void initMarkerToPhoneLocation() {

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                latitude = location.getLatitude();
                longitude = location.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                if (mMarker != null) {
                    mMarker.remove();
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Posisi Dagang Anda").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
//                        mMap.setMaxZoomPreference(20);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                } else {
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Posisi Dagang Anda").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
//                        mMap.setMaxZoomPreference(20);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }

                progressDialog.dismiss();


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, mLocationListener);


    }




    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        boolean cik = isMyServiceRunning(TrackingService.class) ;
        Log.v("cik", "nilai cik" + cik) ;

    }
}
