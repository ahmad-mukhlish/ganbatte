package com.programmerbaper.skripsi.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.programmerbaper.skripsi.R;

import static com.programmerbaper.skripsi.config.Config.ID_USER;
import static com.programmerbaper.skripsi.config.Config.MY_PREFERENCES;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin;
    private String user, pass;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);

        initPreferences();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if (user.equals("") && pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Username dan Password Harus Diisi", Toast.LENGTH_SHORT).show();
                } else {
                    requestLogin();
                }
            }
        });
    }

    private void initPreferences() {
        pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");
        if (id.contains("1")) {
            //TODO jika dia id-nya satu masuk ke pedagang
//            Intent intent = new Intent(LoginActivity.this, OwnerMenuActivity.class);
//            startActivity(intent);
//            finish();
        } else if (id.contains("2")) {
            //TODO jika dia id-nya satu masuk ke pembeli
//            Intent intent = new Intent(LoginActivity.this, SalesMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    }



    private void requestLogin() {


        if (user.equals("admin") && pass.equals("admin")) {
            Intent intent = new Intent(LoginActivity.this, DagangActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
        }


//        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//        Call<User> call = apiInterface.getUser(user, pass);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                User user = response.body();

//                if (user.getMessage().equals("success")) {
//                    Log.d(DEBUG,user.getNama());
//                    dialog.dismiss();
//                    editor = pref.edit();
//                    editor.putString(ID_USER, String.valueOf(user.getIdUser()));
//                    editor.putString(NAMA_USER, user.getNama());
//                    editor.putString(KTP_SALES, user.getNoKtpSales());
//                    editor.putString(ACCESTOKEN, user.getAccesToken());
//                    editor.putInt("STATUS", user.getStatus());
//                    editor.apply();
//                    if (user.getStatus()==1) {
//                        Intent intent = new Intent(LoginActivity.this, OwnerMenuActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(LoginActivity.this, SalesMenuActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                } else {
//                    dialog.dismiss();
//                    Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
//                }
//            }

//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                dialog.dismiss();
//                t.printStackTrace();
//                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

}
