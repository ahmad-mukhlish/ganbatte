package com.programmerbaper.skripsi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.model.api.Pedagang;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.misc.Config.BASE_URL;
import static com.programmerbaper.skripsi.misc.Config.FCM_TOKEN;
import static com.programmerbaper.skripsi.misc.Config.ID_PEMILIK;
import static com.programmerbaper.skripsi.misc.Config.ID_USER;
import static com.programmerbaper.skripsi.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsi.misc.Config.PASSWORD;
import static com.programmerbaper.skripsi.misc.Config.USERNAME;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
    private TextView policy;
    private CheckBox checkBox;
    private ProgressDialog dialog;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        bind();
        initProgressDialog();
        initPreferences();
        Log.v("cik", "coba");


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.equals("") && pass.equals("")) {
                Toast.makeText(LoginActivity.this, getString(R.string.tst_empty_login), Toast.LENGTH_SHORT).show();
            } else if (!checkBox.isChecked()) {
                Toast.makeText(LoginActivity.this, "Silakan setujui Terms of Service dan Privacy Policy terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                requestLogin(user, pass);
            }

        }
        else if (view.getId() == R.id.policy) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL+"policy")));


        }


    }

    @Override
    public void onBackPressed() {

    }

    private void bind() {

        checkBox = findViewById(R.id.check);
        policy = findViewById(R.id.policy);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
        policy.setOnClickListener(this);
    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.lbl_progress_login);
        dialog.setMessage(getString(R.string.lbl_message_progress_login));
        dialog.setCancelable(false);
    }

    private void initPreferences() {
        pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");
        if (!id.equals("")) {
            Intent intent = new Intent(LoginActivity.this, DagangActivity.class);
            startActivity(intent);
        }
    }


    private void writeStatusToFirebase(int idPemilik, int idPedagang) {

        DatabaseReference root = FirebaseDatabase.getInstance().getReference()
                .child("pemilik").child("pmk" + idPemilik).child("status"
                ).child("pdg" + idPedagang
                );

        root.child("login").setValue(true);

    }

    private void requestLogin(String user, String pass) {

        dialog.show();

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<Pedagang> call = apiInterface.getUser(user, pass);
        call.enqueue(new Callback<Pedagang>() {
            @Override
            public void onResponse(Call<Pedagang> call, Response<Pedagang> response) {
                Pedagang pedagang = response.body();

                if (response.body() != null) {
                    if (!pedagang.getNama().equals("Password Salah")) {
                        dialog.dismiss();
                        editor = pref.edit();
                        editor.putString(ID_USER, String.valueOf(pedagang.getIdPedagang()));
                        editor.putString(ID_PEMILIK, String.valueOf(pedagang.getIdPemilik()));
                        editor.putString(USERNAME, String.valueOf(pedagang.getUsername()));
                        editor.putString(PASSWORD, String.valueOf(pedagang.getPassword()));
                        editor.apply();

                        tokenize();

                        writeStatusToFirebase(pedagang.getIdPemilik(), pedagang.getIdPedagang());

                        Intent intent = new Intent(LoginActivity.this, DagangActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.tst_wrong_pass), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Terjadi Error Pada Server", Toast.LENGTH_SHORT).show();

                    try {
                        Log.v("cik", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<Pedagang> call, Throwable t) {
                dialog.dismiss();
                Log.v("cik", t.getMessage());
                Toast.makeText(LoginActivity.this, getString(R.string.tst_login_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void tokenize() {

        //Check wether token exist or not at shared pref and dbase
        pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        Log.v("cik", pref.getString(FCM_TOKEN, ""));
        if (pref.getString(FCM_TOKEN, "").isEmpty()) {

            Log.v("cik", "empty mang");
            Log.v("cik", pref.getString(ID_USER, ""));

            APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
            Call<String> call = apiInterface.retrieveTokenByIDGet(Integer.parseInt(pref.getString(ID_USER, "")));
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.body().isEmpty()) {

                        Log.v("cik", response.body());
                        getTokenFromFcm();

                    } else {
                        editor = pref.edit();
                        editor.putString(FCM_TOKEN, response.body());
                        editor.apply();

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    dialog.dismiss();
                    t.printStackTrace();
                    Log.v("cik", t.getMessage());
                    Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga Pada FCM", Toast.LENGTH_SHORT).show();

                }
            });


        }

        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }

    private void getTokenFromFcm() {

        Log.v("cik", "asup");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();

                Log.v("cik", token);

                editor = pref.edit();
                editor.putString(FCM_TOKEN, token);
                editor.apply();

                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                Call<String> call = apiInterface.saveTokenByIDPost(Integer.parseInt(pref.getString(ID_USER, "")), token);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.v("cik", response.body());

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.v("cik", t.getMessage());
                    }
                });


            }
        });
    }
}
