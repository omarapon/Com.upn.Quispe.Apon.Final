package com.example.comupnquispeaponfinal.Utilies;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofiU {
    public static Retrofit build() {
        return new Retrofit.Builder()
                .baseUrl("https://6477447c9233e82dd53b4dd6.mockapi.io/") // revisar
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
