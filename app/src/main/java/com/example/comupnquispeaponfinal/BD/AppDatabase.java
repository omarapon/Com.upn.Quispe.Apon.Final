package com.example.comupnquispeaponfinal.BD;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.Repositoris.CartaRepository;
import com.example.comupnquispeaponfinal.Repositoris.DuelistaRepository;

@Database(entities = {Duelista.class, Cartas.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DuelistaRepository duelistaRepository();
    public abstract CartaRepository cartaRepository();

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "ExamenDB")
                .allowMainThreadQueries()
                .build();
    }


}
