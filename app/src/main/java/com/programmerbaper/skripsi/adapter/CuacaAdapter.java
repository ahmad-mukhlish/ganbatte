package com.programmerbaper.skripsi.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.model.cuaca.List;
import com.squareup.picasso.Picasso;


public class CuacaAdapter extends RecyclerView.Adapter<CuacaAdapter.CuacaViewHolder> {

    private Context mContext;
    private java.util.List<List> mList;
    private Activity parentActivity;
    private CuacaAdapter adapter;
    private ProgressDialog progressDialog = null;

    public CuacaAdapter(Context mContext, java.util.List<List> mList, Activity parentActivity) {
        this.mContext = mContext;
        this.mList = mList;
        this.parentActivity = parentActivity;
        this.adapter = this;
    }

    @Override
    public CuacaAdapter.CuacaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.cuaca_card, null, false);
        CuacaAdapter.CuacaViewHolder adapter = new CuacaAdapter.CuacaViewHolder(view);

        return adapter;
    }


    private void initProgressDialog() {
        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang Memproses..");
        progressDialog.setCancelable(false);
    }


    @Override
    public void onBindViewHolder(final CuacaAdapter.CuacaViewHolder cuacaViewHolder, int i) {
        initProgressDialog();

        final List cuacaNow = mList.get(i);

        Picasso.get()
                .load("http://openweathermap.org/img/w/" + cuacaNow.getWeather().get(0).getIcon() + ".png")
                .into(cuacaViewHolder.icon);

        cuacaViewHolder.waktu.setText(cuacaNow.getDtTxt().substring(11,cuacaNow.getDtTxt().length()-3));
        cuacaViewHolder.cuaca.setText(translate(cuacaNow.getWeather().get(0).getMain()));



    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CuacaViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon ;
        private TextView cuaca, waktu;
        private View view;

        public CuacaViewHolder(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            cuaca = itemView.findViewById(R.id.cuaca);
            waktu = itemView.findViewById(R.id.waktu);


            this.view = itemView;

        }
    }

    private String translate(String input) {

        String hasil = "" ;

        if (input.toLowerCase().equals("thunderstorm")) {

            hasil = "Badai Petir" ;

        }

        else if (input.toLowerCase().equals("drizzle")) {

            hasil = "Gerimis" ;

        }

        else if (input.toLowerCase().equals("rain")) {

            hasil = "Hujan" ;

        }

        else if (input.toLowerCase().equals("snow")) {

            hasil = "Bersalju" ;

        }

        else if (input.toLowerCase().equals("clouds")) {

            hasil = "Berawan" ;

        }

        else if (input.toLowerCase().equals("clear")) {

            hasil = "Cerah" ;

        }

        return hasil ;
    }
}
