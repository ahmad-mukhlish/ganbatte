package com.programmerbaper.skripsi.services;


import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programmerbaper.skripsi.model.api.Lokasi;

import androidx.core.content.ContextCompat;

import static com.programmerbaper.skripsi.activities.DagangActivity.berkeliling;
import static com.programmerbaper.skripsi.misc.Config.ID_PEMILIK;
import static com.programmerbaper.skripsi.misc.Config.ID_USER;
import static com.programmerbaper.skripsi.misc.Config.MY_PREFERENCES;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        loginToFirebase();
        requestLocationUpdates();

    }


    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Unregister the BroadcastReceiver when the notification is tapped//
            Log.v("cik", "cak");

            unregisterReceiver(stopReceiver);

            //Stop the Service//

            stopSelf();
        }
    };

    private void loginToFirebase() {

        //Authenticate with Firebase, using the email and password we created earlier//

        String email = "ahmad_mukhlish_s@yahoo.co.id";
        String password = "mukhlish";

        //Call OnCompleteListener if the user is signed in successfully//

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                //If the user has been authenticated...//

                if (task.isSuccessful()) {


                    requestLocationUpdates();

                } else {

                    //If sign in fails, then log the error//

                    Log.d(TAG, "Firebase authentication failed");
                }
            }
        });
    }


    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    String id = pref.getString(ID_USER, "");
                    String idPemilik = pref.getString(ID_PEMILIK, "");


                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                            .child("pmk" + idPemilik).child("lokasi").child("pdg" + id);


                    if (!id.isEmpty()) {
                        Location location = locationResult.getLastLocation();
                        Lokasi lokasi = new Lokasi(location.getLatitude(), location.getLongitude(), Integer.parseInt(id), berkeliling);

                        if (location != null && berkeliling) {
                            root.setValue(lokasi);
                        } else if (location != null && !berkeliling) {
                            root.child("moving").setValue(false);
                        }


                    }


                }
            }, null);
        }
    }
}
