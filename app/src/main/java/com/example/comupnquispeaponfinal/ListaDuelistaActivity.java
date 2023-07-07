package com.example.comupnquispeaponfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comupnquispeaponfinal.Adapters.CartasAdapter;
import com.example.comupnquispeaponfinal.Adapters.DuelistaAdapter;
import com.example.comupnquispeaponfinal.BD.AppDatabase;
import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.CartaRepository;
import com.example.comupnquispeaponfinal.Repositoris.DuelistaRepository;
import com.example.comupnquispeaponfinal.Service.CartaService;
import com.example.comupnquispeaponfinal.Service.DuelistaService;
import com.example.comupnquispeaponfinal.Utilies.RetrofiU;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListaDuelistaActivity extends AppCompatActivity {

    Retrofit RetrofitD;
    RecyclerView mRvListaDuelista;
    boolean mIsLoading = false;
    int mPage = 1;
    String currentFilter = "";
    List<Duelista> cdata = new ArrayList<>();
    DuelistaAdapter mAdapter = new DuelistaAdapter(cdata, this);
    List<Cartas> datac = new ArrayList<>();
    CartasAdapter Adapterc = new CartasAdapter(datac, this);
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_duelista);

        RetrofitD = RetrofiU.build();
        Button btnRegistro = findViewById(R.id.registroduel);
        AppDatabase db = AppDatabase.getInstance(context);
        DuelistaRepository repository = db.duelistaRepository();
        List<Duelista> duelistas = repository.getAll();

        if(!isNetworkConnected()) {
            Log.i("MAIN_APP: DB", new Gson().toJson(duelistas));
            mAdapter.setDuelista(duelistas);
            mAdapter.notifyDataSetChanged();
        }
        else {
            UploadDuelistas(duelistas);
            loadMore(currentFilter, mPage);
            uploadToWebService(1);
            mAdapter.setDuelista(duelistas);
        }


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvListaDuelista =  findViewById(R.id.rvListaDuelistas);
        mRvListaDuelista.setLayoutManager(layoutManager);
        mRvListaDuelista.setAdapter(mAdapter);


        mRvListaDuelista.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!mIsLoading) {

                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == cdata.size() - 1) {
                        mPage++;
                        loadMore(currentFilter, mPage);
                    }
                }

            }
        });
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(ListaDuelistaActivity.this, RegistroDuelista.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



    private void UploadDuelistas(List<Duelista> duelistas){

        for (int i = 0; i<duelistas.size();i++){
            Duelista duelista = duelistas.get(i);
            if (!duelista.getSincro()) {
                DuelistaService service = RetrofitD.create(DuelistaService.class);
                Duelista aux = new Duelista();
                aux.setNombre(duelista.getNombre());
                aux.setSincro(true);

                Call<Duelista> call = service.create(aux);

                call.enqueue(new Callback<Duelista>() {
                    @Override
                    public void onResponse(Call<Duelista> call, Response<Duelista> response) {
                        Log.i("MAIN_APP",  String.valueOf(response.code()));
                    }

                    @Override
                    public void onFailure(Call<Duelista> call, Throwable t) {

                    }
                });
            }
        }


        AppDatabase db = AppDatabase.getInstance(context);
        CartaRepository repository = db.cartaRepository();
        List<Cartas> cartas = repository.getAll();

        for (int i = 0; i<cartas.size();i++){
            Cartas carta = cartas.get(i);
            if (!carta.getSincro()) {
                CartaService service = RetrofitD.create(CartaService.class);
                Cartas aux = new Cartas();
                aux.setNombre(carta.getNombre());
                aux.setPuntosdeataque(carta.getPuntosdeataque());
                aux.setPuntosdedefensa(carta.getPuntosdedefensa());
                aux.setLatitud(carta.getLatitud());
                aux.setLongitud(carta.getLongitud());
                aux.setImagen(carta.getImagen());
                aux.setIdDuelista(carta.getIdDuelista());
                aux.setSincro(true);

                Call<Cartas> call = service.create(aux);

                call.enqueue(new Callback<Cartas>() {
                    @Override
                    public void onResponse(Call<Cartas> call, Response<Cartas> response) {

                    }

                    @Override
                    public void onFailure(Call<Cartas> call, Throwable t) {

                    }
                });
            }
        }

    }

    private void uploadToWebService(int nextPage) {

        AppDatabase db = AppDatabase.getInstance(context);
        db.clearAllTables();

        DuelistaService service = RetrofitD.create(DuelistaService.class);
        service.getAllDuelista(currentFilter, 50, nextPage).enqueue(new Callback<List<Duelista>>() {
            @Override
            public void onResponse(Call<List<Duelista>> call, Response<List<Duelista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaDuelistaActivity.this);
                    DuelistaRepository repository = db.duelistaRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Duelista> newData = repository.getAll();
                    mAdapter.setDuelista(newData);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Duelista>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });


        CartaService service2 = RetrofitD.create(CartaService.class);
        service2.getAllCartas(currentFilter, 50, nextPage).enqueue(new Callback<List<Cartas>>() {
            @Override
            public void onResponse(Call<List<Cartas>> call, Response<List<Cartas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaDuelistaActivity.this);
                    CartaRepository repository = db.cartaRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Cartas> newData = repository.getAll();
                    Adapterc.setCartas(newData);
                    Adapterc.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Cartas>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });


    }
    private void loadMore(String filter, int nextPage) {
        mIsLoading = true;

        cdata.add(null);
        mAdapter.notifyItemInserted(cdata.size() - 1);

        DuelistaService service = RetrofitD.create(DuelistaService.class);
        Log.i("MAIN_APP  Page:", String.valueOf(nextPage));
        service.getAllDuelista(filter, 20, nextPage).enqueue(new Callback<List<Duelista>>() {
            @Override
            public void onResponse(Call<List<Duelista>> call, Response<List<Duelista>> response) {

                if(cdata.size() > 0) {
                    cdata.remove(cdata.size() - 1);
                    mAdapter.notifyItemRemoved(cdata.size() - 1);
                }

                cdata.addAll(response.body());
                mAdapter.notifyDataSetChanged();
                mIsLoading = false;
            }

            @Override
            public void onFailure(Call<List<Duelista>> call, Throwable t) {

            }
        });


    }

}