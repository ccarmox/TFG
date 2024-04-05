/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.classes;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.clases.listeners.InterfazIdentificable;

public class VerticeBase implements InterfazIdentificable {

    private final Vertice vertice;

    public VerticeBase(@NonNull Vertice vertice) {
        this.vertice = vertice;
    }

    public Vertice getVertice() {
        return vertice;
    }

    @Override
    public String getID() {
        return vertice.getID();
    }

    @Override
    public double getLatitud() {
        return vertice.getLatitud();
    }

    @Override
    public double getLongitud() {
        return vertice.getLongitud();
    }

    @Override
    public double getAltitud() {
        return vertice.getAltitud();
    }
}
