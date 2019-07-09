package com.programmerbaper.skripsi.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.programmerbaper.skripsi.services.TrackingService;

import static com.programmerbaper.skripsi.config.Config.ID_PEMILIK;
import static com.programmerbaper.skripsi.config.Config.ID_USER;
import static com.programmerbaper.skripsi.config.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsi.config.Config.PASSWORD;
import static com.programmerbaper.skripsi.config.Config.USERNAME;

public class DagangActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mMarker;
    private LocationListener mLocationListener;
    private ProgressDialog progressDialog = null;
    private Button mKeliling;
    public static boolean berkeliling;
    public static boolean permission = false;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagang);

        getSupportActionBar().setTitle("Dagang Keliling App");

        initProgressDialog();

        if (!permission) {
            initPermission();
        }

        bind();

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
                intentToCuaca();
                return true;
            }
            case R.id.logout: {
                logout();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        showMap(googleMap);

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Anda sudah di menu utama", Toast.LENGTH_SHORT).show();
    }

    private void logout() {

        if (!berkeliling) {
            //write logout to firebase
            SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            String id = pref.getString(ID_USER, "");
            String idPemilik = pref.getString(ID_PEMILIK, "");


            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                    .child("pmk" + idPemilik).child("status").child("pdg" + id);

            root.child("login").setValue(false);

            //flush shared preferences
            SharedPreferences.Editor editor = pref.edit();


            editor.putString(ID_USER, "");
            editor.putString(ID_PEMILIK, "");
            editor.putString(USERNAME, "");
            editor.putString(PASSWORD, "");

            editor.commit();

            Intent intent = new Intent(DagangActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Harap Berhenti Keliling Terlebih Dahulu", Toast.LENGTH_SHORT).show();
        }


    }

    private void showMap(GoogleMap googleMap) {

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
            permission = true;
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                } else {
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Posisi Dagang Anda").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
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

    private void bind() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mKeliling = findViewById(R.id.keliling);
        mKeliling.setOnClickListener(this);
        berkeliling = false;

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.keliling) {
            if (!berkeliling) {
                mulaiKeliling();
            } else {
                berhentiKeliling();
            }
        }
    }

    private void mulaiKeliling() {

        berkeliling = true;

        startService(new Intent(DagangActivity.this, TrackingService.class));

        //write to firebase set the keliling value to true
        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");
        String idPemilik = pref.getString(ID_PEMILIK, "");


        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                .child("pmk" + idPemilik).child("status").child("pdg" + id);

        root.child("keliling").setValue(true);


        mKeliling.setText("Stop Keliling");
        mKeliling.setBackground(getResources().getDrawable(R.drawable.round_red_button));

    }

    private void berhentiKeliling() {
        berkeliling = false;

        //write to firebase set the keliling value to false
        stopService(new Intent(DagangActivity.this, TrackingService.class));

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");
        String idPemilik = pref.getString(ID_PEMILIK, "");


        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                .child("pmk" + idPemilik).child("status").child("pdg" + id);

        root.child("keliling").setValue(false);


        mKeliling.setText("Mulai Keliling");
        mKeliling.setBackground(getResources().getDrawable(R.drawable.round_button));
    }

    private void intentToCuaca() {
        Intent intent = new Intent(this, CuacaActivity.class);
        intent.putExtra("lat", latitude);
        intent.putExtra("lon", longitude);
        startActivity(intent);
    }
}