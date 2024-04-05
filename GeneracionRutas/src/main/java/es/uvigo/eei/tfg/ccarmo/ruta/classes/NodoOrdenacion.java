/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.classes;

public class NodoOrdenacion implements Comparable<NodoOrdenacion> {

    public final String id;
    public final Integer distanciaEstimadaAOrigen;

    public NodoOrdenacion(String id, Integer distanciaEstimadaAOrigen) {
        this.id = id;
        this.distanciaEstimadaAOrigen = distanciaEstimadaAOrigen;
    }

    @Override
    public int compareTo(NodoOrdenacion o) {
        return distanciaEstimadaAOrigen.compareTo(o.distanciaEstimadaAOrigen);
    }
}
