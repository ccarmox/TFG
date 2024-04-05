/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//Clase para calcular los retrasos de cada metodo
public class Cronometro {

    private final String tag;
    private long tiempo;
    private int contador;

    public Cronometro(@NonNull String tag) {
        this.tag = tag;
        this.contador = 0;
        comenzar();
    }

    public void comenzar() {
        actualizar();
    }

    private void actualizar() {
        this.tiempo = System.currentTimeMillis();
    }

    public void print() {
        print(null);
    }

    public void print(@Nullable String marca) {
        long transcurrido = (System.currentTimeMillis() - tiempo);
        actualizar();

        if (tag != null) {
            android.util.Log.v(tag, marca + " => " + transcurrido + " ms.");
        } else {
            contador++;
            android.util.Log.v(tag, "Marca[" + contador + "] => " + transcurrido + " ms.");
        }
    }

    public void reiniciar() {
        contador = 0;
        actualizar();
    }
}
