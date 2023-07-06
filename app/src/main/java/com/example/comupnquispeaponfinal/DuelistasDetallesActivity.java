package com.example.comupnquispeaponfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.comupnquispeaponfinal.BD.AppDatabase;
import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.CartaRepository;
import com.example.comupnquispeaponfinal.Repositoris.DuelistaRepository;
import com.example.comupnquispeaponfinal.Utilies.RetrofiU;

import java.util.List;

import retrofit2.Retrofit;

public class DuelistasDetallesActivity extends AppCompatActivity {

    Duelista duelista;
    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duelistas_detalles);

        mRetrofit = RetrofiU.build();

        float saldoF = 0;

        int position = getIntent().getIntExtra("position", 0);

        AppDatabase db = AppDatabase.getInstance(this);
        DuelistaRepository repository = db.duelistaRepository();
        Duelista duelista1 = repository.findDuelistaById(position);

        CartaRepository repositoryM = db.cartaRepository();
        List<Cartas> cartas = repositoryM.getCartaDuelista(position);

        TextView tvNombre = findViewById(R.id.tvnombredueslista);
        TextView tvSincro = findViewById(R.id.dbtnSincro);
        Button bttnRegistrar = findViewById(R.id.dbtnRegistrar);
        Button bttnVerC = findViewById(R.id.dbtnvercarta);
        tvNombre.setText(duelista.getNombre());


    }
}