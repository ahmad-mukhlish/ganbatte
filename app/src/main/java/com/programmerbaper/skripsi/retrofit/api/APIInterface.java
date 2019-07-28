package com.programmerbaper.skripsi.retrofit.api;


import com.programmerbaper.skripsi.model.api.Makanan;
import com.programmerbaper.skripsi.model.api.Pedagang;
import com.programmerbaper.skripsi.model.api.Transaksi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @FormUrlEncoded
    @POST("login")
    Call<Pedagang> getUser(@Field("username") String username,
                           @Field("password") String password);

    @GET("pesananOnlineGet/{id_pedagang}")
    Call<List<Transaksi>> pesananOnlineGet(@Path("id_pedagang") int idPedagang);

    @GET("detailTransaksiGet/{id_transaksi}")
    Call<List<Makanan>> detailTransaksiGet(@Path("id_transaksi") int idTransaksi);

    @GET("retrieveTokenByIDGet/{id_pedagang}")
    Call<String> retrieveTokenByIDGet(@Path("id_pedagang") int idPedagang);

    @FormUrlEncoded
    @POST("saveTokenByIDPost")
    Call<String> saveTokenByIDPost(@Field("id_pedagang") int idPedagang,
                                   @Field("fcm_token") String fcmToken);

    @FormUrlEncoded
    @POST("renullTokenPost")
    Call<String> renullTokenPost(@Field("id_pedagang") int idPedagang);

    @GET("transaksiByIDGet/{id_transaksi}")
    Call<Transaksi> transaksiByIDGet(@Path("id_transaksi") int idTransaksi);

    @FormUrlEncoded
    @POST("notifDekatPost")
    Call<String> notifDekatPost(@Field("id_transaksi") int idTransaksi,
                                @Field("id_pembeli") int idPembeli,
                                @Field("id_pedagang") int idPedagang);


    @FormUrlEncoded
    @POST("notifSelesaiPost")
    Call<String> notifSelesaiPost(@Field("id_transaksi") int idTransaksi,
                                  @Field("id_pembeli") int idPembeli,
                                  @Field("id_pedagang") int idPedagang);

    @FormUrlEncoded
    @POST("updateTransaksiPost")
    Call<String> updateTransaksiPost(@Field("id_transaksi") int transaksi,
                                     @Field("cuaca") String cuaca);

}
