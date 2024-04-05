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

import es.uvigo.eei.tfg.ccarmo.clases.listeners.InterfazMapa;


public class Pixel extends LatLon implements Serializable {

    private int x;
    private int y;

    public Pixel() {
        super(new LatLon(0, 0, 0));
    }

    public Pixel(@NonNull LatLon latLon, @NonNull InterfazMapa mapa2) {
        super(latLon);
        referenciar(mapa2);
    }

    public Pixel(double latitude, double longitude, @NonNull InterfazMapa mapa2) {
        super(latitude, longitude, 0);
        referenciar(mapa2);
    }


    public Pixel(double latitude, double longitude, double altitude) {
        super(latitude, longitude, altitude);
    }

    public Pixel(double latitude, double longitude, double altitude, int resolucion) {
        super(latitude, longitude, altitude, resolucion);
    }

    public void referenciar(InterfazMapa mapa2) {
        referenciar(getLatitud(), getLongitud(), mapa2);
    }

    public void referenciar(double lat, double lon, InterfazMapa mapa2) {

        //Calculo las coordenadas para un eje inicial
        double fy = (((double) mapa2.getAlto() / (double) mapa2.getDiferencialLatitud()) * ((double) (lat - mapa2.getLatitudCentro())));
        double fx = (((double) mapa2.getAncho() / (double) mapa2.getDiferencialLongitud()) * ((double) (lon - mapa2.getLongitudCentro())));

        // Calcular nuevas coordenadas después de la rotación
        double xPrima = fx * mapa2.getCosenoGiro() + fy * mapa2.getSenoGiro();
        double yPrima = -fx * mapa2.getSenoGiro() + fy * mapa2.getCosenoGiro();

        // Guardo los valores
        x = (int) Math.round(xPrima);
        y = (int) Math.round(yPrima);

        //Adapto los valores a la forma de dibujar en Android (Cuarto cuadrante con eje Y positivo hacia abajo)
        x = x + mapa2.getAncho() / 2;
        y = -(y - mapa2.getAlto() / 2);

    }

    public void invertirReferencia(int x, int y, InterfazMapa mapa2) {

        this.x = x;
        this.y = y;

        //Adapto los valores a la forma de dibujar en Android (Cuarto cuadrante)
        x = x - mapa2.getAncho() / 2;
        y = -y + mapa2.getAlto() / 2;

        // Calcular nuevas coordenadas después de la rotación
        double xPrima = x * mapa2.getCosenoGiro() - y * mapa2.getSenoGiro();
        double yPrima = x * mapa2.getSenoGiro() + y * mapa2.getCosenoGiro();

        // Guardo los valores
        x = (int) Math.round(xPrima);
        y = (int) Math.round(yPrima);

        double lat = y * (mapa2.getDiferencialLatitud() / mapa2.getAlto()) + mapa2.getLatitudCentro();
        double lon = x * (mapa2.getDiferencialLongitud() / mapa2.getAncho()) + mapa2.getLongitudCentro();

        setLatitud(lat);
        setLongitud(lon);
        setAltitud(0);
        setResolucion(Math.max(getResolucion(lat), getResolucion(lon)));

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
