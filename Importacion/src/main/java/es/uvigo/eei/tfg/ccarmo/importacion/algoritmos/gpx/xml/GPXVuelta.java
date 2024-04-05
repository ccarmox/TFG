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
import java.util.List;

public class GPXVuelta implements Serializable {

    @JacksonXmlProperty(localName = "name")
    @JacksonXmlElementWrapper(useWrapping = false)
    private String nombre;

    @JacksonXmlProperty(localName = "trkseg")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GPXSegmento> segmentos;

    public String getNombre() {
        return nombre;
    }

    public List<GPXSegmento> getSegmentos() {
        return segmentos;
    }
}
