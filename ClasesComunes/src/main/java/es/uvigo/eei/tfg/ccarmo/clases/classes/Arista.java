/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.clases.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Arista implements Serializable, Comparable<Arista> {
    public final Integer cost;
    public final String id;

    public Arista(String id, Integer costVal) {
        this.id = id;
        cost = costVal;
    }

    public Integer getCoste() {
        return cost;
    }

    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nUnion{" +
                "\ncost=" + cost +
                ", \nid='" + id + '\'' +
                "\n}";
    }

    @Override
    public int compareTo(Arista b) {
        return cost - b.cost;
    }
}
