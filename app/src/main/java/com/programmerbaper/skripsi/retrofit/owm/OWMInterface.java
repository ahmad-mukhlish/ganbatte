package com.programmerbaper.skripsi.retrofit.owm;


import com.programmerbaper.skripsi.model.cuaca.Cuaca;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OWMInterface {


    @GET("forecast")
    Call<Cuaca> getCuaca(@Query("lat") float lat, @Query("lon") float lon, @Query("APPID") String APPID);

}

