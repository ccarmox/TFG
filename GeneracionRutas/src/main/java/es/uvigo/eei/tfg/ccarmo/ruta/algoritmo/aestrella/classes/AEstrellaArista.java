/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.aestrella.classes;


import androidx.annotation.NonNull;

public class AEstrellaArista {
    private final AEstrellaVertice nudoSimple;
    private final Integer distancia;

    public AEstrellaArista(@NonNull AEstrellaVertice nudoSimple, @NonNull Integer distancia) {
        this.nudoSimple = nudoSimple;
        this.distancia = distancia;
    }

    public AEstrellaVertice getNudoSimple() {
        return nudoSimple;
    }

    public Integer getDistancia() {
        return distancia;
    }
}
