/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.clases.classes;

import android.location.Location;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import es.uvigo.eei.tfg.ccarmo.clases.listeners.InterfazIdentificable;


public class LatLon implements Serializable, InterfazIdentificable {

    private final String id;
    private double latitud;
    private double longitud;
    private double altitud;
    private int resolucion;

    public LatLon(@NonNull LatLon latLon) {
        this.altitud = latLon.altitud;
        this.latitud = latLon.latitud;
        this.longitud = latLon.longitud;
        this.resolucion = latLon.resolucion;
        this.id = latLon.id;
    }

    public LatLon(double latitude, double longitude, double altitude) {
        this(latitude, longitude, altitude, Math.max(getResolucion(latitude), getResolucion(longitude)));
    }

    public LatLon(double latitude, double longitude, double altitude, int resolucion) {
        this(generarID(latitude, longitude, altitude, resolucion), latitude, longitude, altitude, resolucion);
    }

    public LatLon(@NonNull String id, double latitude, double longitude, double altitude, int resolucion) {
        this.latitud = setResolucion(latitude, resolucion);
        this.longitud = setResolucion(longitude, resolucion);
        this.altitud = setResolucion(altitude, resolucion);
        this.resolucion = resolucion;
        this.id = id;
    }


    private static double setResolucion(double parametro, int resolucion) {
        return BigDecimal.valueOf(parametro)
                .setScale(resolucion, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static int getResolucion(double parametro) {
        return BigDecimal.valueOf(parametro).scale();
    }

    private static String generarID(double latitude, double longitude, double altitude, int resolucion) {

        String la = generarID(latitude, resolucion);
        String lo = generarID(longitude, resolucion);
        String al = generarIDAltitud(altitude);

        return la + "|" + lo + "|" + al;

    }

    private static String generarID(double coordenada, int resolucion) {
        String[] a = String.valueOf(coordenada).replace(",", ".").split("[.]");
        if (a[1].length() == resolucion) {
            return a[0] + "." + a[1];
        }
        String b = a[1];
        while (b.length() < resolucion) {
            b = b + "0";
        }
        return a[0] + "." + b;
    }

    private static String generarIDAltitud(double coordenada) {
        //La marca de altitud se actualiza cada 5 metros de altura
        //De esta forma se evita que dos dispositivos generen cotas distintas cuando la diferencia es epequeña
        return String.valueOf(Math.round(coordenada / 5) * 5);
    }

    public static double getDistanciaFisica(LatLon a, LatLon b) {
        float[] distancias = new float[3];
        Location.distanceBetween(a.getLatitud(), a.getLongitud(), b.getLatitud(), b.getLongitud(), distancias);
        return distancias[0];
    }

    @Override
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    @Override
    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public double getAltitud() {
        return altitud;
    }

    public void setAltitud(double altitud) {
        this.altitud = altitud;
    }

    @NonNull
    public LatLon clone() {
        return new LatLon(this);
    }

    public int getResolucion() {
        return resolucion;
    }

    public void setResolucion(int resolucion) {
        this.resolucion = resolucion;
    }

    @Override
    public String getID() {
        return id;
    }

    public double getDistanciaFisicaA(LatLon latLon) {
        return getDistanciaFisica(this, latLon);
    }

    @NonNull
    @Override
    public String toString() {
        return "LatLon{" +
                "\nlatitude=" + latitud +
                "\nlongitude=" + longitud +
                "\naltitude=" + altitud +
                "\n}";
    }

}
