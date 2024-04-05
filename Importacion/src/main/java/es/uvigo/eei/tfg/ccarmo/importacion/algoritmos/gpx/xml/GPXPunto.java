/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

public class GPXPunto implements Serializable {

    @JacksonXmlProperty(localName = "lat", isAttribute = true)
    @JacksonXmlElementWrapper(useWrapping = false)
    private double latitud;

    @JacksonXmlProperty(localName = "lon", isAttribute = true)
    @JacksonXmlElementWrapper(useWrapping = false)
    private double longitud;

    @JacksonXmlProperty(localName = "ele")
    @JacksonXmlElementWrapper(useWrapping = false)
    private double altitud;

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public double getAltitud() {
        return altitud;
    }
}
