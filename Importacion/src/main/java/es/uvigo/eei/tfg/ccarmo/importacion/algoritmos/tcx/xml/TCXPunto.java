/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TCXPunto {

    @JacksonXmlProperty(localName = "Position")
    @JacksonXmlElementWrapper(useWrapping = false)
    private TCXPosicion posicion;

    @JacksonXmlProperty(localName = "AltitudeMeters")
    private double altitud;

    public TCXPosicion getPosicion() {
        return posicion;
    }

    public double getAltitud() {
        return altitud;
    }

    public double getLatitudeDegrees() {
        return getPosicion().getLatitud();
    }

    public double getLongitudeDegrees() {
        return getPosicion().getLongitud();
    }
}
