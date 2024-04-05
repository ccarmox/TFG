
/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OSMFrontera {

    @JacksonXmlProperty(localName = "minlat", isAttribute = true)
    private String latitudMinima;
    @JacksonXmlProperty(localName = "minlon", isAttribute = true)
    private String longitudMinima;
    @JacksonXmlProperty(localName = "maxlat", isAttribute = true)
    private String latitudMaxima;
    @JacksonXmlProperty(localName = "maxlon", isAttribute = true)
    private String longitudMaxima;

    public String getLatitudMinima() {
        return latitudMinima;
    }

    public void setLatitudMinima(String latitudMinima) {
        this.latitudMinima = latitudMinima;
    }

    public String getLongitudMinima() {
        return longitudMinima;
    }

    public void setLongitudMinima(String longitudMinima) {
        this.longitudMinima = longitudMinima;
    }

    public String getLatitudMaxima() {
        return latitudMaxima;
    }

    public void setLatitudMaxima(String latitudMaxima) {
        this.latitudMaxima = latitudMaxima;
    }

    public String getLongitudMaxima() {
        return longitudMaxima;
    }

    public void setLongitudMaxima(String longitudMaxima) {
        this.longitudMaxima = longitudMaxima;
    }

    public double getMinLatitude() {
        return Double.parseDouble(getLatitudMinima());
    }

    public double getMinLongitude() {
        return Double.parseDouble(getLongitudMinima());
    }

    public double getMaxLatitude() {
        return Double.parseDouble(getLatitudMaxima());
    }

    public double getMaxLongitude() {
        return Double.parseDouble(getLongitudMaxima());
    }

}
