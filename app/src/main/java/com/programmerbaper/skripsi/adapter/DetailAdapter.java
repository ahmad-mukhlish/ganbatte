package com.programmerbaper.skripsi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.misc.Helper;
import com.programmerbaper.skripsi.model.api.Makanan;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    private Context context;
    private List<Makanan> listDetail;

    public DetailAdapter(Context context, List<Makanan> listMakanan) {
        this.context = context;
        this.listDetail = listMakanan;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_detail, parent, false);
        return new DetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailViewHolder holder, int position) {
        final Makanan makananNow = listDetail.get(position);
        holder.jumlah.setText(makananNow.getNama());
        holder.harga.setText(Helper.formatter("" + (makananNow.getHarga() * makananNow.getJumlah())));
        holder.nama.setText(makananNow.getJumlah() + "");
        if (position % 2 != 0) {
            holder.mItemView.setBackgroundColor(Color.rgb(255, 255, 255));
        }


    }

    @Override
    public int getItemCount() {
        return listDetail.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {

        TextView jumlah, harga, nama;
        View mItemView;


        DetailViewHolder(View itemView) {
            super(itemView);
            jumlah = itemView.findViewById(R.id.nama);
            harga = itemView.findViewById(R.id.harga);
            nama = itemView.findViewById(R.id.jumlah);
            mItemView = itemView;
        }


    }
}
