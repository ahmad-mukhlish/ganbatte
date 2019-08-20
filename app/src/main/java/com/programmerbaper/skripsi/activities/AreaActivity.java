package com.programmerbaper.skripsi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.adapter.AreaAdapter;
import com.programmerbaper.skripsi.adapter.CuacaAdapter;
import com.programmerbaper.skripsi.model.api.Area;
import com.programmerbaper.skripsi.model.cuaca.Cuaca;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;
import com.programmerbaper.skripsi.retrofit.owm.OWMClient;
import com.programmerbaper.skripsi.retrofit.owm.OWMInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.misc.Config.API_KEY_OWM;
import static com.programmerbaper.skripsi.misc.Config.ID_USER;
import static com.programmerbaper.skripsi.misc.Config.MY_PREFERENCES;

public class AreaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AreaAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        getSupportActionBar().setTitle("Area");

        bind();
        initProgressDialog();
        getArea();
    }


    private void getArea() {

        dialog.show();

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");

        Call<List<Area>> call = apiInterface.rekomendasiAreaGet(getTodayTanggal(),id);


        call.enqueue(new Callback<List<Area>>() {
            @Override
            public void onResponse(Call<List<Area>> call, Response<List<Area>> response) {

                dialog.dismiss();
                adapter = new AreaAdapter(getApplicationContext(), response.body(), AreaActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<List<Area>> call, Throwable t) {

                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(AreaActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Sedang Memproses..");
        dialog.setCancelable(false);
    }

    private void bind() {

        recyclerView = findViewById(R.id.rvArea);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private String getTodayTanggal() {

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);

    }
}
