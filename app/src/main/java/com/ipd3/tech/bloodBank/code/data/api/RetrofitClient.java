package com.ipd3.tech.bloodBank.code.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static String BASE_URL = "http://ipda3-tech.com/blood-bank/api/v1/";
    public static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl( BASE_URL )
                    .addConverterFactory( GsonConverterFactory.create() )
                    .build();

        }
        return retrofit;
    }
}