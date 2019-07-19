package com.programmerbaper.skripsi.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.adapter.DetailAdapter;
import com.programmerbaper.skripsi.misc.Helper;
import com.programmerbaper.skripsi.model.api.Makanan;
import com.programmerbaper.skripsi.model.api.Transaksi;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.misc.Config.DATA_TRANSAKSI;

public class DetailTransaksiActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Transaksi transaksi ;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private LinearLayoutManager layoutManager;

    private GoogleMap googleMap;
    private Marker marker;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        initProgressDialog();
        recyclerView = findViewById(R.id.rvDetail);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getIntent().getExtras();
        transaksi = bundle.getParcelable(DATA_TRANSAKSI);



        Button done = findViewById(R.id.done);
        done.setText("Transaksi Selesai ("+ Helper.formatter(transaksi.getHarga()+"")+")");

//        done.setOnClickListener(new doneListener(this));

        setTitle("Detail Transaksi "+transaksi.getNama());

        //set list of pesanan to be scrollable
        ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout))
                .setScrollableView(findViewById(R.id.scrollView));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dialog.show();
        getDetail();
    }

    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Daftar Detail Pesanan");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }

    private void getDetail() {

        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<List<Makanan>> call = apiInterface.detailTransaksiGet(transaksi.getIdTransaksi()) ;
        call.enqueue(new Callback<List<Makanan>>() {
            @Override
            public void onResponse(Call<List<Makanan>> call, Response<List<Makanan>> response) {
                dialog.dismiss();
                List<Makanan> listMakanan = response.body();

                detailAdapter = new DetailAdapter(DetailTransaksiActivity.this, listMakanan);
                recyclerView.setAdapter(detailAdapter);
                detailAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<Makanan>> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(DetailTransaksiActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(10, 180, 10, 10);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        dialog.show();
        initMarkerToPhoneLocation();

        LatLng posPembeli = new LatLng(transaksi.getLatitude(), transaksi.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(posPembeli).title(transaksi.getAlamat()).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.mark9)));
    }

    @SuppressLint("MissingPermission")
    private void initMarkerToPhoneLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                latitude = location.getLatitude();
                longitude = location.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                if (marker != null) {
                    marker.remove();
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Posisi Dagang Anda").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                } else {
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Posisi Dagang Anda").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }

                dialog.dismiss();


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


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);


    }

}
