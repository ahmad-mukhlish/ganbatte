package com.programmerbaper.skripsi.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.model.api.Area;


public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {

    private Context mContext;
    private java.util.List<Area> mList;
    private Activity parentActivity;
    private AreaAdapter adapter;
    private ProgressDialog progressDialog = null;

    public AreaAdapter(Context mContext, java.util.List<Area> mList, Activity parentActivity) {
        this.mContext = mContext;
        this.mList = mList;
        this.parentActivity = parentActivity;
        this.adapter = this;
    }

    @Override
    public AreaAdapter.AreaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_area, null, false);
        AreaAdapter.AreaViewHolder adapter = new AreaAdapter.AreaViewHolder(view);

        return adapter;
    }


    private void initProgressDialog() {
        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang Memproses..");
        progressDialog.setCancelable(false);
    }


    @Override
    public void onBindViewHolder(final AreaAdapter.AreaViewHolder cuacaViewHolder, int i) {
        initProgressDialog();

        final Area areaNow = mList.get(i);

        cuacaViewHolder.nomor.setText((i+1)+"");
        cuacaViewHolder.area.setText(areaNow.getArea());
        cuacaViewHolder.pendapatan.setText(areaNow.getPendapatan());



    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder {


        private TextView nomor, area, pendapatan;
        private View view;

        public AreaViewHolder(View itemView) {
            super(itemView);

            nomor = itemView.findViewById(R.id.nomor);
            area = itemView.findViewById(R.id.area);
            pendapatan = itemView.findViewById(R.id.pendapatan);


            this.view = itemView;

        }
    }


}
