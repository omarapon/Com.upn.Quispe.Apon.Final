package com.example.comupnquispeaponfinal.Service;

import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.google.gson.annotations.SerializedName;

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

    @POST("image")
    Call<ImageResponse> subirImagen(@Body ImageToSave imagen);

    class ImageResponse {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }

    class ImageToSave {
        String base64Image;

        public ImageToSave(String base64Image) {
            this.base64Image = base64Image;
        }

    }
}
