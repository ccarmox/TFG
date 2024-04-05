/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.classes;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;

public class ParserCache {

    double minLatitude = 361;
    double maxLatitude = -361;

    double minLongitude = 361;
    double maxLongitude = -361;


    public ParserCache() {
        minLongitude = -8.763113;
        maxLongitude = -8.753758;
        minLatitude = 42.206713;
        maxLatitude = 42.214913;
    }

    public void refresh(ArrayList<Vertice> nudos) {
        for (Vertice nudo : nudos) {
            refresh(nudo);
        }
    }

    public void refresh(Vertice nudo) {

        maxLatitude = Math.max(maxLatitude, nudo.getLatitud());
        minLatitude = Math.min(minLatitude, nudo.getLatitud());

        maxLongitude = Math.max(maxLongitude, nudo.getLongitud());
        minLongitude = Math.min(minLongitude, nudo.getLongitud());

    }

    public double getLatitudCentro() {
        return (minLatitude + maxLatitude) / 2D;
    }

    public double getLongitudCentro() {
        return (minLongitude + maxLongitude) / 2D;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }
}
