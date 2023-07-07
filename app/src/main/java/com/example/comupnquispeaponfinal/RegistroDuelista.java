package com.example.comupnquispeaponfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.comupnquispeaponfinal.BD.AppDatabase;
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.DuelistaRepository;
import com.example.comupnquispeaponfinal.Service.DuelistaService;
import com.example.comupnquispeaponfinal.Utilies.RetrofiU;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistroDuelista extends AppCompatActivity {

    Retrofit mRetrofit;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_duelista);

        Button btnRegistrar = findViewById(R.id.btnRegistarD);
        Button btnListado = findViewById(R.id.btnListadoD);
        EditText etNombre = findViewById(R.id.etDuelista);

        mRetrofit = RetrofiU.build();


        btnListado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroDuelista.this, ListaDuelistaActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isNetworkConnected()) {
                    // llamar a retrofit
                    AppDatabase database = AppDatabase.getInstance(context);
                    DuelistaRepository cuentaRepository = database.duelistaRepository();

                    // Obtener el último ID registrado en la base de datos
                    int lastId = cuentaRepository.getLastId();

                    Duelista duelista = new Duelista();
                    duelista.setId(lastId + 1); // Asignar el nuevo ID incrementado en uno
                    duelista.setNombre(etNombre.getText().toString());
                    duelista.setSincro(false);

                    cuentaRepository.create(duelista);
                    Log.i("MAIN_APP: DB", new Gson().toJson(duelista));

                }
                else {
                    DuelistaService service = mRetrofit.create(DuelistaService.class);
                    Duelista duelista = new Duelista();
                    duelista.setNombre(etNombre.getText().toString());
                    duelista.setSincro(true);

                    Call<Duelista> call = service.create(duelista);
                    call.enqueue(new Callback<Duelista>() {
                        @Override
                        public void onResponse(Call<Duelista> call, Response<Duelista> response) {

                        }

                        @Override
                        public void onFailure(Call<Duelista> call, Throwable t) {

                        }
                    });

                }
                Intent intent = new Intent(RegistroDuelista.this, ListaDuelistaActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}