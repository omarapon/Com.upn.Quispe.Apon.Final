package com.example.comupnquispeaponfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.comupnquispeaponfinal.BD.AppDatabase;
import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.CartaRepository;
import com.example.comupnquispeaponfinal.Repositoris.DuelistaRepository;
import com.example.comupnquispeaponfinal.Service.DuelistaService;
import com.example.comupnquispeaponfinal.Utilies.RetrofiU;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DuelistasDetallesActivity extends AppCompatActivity {

    Duelista duelista;
    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duelistas_detalles);

        mRetrofit = RetrofiU.build();


        int position = getIntent().getIntExtra("position", 0);

        AppDatabase db = AppDatabase.getInstance(this);
        DuelistaRepository repository = db.duelistaRepository();
        Duelista duelista1 = repository.findDuelistaById(position);

        TextView tvNombre = findViewById(R.id.tvnombredueslista);
        Button tvSincro = findViewById(R.id.dbtnSincro);
        Button bttnRegistrar = findViewById(R.id.dbtnRegistrar);
        Button bttnVerC = findViewById(R.id.dbtnvercarta);
        tvNombre.setText(duelista1.getNombre());
        tvSincro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!duelista1.getSincro()) {
                    DuelistaService service = mRetrofit.create(DuelistaService.class);
                    Duelista cuenta = new Duelista();
                    cuenta.setNombre(tvNombre.getText().toString());
                    cuenta.setSincro(true);

                    Call<Duelista> call = service.create(cuenta);

                    call.enqueue(new Callback<Duelista>() {
                        @Override
                        public void onResponse(Call<Duelista> call, Response<Duelista> response) {
                            Log.i("MAIN_APP",  String.valueOf(response.code()));

                            Intent intent =  new Intent(DuelistasDetallesActivity.this, ListaDuelistaActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<Duelista> call, Throwable t) {

                        }
                    });
                }
            }
        });


        bttnRegistrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    bttnVerC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                }
            }



