package com.example.comupnquispeaponfinal.Service;

import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.Clases.Duelista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CartaService {
    @GET("Cartas")
    Call<List<Cartas>> getAllCartas(@Query("limit") int limit, @Query("page") int page);

    @GET("Cartas/{id}")
    Call<Cartas> findCartas(@Path("id") int id);

    @POST("Cartas")
    Call<Cartas> create(@Body Cartas cartas);

    @PUT("Cartas/{id}")
    Call<Cartas> update(@Path("id") int id, @Body Cartas cartas);

    @DELETE("Cartas/{id}")
    Call<Void> delete(@Path("id") int id);

    @POST("Cartas/upload")
    Call<Void> uploadCartas(@Body List<Cartas> cartas);
}
