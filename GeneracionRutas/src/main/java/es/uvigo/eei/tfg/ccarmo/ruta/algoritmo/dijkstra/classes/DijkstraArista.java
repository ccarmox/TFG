/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.dijkstra.classes;


public class DijkstraArista {
    private final DijkstraVertice nudoSimple;
    private final Integer distancia;

    public DijkstraArista(DijkstraVertice nudoSimple, Integer distancia) {
        this.nudoSimple = nudoSimple;
        this.distancia = distancia;
    }

    public DijkstraVertice getNudoSimple() {
        return nudoSimple;
    }

    public Integer getDistancia() {
        return distancia;
    }

}
