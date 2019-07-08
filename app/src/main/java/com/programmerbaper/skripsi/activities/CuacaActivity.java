package com.programmerbaper.skripsi.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.adapter.CuacaAdapter;
import com.programmerbaper.skripsi.model.cuaca.Cuaca;
import com.programmerbaper.skripsi.retrofit.owm.OWMClient;
import com.programmerbaper.skripsi.retrofit.owm.OWMInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.config.Config.API_KEY_OWM;

public class CuacaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CuacaAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuaca);
        getSupportActionBar().setTitle("Cuaca");

        bind();
        initProgressDialog();
        getCuaca();


    }

    private void getCuaca() {

        dialog.show();

        Bundle bundle = getIntent().getExtras();
        OWMInterface owmInterface = OWMClient.getApiClient().create(OWMInterface.class);
        Call<Cuaca> call = owmInterface.getCuaca((float) bundle.getDouble("lat"), (float) bundle.getDouble("lon"), API_KEY_OWM);

        call.enqueue(new Callback<Cuaca>() {
            @Override
            public void onResponse(Call<Cuaca> call, Response<Cuaca> response) {

                dialog.dismiss();
                List<com.programmerbaper.skripsi.model.cuaca.List> list = new ArrayList<>();


                for (com.programmerbaper.skripsi.model.cuaca.List listNow : response.body().getList()) {

                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateNow = new Date();

                    Log.v("cik",originalFormat.format(dateNow));

                    if (listNow.getDtTxt().substring(0, 10).equals(originalFormat.format(dateNow))) {
                        list.add(listNow);
                    }

                }


                adapter = new CuacaAdapter(getApplicationContext(), list, CuacaActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<Cuaca> call, Throwable t) {

                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(CuacaActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();

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

        recyclerView = findViewById(R.id.rvCuaca);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

}
