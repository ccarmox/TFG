/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2021-2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferences2 {

    private final String db;
    private final Context context;

    public SharedPreferences2(Context context) {
        this.db = "Datos";
        this.context = context;
    }

    public SharedPreferences2(String db, Context context) {
        this.db = db;
        this.context = context;
    }

    public String leer(String variable, String predefinido) {

        String valor = "";
        try {
            SharedPreferences prefs16 = context.getSharedPreferences(db,
                    Context.MODE_PRIVATE);

            valor = prefs16.getString(variable, predefinido);
        } catch (Exception e) {
            valor = predefinido;
        }

        return valor;
    }

    public int leer(String variable, int predefinido) {

        int valor = 0;
        try {
            SharedPreferences prefs16 = context.getSharedPreferences(db,
                    Context.MODE_PRIVATE);

            valor = prefs16.getInt(variable, predefinido);
        } catch (Exception e) {
            valor = predefinido;
        }

        return valor;
    }

    public boolean leer(String variable, boolean predefinido) {

        boolean valor;
        try {
            SharedPreferences prefs16 = context.getSharedPreferences(db,
                    Context.MODE_PRIVATE);

            valor = prefs16.getBoolean(variable, predefinido);
        } catch (Exception e) {
            valor = predefinido;
        }
        return valor;
    }

    public long leer(String variable, long predefinido) {
        return Long.parseLong(leer(variable, "" + predefinido));
    }


    public void guardar(String variable, String valor) {

        SharedPreferences prefs73 = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor73 = prefs73.edit();

        editor73.putString(variable, valor);
        editor73.commit();


    }

    public void borrar(String variable) {

        SharedPreferences prefs73 = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor73 = prefs73.edit();

        editor73.remove(variable);
        editor73.commit();

    }

    public void guardar(String variable, int valor) {

        SharedPreferences prefs73 = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor73 = prefs73.edit();

        editor73.putInt(variable, valor);
        editor73.commit();


    }

    public void guardar(String variable, boolean valor) {

        SharedPreferences prefs73 = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor73 = prefs73.edit();

        editor73.putBoolean(variable, valor);
        editor73.commit();


    }

    public void guardar(String variable, long valor) {
        guardar(variable, valor + "");
    }

}