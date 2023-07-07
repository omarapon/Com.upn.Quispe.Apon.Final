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

public class ListaCartaActivity extends AppCompatActivity {

    RecyclerView mRvLista;
    boolean mIsLoading = false;
    int mPage = 1;
    List<Cartas> cdata = new ArrayList<>();
    List<Duelista> ddata = new ArrayList<>();
    CartasAdapter mAdapter = new CartasAdapter(cdata, this);
    DuelistaAdapter cAdapter = new DuelistaAdapter(ddata, this);
    Retrofit RetrofitC;
    Context context = this;
    String currentFilter = "";
    int idDuel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_carta);

        idDuel = getIntent().getIntExtra("position", 0); //RECIVI EL POKEMON EXACTO
        RetrofitC = RetrofiU.build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvLista =  findViewById(R.id.rvListaCartas);
        mRvLista.setLayoutManager(layoutManager);
        mRvLista.setAdapter(mAdapter);

        Button btnVolver = findViewById(R.id.VolverC);


        AppDatabase db = AppDatabase.getInstance(context);
        CartaRepository repository = db.cartaRepository();
        List<Cartas> cartas = repository.getAll(); //mandamos la lista de los pokemones
        Log.i("MAIN_APP: DB", new Gson().toJson(cartas));
        Log.i("MAIN_APP", idDuel+"");
        mAdapter.setCartas(cartas);
        mAdapter.notifyDataSetChanged();


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(ListaCartaActivity.this, ListaDuelistaActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mRvLista.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadMore(mPage);
                    }
                }

            }
        });

        mRvLista.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadMore(mPage);
                    }
                }

            }
        });


    }
    private void uploadToWebService(int nextPage) {

        AppDatabase db = AppDatabase.getInstance(context);
        db.clearAllTables();

        CartaService service = RetrofitC.create(CartaService.class);
        service.getAllCartas(currentFilter, 50, nextPage).enqueue(new Callback<List<Cartas>>() {
            @Override
            public void onResponse(Call<List<Cartas>> call, Response<List<Cartas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaCartaActivity.this);
                    CartaRepository repository = db.cartaRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Cartas> newData = repository.getAll();
                    mAdapter.setCartas(newData);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Cartas>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });


        DuelistaService service2 = RetrofitC.create(DuelistaService.class);
        service2.getAllDuelista(currentFilter, 50, nextPage).enqueue(new Callback<List<Duelista>>() {
            @Override
            public void onResponse(Call<List<Duelista>> call, Response<List<Duelista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaCartaActivity.this);
                    DuelistaRepository repository = db.duelistaRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Duelista> newData = repository.getAll();
                    cAdapter.setDuelista(newData);
                    cAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Duelista>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void loadMore(int nextPage) {
        mIsLoading = true;

        cdata.add(null);
        mAdapter.notifyItemInserted(cdata.size() - 1);

        CartaService service = RetrofitC.create(CartaService.class);
        Log.i("MAIN_APP  Page:", String.valueOf(nextPage));
        service.getAllCartas(currentFilter,100, nextPage).enqueue(new Callback<List<Cartas>>() { // Cambia el número de registros por página según tus necesidades
            @Override
            public void onResponse(Call<List<Cartas>> call, Response<List<Cartas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cdata.remove(cdata.size() - 1);
                    mAdapter.notifyItemRemoved(cdata.size() - 1);

                    cdata.addAll(response.body());
                    mAdapter.notifyDataSetChanged();
                    mIsLoading = false;

                    // Si hay más registros disponibles, cargar la siguiente página
                    if (response.body().size() >= 100) { // Cambia el número de registros por página según tus necesidades
                        loadMore(nextPage + 1);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Cartas>> call, Throwable t) {
                // Manejar error de la llamada al servicio MockAPI
            }
        });
    }

}