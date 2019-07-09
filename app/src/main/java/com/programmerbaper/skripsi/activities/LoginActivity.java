package com.programmerbaper.skripsi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.model.api.Pedagang;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.config.Config.ID_PEMILIK;
import static com.programmerbaper.skripsi.config.Config.ID_USER;
import static com.programmerbaper.skripsi.config.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsi.config.Config.PASSWORD;
import static com.programmerbaper.skripsi.config.Config.USERNAME;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
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

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {

            Log.v("cik","cukk");

            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.equals("") && pass.equals("")) {
                Toast.makeText(LoginActivity.this, "Username dan Password Harus Diisi", Toast.LENGTH_SHORT).show();
            } else {
                requestLogin(user, pass);
            }

        }
    }

    @Override
    public void onBackPressed() {

    }

    private void bind() {

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Sedang Memeriksa..");
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

                if (!pedagang.getNama().equals("Password Salah")) {
                    dialog.dismiss();
                    editor = pref.edit();
                    editor.putString(ID_USER, String.valueOf(pedagang.getIdPedagang()));
                    editor.putString(ID_PEMILIK, String.valueOf(pedagang.getIdPemilik()));
                    editor.putString(USERNAME, String.valueOf(pedagang.getUsername()));
                    editor.putString(PASSWORD, String.valueOf(pedagang.getPassword()));
                    editor.apply();

                    writeStatusToFirebase(pedagang.getIdPemilik(), pedagang.getIdPedagang());

                    Intent intent = new Intent(LoginActivity.this, DagangActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pedagang> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
