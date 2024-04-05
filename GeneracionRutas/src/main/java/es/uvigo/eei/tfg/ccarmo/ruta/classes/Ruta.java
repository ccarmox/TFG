/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.classes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;

public class Ruta {

    @NonNull
    private final String nombreDB;
    private double distancia;
    private boolean isArea;

    @Nullable
    private ArrayList<PuntoCalor> mapaCalor;

    private SQLite rutaDB;

    public Ruta(@NonNull String nombreDB, double distancia, @Nullable ArrayList<PuntoCalor> mapaCalor) {
        this.nombreDB = nombreDB;
        this.distancia = distancia;
        this.mapaCalor = mapaCalor;
    }

    @NonNull
    public String getNombreDB() {
        return nombreDB;
    }

    @NonNull
    public SQLite getDB(@NonNull Context context) {
        if (rutaDB == null) {
            rutaDB = SQLite.getBaseDeDatos(getNombreDB(), context);
        }
        return rutaDB;
    }

    public void cerrar() {
        if (rutaDB != null) {
            rutaDB.close();
            rutaDB = null;
        }
    }

    public boolean isArea() {
        return isArea;
    }

    public void setArea(boolean area) {
        isArea = area;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    @Nullable
    public ArrayList<PuntoCalor> getMapaCalor() {
        return mapaCalor;
    }

    public void setMapaCalor(@Nullable ArrayList<PuntoCalor> mapaCalor) {
        this.mapaCalor = mapaCalor;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "nombreDB='" + nombreDB + '\'' +
                ", distancia=" + distancia +
                '}';
    }
}
