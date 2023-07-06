package com.example.comupnquispeaponfinal.Repositoris;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.comupnquispeaponfinal.Clases.Duelista;

import java.util.List;

@Dao
public interface DuelistaRepository {

    @Query("SELECT * FROM Duelistas")
    List<Duelista> getAll();

    @Insert
    void create(Duelista duelista);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Duelista> duelistas);

    @Query("SELECT * FROM Duelistas WHERE sincro = 0")
    List<Duelista> getUnSincro();

    @Update
    void updateDuelista(Duelista duelista);

    @Query("SELECT MAX(id) FROM Duelistas")
    int getLastId();

    @Query("SELECT * FROM Duelistas WHERE id = :Id")
    Duelista findDuelistaById(int Id);

}
