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
import android.net.ConnectivityManager;
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
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.CartaRepository;
import com.example.comupnquispeaponfinal.Service.CartaService;
import com.example.comupnquispeaponfinal.Service.DuelistaService;
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
                Cartas carta = new Cartas();
                //Llenar
                carta.setNombre(nombre);
                carta.setPuntosdeataque(ataquexd);
                carta.setPuntosdedefensa(defensaxd);
                carta.setIdDuelista(idDuelista+"");
                carta.setLongitud(longitud+"");
                carta.setLatitud(latitud+"");

                if(!isNetworkConnected()) {
                    //Crear una Carta en la base de datos
                    carta.setId(lastId + 1);
                    carta.setImagen("https://i.imgur.com/DvpvklR.png");
                    carta.setSincro(false);
                    cartasRepoy.create(carta);
                }
                else {
                    //Crear una Carta en el Mockapi
                    carta.setSincro(true);
                    //Como Api funciona con internet, se manda url predeterminado
                    carta.setImagen(urlCamara);
                    CartaService service = retrofitC.create(CartaService.class);

                    Call<Cartas> call = service.create(carta);

                    call.enqueue(new Callback<Cartas>() {
                        @Override
                        public void onResponse(Call<Cartas> call, Response<Cartas> response) {

                        }

                        @Override
                        public void onFailure(Call<Cartas> call, Throwable t) {

                        }
                    });
                }

                Intent intent =  new Intent(RegistroCartaActivity.this, ListaDuelistaActivity.class);
                startActivity(intent);
                finish();

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

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST);
    }

    private void enviarImagen (CartaService.ImageToSave imgB64){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlFotoApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CartaService service = retrofit.create(CartaService.class);
        Call<CartaService.ImageResponse> call = service.subirImagen(imgB64);
        call.enqueue(new Callback<CartaService.ImageResponse>() {
            @Override
            public void onResponse(Call<CartaService.ImageResponse> call, Response<CartaService.ImageResponse> response) {
                if(response.isSuccessful()){
                    Log.i("MAIN_APP", "Si se subió");
                    Log.i("MAIN_APP", urlFotoApi  + response.body().getUrl());
                    urlCamara = urlFotoApi + response.body().getUrl();
                }
                else{
                    Log.i("MAIN_APP", "No se subió");
                }
            }
            @Override
            public void onFailure(Call<CartaService.ImageResponse> call, Throwable t) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OPEN_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close(); // close cursor

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ivAvatar.setImageBitmap(bitmap);
            //Convertir a base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            CartaService.ImageToSave imgB64 = new CartaService.ImageToSave(imgBase64);
            enviarImagen(imgB64);
        }

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
