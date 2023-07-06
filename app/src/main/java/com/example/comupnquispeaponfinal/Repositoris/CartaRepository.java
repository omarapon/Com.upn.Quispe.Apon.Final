package com.example.comupnquispeaponfinal.Repositoris;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.comupnquispeaponfinal.Clases.Cartas;

import java.util.List;

@Dao
public interface CartaRepository {
    @Query("SELECT * FROM Cartas")
    List<Cartas> getAll();

    @Insert
    void create(Cartas cartas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Cartas> movimientos);

    @Query("SELECT * FROM Cartas WHERE sincro = 0")
    List<Cartas> getUnsyncedCarta();

    @Update
    void updateCarta(Cartas cartas);

    @Query("SELECT * FROM Cartas WHERE idDuelista = :idDuelista")
    List<Cartas> getCartaDuelista(int idDuelista);

}
