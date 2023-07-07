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
        Button bttnRegistrar = findViewById(R.id.dbtnRegistrar);
        Button bttnVerC = findViewById(R.id.dbtnvercarta);
        tvNombre.setText(duelista1.getNombre());


        bttnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(DuelistasDetallesActivity.this, RegistroCartaActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                finish();
            }
        });
        bttnVerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(DuelistasDetallesActivity.this, ListaCartaActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                finish();
            }
        });

    }
}



