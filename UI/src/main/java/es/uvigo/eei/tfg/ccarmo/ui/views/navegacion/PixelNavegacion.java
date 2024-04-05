/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.navegacion;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;

public class PixelNavegacion {


    private final double latitude;
    private final double longitude;

    public PixelNavegacion(LatLon latLon, LatLon userLocation, double giro) {
        this(NavegacionView.rotarPunto(latLon, userLocation, giro).getLatitud(), NavegacionView.rotarPunto(latLon, userLocation, giro).getLongitud());
    }

    public PixelNavegacion(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public int getX(double latitude1, double latitude2, double longitude1, double longitude2, NavegacionView userNavigationUI) {

        int w = userNavigationUI.getWidth();

        return (int) ((w / (longitude2 - longitude1)) * (longitude - longitude1));
    }

    public int getY(double latitude1, double latitude2, double longitude1, double longitude2, NavegacionView userNavigationUI) {

        int w = userNavigationUI.getWidth();
        int h = userNavigationUI.getHeight();

        latitude2 = (double) h / (double) w * (longitude2 - longitude1) + latitude1;
        return h - (int) ((h / (latitude2 - latitude1)) * (latitude - latitude1));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}