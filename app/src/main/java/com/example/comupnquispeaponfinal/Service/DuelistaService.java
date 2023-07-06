package com.example.comupnquispeaponfinal.Service;

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

public interface DuelistaService {
    @GET("Duelista")
    Call<List<Duelista>> getAllDuelista(@Query("limit") int limit, @Query("page") int page);

    @GET("Duelista/{id}")
    Call<Duelista> findDuelista(@Path("id") int id);

    @POST("Duelista")
    Call<Duelista> create(@Body Duelista duelista);

    @PUT("Duelista/{id}")
    Call<Duelista> update(@Path("id") int id, @Body Duelista duelista);

    @DELETE("Duelista/{id}")
    Call<Void> delete(@Path("id") int id);

    @POST("Duelista/upload")
    Call<Void> uploadDuelista(@Body List<Duelista> duelistas);
}
