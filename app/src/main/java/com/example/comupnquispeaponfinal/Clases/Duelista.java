package com.example.comupnquispeaponfinal.Clases;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Duelistas")
public class Duelista {
    @PrimaryKey()
    public int id;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "sincro")
    private Boolean sincro;

    public Boolean getSincro() {
        return sincro;
    }

    public void setSincro(Boolean sincro) {
        this.sincro = sincro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
