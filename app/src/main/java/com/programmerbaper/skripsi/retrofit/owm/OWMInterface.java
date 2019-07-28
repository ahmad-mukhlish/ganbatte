package com.programmerbaper.skripsi.retrofit.owm;


import com.programmerbaper.skripsi.model.cuaca.Cuaca;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OWMInterface {


    @GET("forecast")
    Call<Cuaca> getForecast(@Query("lat") float lat, @Query("lon") float lon, @Query("APPID") String APPID);

    @GET("weather")
    Call<String> getWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("APPID") String APPID);

}

