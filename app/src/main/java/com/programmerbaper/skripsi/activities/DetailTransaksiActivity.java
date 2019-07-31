package com.programmerbaper.skripsi.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.adapter.CuacaAdapter;
import com.programmerbaper.skripsi.adapter.DetailAdapter;
import com.programmerbaper.skripsi.misc.CurrentActivityContext;
import com.programmerbaper.skripsi.misc.Helper;
import com.programmerbaper.skripsi.misc.directionhelpers.FetchURL;
import com.programmerbaper.skripsi.misc.directionhelpers.TaskLoadedCallback;
import com.programmerbaper.skripsi.model.api.DetailTransaksi;
import com.programmerbaper.skripsi.model.api.Makanan;
import com.programmerbaper.skripsi.model.api.Transaksi;
import com.programmerbaper.skripsi.model.cuaca.Cuaca;
import com.programmerbaper.skripsi.model.jarak.Jarak;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;
import com.programmerbaper.skripsi.retrofit.dm.DMClient;
import com.programmerbaper.skripsi.retrofit.dm.DMInterface;
import com.programmerbaper.skripsi.retrofit.owm.OWMClient;
import com.programmerbaper.skripsi.retrofit.owm.OWMInterface;
import com.programmerbaper.skripsi.services.TrackingService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.misc.Config.API_KEY_OWM;
import static com.programmerbaper.skripsi.misc.Config.DATA_TRANSAKSI;
import static com.programmerbaper.skripsi.misc.Config.ID_PEMILIK;
import static com.programmerbaper.skripsi.misc.Config.ID_USER;
import static com.programmerbaper.skripsi.misc.Config.MY_PREFERENCES;

public class DetailTransaksiActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private Transaksi transaksi;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private LinearLayoutManager layoutManager;

    private GoogleMap googleMap;
    private Marker marker;
    private double latitude;
    private double longitude;
    private LatLng posPembeli;
    private Polyline currentPolyline;
    private boolean dekat = false;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    public static boolean bertransaksi;


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
        done.setText("Transaksi Selesai (" + Helper.formatter(transaksi.getHarga() + "") + ")");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCuaca();
                sendNotifSelesai();
                berhentiBertransaksi();
                Intent intent = new Intent(DetailTransaksiActivity.this, DagangActivity.class);
                startActivity(intent);


            }
        });

        String[] firstName = transaksi.getNama().split("\\s+");
        setTitle("Detail Transaksi " + firstName[0]);

        //set list of pesanan to be scrollable
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setScrollableView(findViewById(R.id.scrollView));
        slidingUpPanelLayout.setEnabled(false);
        slidingUpPanelLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Toast.makeText(DetailTransaksiActivity.this,"Dekati Titik Terlebih Dahulu",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dialog.show();
        getDetail();
        dialogueKeterangan();
        mulaiBertransaksi();
    }

    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Daftar Detail Pesanan");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_transaksi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note) {
            dialogueKeterangan();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogueKeterangan() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootDialog = LayoutInflater.from(this).inflate(R.layout.dialogue_keterangan, null);
        TextView keterangan = rootDialog.findViewById(R.id.keterangan);

        if (transaksi.getCatatan() == null) {
            keterangan.setText("Catatan Kosong");
        } else {
            keterangan.setText(transaksi.getCatatan());
        }

        builder.setView(rootDialog);
        final AlertDialog dialog = builder.create();
        dialog.show();


        TextView ok = rootDialog.findViewById(R.id.konfirmasi_catatan);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    private void getDetail() {

        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<List<Makanan>> call = apiInterface.detailTransaksiGet(transaksi.getIdTransaksi());
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

        posPembeli = new LatLng(transaksi.getLatitude(), transaksi.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(posPembeli).title(transaksi.getAlamat()));
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


                new FetchURL(DetailTransaksiActivity.this)
                        .execute(getRouteUrl(posPembeli, latLng, "walking"), "walking");

                if (!dekat) {
                    cekJarak(latLng, posPembeli, "walking");
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


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);


    }

    private String getRouteUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = googleMap.addPolyline((PolylineOptions) values[0]);
        currentPolyline.setColor(R.color.colorPrimaryDark);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CurrentActivityContext.setActualContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CurrentActivityContext.setActualContext(null);
    }


    private void cekJarak(LatLng origin, LatLng destination, String mode) {

        String origins = origin.latitude + "," + origin.longitude;
        String destinations = destination.latitude + "," + destination.longitude;

        DMInterface dmInterface = DMClient.getApiClient().create(DMInterface.class);
        Call<Jarak> call = dmInterface.getJarak(origins, destinations, mode, getString(R.string.google_maps_key));
        call.enqueue(new Callback<Jarak>() {
            @Override
            public void onResponse(Call<Jarak> call, Response<Jarak> response) {
                Log.v("cikandes",response.body().getRows().get(0).getElements().get(0).getDistance().getValue()+"");
                if (response.body().getRows().get(0).getElements().get(0).getDistance().getValue() < 200) {
                    dekat = true;
                    sendNotifDekat();
                    Toast.makeText(DetailTransaksiActivity.this, "Anda sudah dekat, silahkan swipe up untuk transaksi",
                            Toast.LENGTH_SHORT)
                            .show();
                    slidingUpPanelLayout.setEnabled(true);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    slidingUpPanelLayout.setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View view, DragEvent dragEvent) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Jarak> call, Throwable t) {

            }
        });

    }

    private void sendNotifDekat() {
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");

        Call<String> call = apiInterface.notifDekatPost(transaksi.getIdTransaksi(),
                transaksi.getIdPembeli(), Integer.parseInt(id));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.v("cikan", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    private void updateTransaksi(String cuaca) {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<String> call = apiInterface.updateTransaksiPost(transaksi.getIdTransaksi(), cuaca);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.v("cikk", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void sendNotifSelesai() {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");

        Call<String> call = apiInterface.notifSelesaiPost(transaksi.getIdTransaksi(),
                transaksi.getIdPembeli(), Integer.parseInt(id));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.v("cikk", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void mulaiBertransaksi() {

        bertransaksi = true;

        startService(new Intent(DetailTransaksiActivity.this, TrackingService.class));

        //write to firebase set the keliling value to true

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");
        String idPemilik = pref.getString(ID_PEMILIK, "");


        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                .child("pmk" + idPemilik).child("status").child("pdg" + id);

        root.child("keliling").setValue(true);


    }


    private void berhentiBertransaksi() {
        bertransaksi = false;

        //write to firebase set the keliling value to false
        stopService(new Intent(DetailTransaksiActivity.this, TrackingService.class));

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");
        String idPemilik = pref.getString(ID_PEMILIK, "");


        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                .child("pmk" + idPemilik).child("status").child("pdg" + id);

        root.child("keliling").setValue(false);


    }

    private String getCuaca() {

        dialog.show();

        final String[] responseOWM = new String[1];

        OWMInterface owmInterface = OWMClient.getApiClient().create(OWMInterface.class);
        Call<String> call = owmInterface.getWeather(latitude, longitude, API_KEY_OWM);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dialog.dismiss();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body());
                    JSONArray weather = jsonObject.getJSONArray("weather");
                    JSONObject weatherNow = weather.getJSONObject(0);
                    String cuaca = weatherNow.getString("main").toLowerCase();

                    if (cuaca.equals("rain") || cuaca.equals("drizzle") || cuaca.equals("thunderstorm")) {
                        cuaca = "hujan";
                    } else {
                        cuaca = "tidak hujan";
                    }

                    updateTransaksi(cuaca);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(DetailTransaksiActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();

            }
        });

        return responseOWM[0];

    }

}
