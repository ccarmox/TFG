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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.List;

@JacksonXmlRootElement(localName = "gpx")
public class GPXObjeto implements Serializable {

    @JacksonXmlProperty(localName = "metadate")
    @JacksonXmlElementWrapper(useWrapping = false)
    private String metadatos;

    @JacksonXmlProperty(localName = "trk")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GPXVuelta> vueltas;

    public String getMetadatos() {
        return metadatos;
    }

    public List<GPXVuelta> getVueltas() {
        return vueltas;
    }
}
