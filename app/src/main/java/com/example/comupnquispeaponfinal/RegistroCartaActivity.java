package com.example.comupnquispeaponfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import  android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.comupnquispeaponfinal.BD.AppDatabase;
import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.Repositoris.CartaRepository;
import com.example.comupnquispeaponfinal.Service.CartaService;
import com.example.comupnquispeaponfinal.Utilies.RetrofiU;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroCartaActivity extends AppCompatActivity {
    private static final String urlFotoApi= "https://demo-upn.bit2bittest.com/";
    private static final int OPEN_CAMERA_REQUEST = 1001;
    private static final int OPEN_GALLERY_REQUEST = 1002;

    Retrofit retrofitC;
    Context context = this;
    int idDuelista;

    private ImageView ivAvatar;
    private String urlCamara; //settear camara
    private LocationManager mlocationManager; //para mapas
    double latitud = 0;
    double longitud = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_carta);
        EditText cartanombre = findViewById(R.id.nombrecartaxd);
        EditText puntosAtaque = findViewById(R.id.puntosdeataqueca);
        EditText puntosDefensa = findViewById(R.id.puntosdedefensaca);
        ivAvatar = findViewById(R.id.imageViewCarta);

        Button btnRegistro = findViewById(R.id.RegistrarcartaCar);
        Button btnubicacion = findViewById(R.id.ubicacioncarta);
        Button btnCamara = findViewById(R.id.camaracarta);
        Button btnGaleria = findViewById(R.id.imagencarta);
        retrofitC = RetrofiU.build(); //settear mockapi

        idDuelista = getIntent().getIntExtra("position", 0); //RECIVI EL POKEMON EXACTO

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = cartanombre.getText().toString();
                String ataquexd = puntosAtaque.getText().toString();
                String defensaxd = puntosDefensa.getText().toString();

                //Crear un service del repository
                AppDatabase database = AppDatabase.getInstance(context);
                CartaRepository cartasRepoy = database.cartaRepository();

                // Obtener el último ID registrado en la base de datos para crear uno nuevo
                int lastId = cartasRepoy.getLastId();

                //Crear un movimiento
                Cartas carta = new Cartas();
                carta.setId(lastId + 1);
                //Llenar
                carta.setNombre(nombre);
                carta.setPuntosdeataque(ataquexd);
                carta.setPuntosdedefensa(defensaxd);
                carta.setIdDuelista(idDuelista+"");
                carta.setImagen(urlCamara);
                carta.setLongitud(longitud+"");
                carta.setLatitud(latitud+"");

                cartasRepoy.create(carta);


                Intent intent =  new Intent(RegistroCartaActivity.this, ListaDuelistaActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOpenCamera();
                obtenerCoordenadas(); //para cargar coodenadas
            }
        });
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                    obtenerCoordenadas(); //para cargar coodenadas
                }
                else {
                    String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 2000);
                }
            }
        });


        btnubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RegistroCartaActivity.this, ListaDuelistaActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

  

    private void handleOpenCamera() {
        if(checkSelfPermission(Manifest.permission.CAMERA)  == PackageManager.PERMISSION_GRANTED)
        {
            // abrir camara
            Log.i("MAIN_APP", "Tiene permisos para abrir la camara");
            abrirCamara();
        } else {
            // solicitar el permiso
            Log.i("MAIN_APP", "No tiene permisos para abrir la camara, solicitando");
            String[] permissions = new String[] {Manifest.permission.CAMERA};
            requestPermissions(permissions, 1000);
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OPEN_CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST);
    }

    // ALMACENAR COORDENADAS
    void obtenerCoordenadas(){
        mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    Log.i("MAIN_APP", "Latitud" + latitud);
                    Log.i("MAIN_APP", "Longitud" + longitud);
                    mlocationManager.removeUpdates(this);
                }
            };
            mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
        }
        else{
            String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            Log.i("MAIN_APP", "No hay permisos pa esta webada");
            requestPermissions(permissions, 1000);
        }
    }

}
