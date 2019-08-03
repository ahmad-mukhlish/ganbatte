package com.programmerbaper.skripsi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.adapter.PesananAdapter;
import com.programmerbaper.skripsi.misc.CurrentActivityContext;
import com.programmerbaper.skripsi.model.api.Transaksi;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.misc.Config.ID_USER;
import static com.programmerbaper.skripsi.misc.Config.MY_PREFERENCES;

public class ListPesananActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PesananAdapter pesananAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pesanan);
        initProgressDialog();
        recyclerView = findViewById(R.id.rvPesanan);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getListTransaksi();
        setTitle("List Pesanan");
    }

    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Daftar Pesanan");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }

    private void getListTransaksi() {
        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_USER, "");

        Call<List<Transaksi>> call = null;
        if (id != null) {
            call = apiInterface.pesananOnlineGet(Integer.parseInt(id));
        }
        if (call != null) {
            call.enqueue(new Callback<List<Transaksi>>() {
                @Override
                public void onResponse(Call<List<Transaksi>> call, Response<List<Transaksi>> response) {
                    dialog.dismiss();

                    List<Transaksi> listTransaksi = response.body();

                    pesananAdapter = new PesananAdapter(getApplicationContext(), listTransaksi, ListPesananActivity.this);
                    recyclerView.setAdapter(pesananAdapter);
                    pesananAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<List<Transaksi>> call, Throwable t) {
                    dialog.dismiss();
                    Log.v("cik",t.getMessage());
                    Toast.makeText(ListPesananActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
}
