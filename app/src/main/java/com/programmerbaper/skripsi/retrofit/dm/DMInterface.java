package com.programmerbaper.skripsi.retrofit.dm;

import com.programmerbaper.skripsi.model.jarak.Jarak;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DMInterface {

    @GET("json")
    Call<Jarak> getJarak(@Query("origins") String origins, @Query("destinations") String destinations, @Query("mode") String mode,
                         @Query("key") String key);

}
