package com.programmerbaper.skripsi.retrofit.owm;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.programmerbaper.skripsi.misc.Config.BASE_URL_OWM;

public class OWMClient {

    public static Retrofit retrofit = null;

    public static Retrofit getApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_OWM)
                    .addConverterFactory(retrofit2.converter.scalars.ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
