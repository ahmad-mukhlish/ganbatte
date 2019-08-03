package com.programmerbaper.skripsi.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.activities.DetailTransaksiActivity;
import com.programmerbaper.skripsi.misc.Helper;
import com.programmerbaper.skripsi.model.api.Transaksi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.programmerbaper.skripsi.misc.Config.BASE_URL;
import static com.programmerbaper.skripsi.misc.Config.DATA_TRANSAKSI;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder> {

    private Context context;
    private List<Transaksi> listTransaksi;
    private Activity parentActivity;
    private PesananAdapter adapter;
    private ProgressDialog progressDialog = null;

    public PesananAdapter(Context context, List<Transaksi> listTransaksi, Activity parentActivity) {
        this.context = context;
        this.listTransaksi = listTransaksi;
        this.parentActivity = parentActivity;
        this.adapter = this;
    }

    @Override
    public PesananAdapter.PesananViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_list_pesanan, null, false);
        PesananAdapter.PesananViewHolder adapter = new PesananAdapter.PesananViewHolder(view);

        return adapter;
    }


    private void initProgressDialog() {
        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang Memproses..");
        progressDialog.setCancelable(false);
    }


    @Override
    public void onBindViewHolder(final PesananAdapter.PesananViewHolder pedagangViewHolder, int i) {
        initProgressDialog();
        final Transaksi transaksi = listTransaksi.get(i);

        Glide.with(context)
                .load(BASE_URL + "storage/pembeli-profiles/" + transaksi.getFoto())
                .placeholder(R.drawable.pembeli_holder)
                .into(pedagangViewHolder.image);


        pedagangViewHolder.nama.setText(transaksi.getNama());
        pedagangViewHolder.alamat1.setText(getAlamat1(transaksi.getAlamat()));
        pedagangViewHolder.alamat2.setText(getAlamat2(transaksi.getAlamat()));
        pedagangViewHolder.totalHarga.setText(Helper.formatter(transaksi.getHarga() + ""));
        pedagangViewHolder.items.setText("(" + transaksi.getItem() + " item)");
        pedagangViewHolder.telfon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + transaksi.getNoTelp()));
                context.startActivity(intent);
            }
        });

        pedagangViewHolder.wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsApp(transaksi.getNoTelp());
            }
        });

        if (transaksi.getPreOrderStatus() == 1) {
            if (cekTanggal(transaksi.getTanggal())) {

                //transaksi aman, boleh di klik

                pedagangViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, DetailTransaksiActivity.class);
                        intent.putExtra(DATA_TRANSAKSI, transaksi);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

                pedagangViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#e8f5e9"));
                pedagangViewHolder.tanggal.setText(getTanggal(transaksi.getTanggal()));
                pedagangViewHolder.tanggal.setTextColor(Color.parseColor("#4caf50"));

            } else {

                pedagangViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Belum saatnya memenuhi pesanan", Toast.LENGTH_SHORT).show();
                    }
                });

                pedagangViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffebee"));
                pedagangViewHolder.tanggal.setText(getTanggal(transaksi.getTanggal()));
                pedagangViewHolder.tanggal.setTextColor(Color.parseColor("#f44336"));

            }

            pedagangViewHolder.tanggal.setVisibility(View.VISIBLE);

        } else {

            pedagangViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            pedagangViewHolder.tanggal.setVisibility(View.GONE);
            pedagangViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailTransaksiActivity.class);
                    intent.putExtra(DATA_TRANSAKSI, transaksi);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    public class PesananViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, wa, telfon, chat;
        private TextView nama, alamat1, alamat2, totalHarga, items, tanggal;
        private View view;
        private CardView cardView;

        public PesananViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            image = itemView.findViewById(R.id.image);
            nama = itemView.findViewById(R.id.nama);
            alamat1 = itemView.findViewById(R.id.alamat1);
            alamat2 = itemView.findViewById(R.id.alamat2);
            totalHarga = itemView.findViewById(R.id.total_harga);
            items = itemView.findViewById(R.id.items);
            wa = itemView.findViewById(R.id.wa);
            telfon = itemView.findViewById(R.id.telfon);
            chat = itemView.findViewById(R.id.chat);
            cardView = itemView.findViewById(R.id.card_pesanan);
            tanggal = itemView.findViewById(R.id.tanggal);
        }
    }

    private String getAlamat1(String alamat) {


        List<String> alamatList = Arrays.asList(alamat.split(",[ ]*"));

        return alamatList.get(0);

    }

    private String getAlamat2(String alamat) {

        List<String> alamatList = Arrays.asList(alamat.split(",[ ]*"));

        return alamatList.get(1) + ", " + alamatList.get(2);
    }

    private void openWhatsApp(String number) {
        try {
            number = number.replace(" ", "").replace("+", "");

            number = "+62" + number.substring(1);

            Log.v("cik", number);
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sendIntent);

        } catch (Exception e) {
            Log.v("cik", "ERROR_OPEN_MESSANGER" + e.toString());
        }
    }


    private boolean cekTanggal(String tanggalPesanan) {

        boolean hasil = false;

        SimpleDateFormat sql = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date pickan = sql.parse(tanggalPesanan);
            Date today = Calendar.getInstance().getTime();

            hasil = (pickan.before(today) || pickan.equals(today));
            Log.v("cik", sql.format(pickan));
            Log.v("cik", sql.format(today));


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hasil;
    }

    private String getTanggal(String tanggalPesanan) {

        String hasil = "";
        SimpleDateFormat sql = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));

        try {
            Date tanggal = sql.parse(tanggalPesanan);
            hasil = df.format(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "PRE ORDER : " + hasil;
    }
}
