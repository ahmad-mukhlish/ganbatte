package com.programmerbaper.skripsi.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DetailTransaksiActivity extends AppCompatActivity {

    private Transaksi transaksi ;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private LinearLayoutManager layoutManager;

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

}
