/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta;

import android.content.Context;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Configuracion;
import es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.aestrella.AEstrella;
import es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.dijkstra.Dijkstra;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.AlgoritmoBase;
import es.uvigo.eei.tfg.ccarmo.utils.SharedPreferences2;

public class Algoritmo {

    @NonNull
    public static AlgoritmoBase getAlgoritmo(@NonNull String nombreDB, @NonNull Configuracion configuracion, @NonNull Context context) {

        METODO metodo = METODO.AESTRELLA;

        if (isAEstrella(context)) {
            metodo = METODO.AESTRELLA;
        }

        if (isDijkstra(context)) {
            metodo = METODO.DIJKSTRA;
        }

        return getAlgoritmo(metodo, nombreDB, configuracion, context);

    }

    @NonNull
    public static AlgoritmoBase getAlgoritmo(@NonNull METODO metodo, @NonNull String nombreDB, @NonNull Configuracion configuracion, @NonNull Context context) {

        switch (metodo) {
            case DIJKSTRA:
                return new Dijkstra(nombreDB, configuracion, context);
            case AESTRELLA:
            default:
                return new AEstrella(nombreDB, configuracion, context);
        }

    }

    public static boolean isAEstrella(@NonNull Context context) {
        SharedPreferences2 preferencias = new SharedPreferences2(context);
        return preferencias.leer(PREFERENCIAS.AESTRELLA, true);
    }

    public static boolean isDijkstra(@NonNull Context context) {
        SharedPreferences2 preferencias = new SharedPreferences2(context);
        return preferencias.leer(PREFERENCIAS.DIJKSTRA, false);
    }

    public static void setAEstrella(@NonNull Context context) {
        SharedPreferences2 preferencias = new SharedPreferences2(context);
        preferencias.guardar(PREFERENCIAS.AESTRELLA, true);
        preferencias.guardar(PREFERENCIAS.DIJKSTRA, false);
    }

    public static void setDijkstra(@NonNull Context context) {
        SharedPreferences2 preferencias = new SharedPreferences2(context);
        preferencias.guardar(PREFERENCIAS.AESTRELLA, false);
        preferencias.guardar(PREFERENCIAS.DIJKSTRA, true);
    }

    public enum METODO {
        AESTRELLA,
        DIJKSTRA
    }

    public static final class PREFERENCIAS {
        public static final String AESTRELLA = "estrella";
        public static final String DIJKSTRA = "dijkstra";
    }

}
