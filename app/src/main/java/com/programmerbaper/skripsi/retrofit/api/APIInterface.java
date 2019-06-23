package com.programmerbaper.skripsi.retrofit.api;



import com.programmerbaper.skripsi.model.api.Pedagang;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @FormUrlEncoded
    @POST("loginAPI")
    Call<Pedagang> getUser(@Field("username") String username,
                           @Field("password") String password);


}
