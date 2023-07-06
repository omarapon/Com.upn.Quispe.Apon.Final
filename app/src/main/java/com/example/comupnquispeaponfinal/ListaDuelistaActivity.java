package com.example.comupnquispeaponfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comupnquispeaponfinal.Adapters.DuelistaAdapter;
import com.example.comupnquispeaponfinal.BD.AppDatabase;
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.DuelistaRepository;
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
    List<Duelista> cdata = new ArrayList<>();
    DuelistaAdapter mAdapter = new DuelistaAdapter(cdata, this);
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_duelista);

        RetrofitD = RetrofiU.build();
        Button btnRegistro = findViewById(R.id.registroduel);
        Button btnActualizar = findViewById(R.id.actualizardue);

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
                        loadMore(mPage);
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

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToWebService(1);
            }
        });

        AppDatabase db = AppDatabase.getInstance(context);
        DuelistaRepository repository = db.duelistaRepository();
        List<Duelista> duelistas = repository.getAll();
        Log.i("MAIN_APP: DB", new Gson().toJson(duelistas));
        mAdapter.setCuentas(duelistas);
        mAdapter.notifyDataSetChanged();

    }

    private void uploadToWebService(int nextPage) {

        AppDatabase db = AppDatabase.getInstance(context);
        db.clearAllTables();

        DuelistaService service = RetrofitD.create(DuelistaService.class);
        service.getAllDuelista(100, nextPage).enqueue(new Callback<List<Duelista>>() {
            @Override
            public void onResponse(Call<List<Duelista>> call, Response<List<Duelista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaDuelistaActivity.this);
                    DuelistaRepository repository = db.duelistaRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Duelista> newData = repository.getAll();
                    mAdapter.setCuentas(newData);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Duelista>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });
    }
    private void loadMore(int nextPage) {
        mIsLoading = true;

        cdata.add(null);
        mAdapter.notifyItemInserted(cdata.size() - 1);

        DuelistaService service = RetrofitD.create(DuelistaService.class);
        Log.i("MAIN_APP  Page:", String.valueOf(nextPage));
        service.getAllDuelista(100, nextPage).enqueue(new Callback<List<Duelista>>() { // Cambia el número de registros por página según tus necesidades
            @Override
            public void onResponse(Call<List<Duelista>> call, Response<List<Duelista>> response) {
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
            public void onFailure(Call<List<Duelista>> call, Throwable t) {
                // Manejar error de la llamada al servicio MockAPI
            }
        });
    }

}