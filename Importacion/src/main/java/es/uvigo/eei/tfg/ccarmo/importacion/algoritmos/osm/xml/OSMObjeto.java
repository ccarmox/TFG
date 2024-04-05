/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


public class OSMObjeto {

    //https://wiki.openstreetmap.org/wiki/OSM_XML

    @JacksonXmlProperty(localName = "version", isAttribute = true)
    private String version;

    @JacksonXmlProperty(localName = "generator", isAttribute = true)
    private String generadorExportacionOSM;

    @JacksonXmlProperty(localName = "copyright", isAttribute = true)
    private String copyrightDatos;

    @JacksonXmlProperty(localName = "attribution", isAttribute = true)
    private String atribucion;

    @JacksonXmlProperty(localName = "license", isAttribute = true)
    private String licencia;


    @JacksonXmlProperty(localName = "bounds")
    @JacksonXmlElementWrapper(useWrapping = false)
    private OSMFrontera fronteras;

    @JacksonXmlProperty(localName = "node")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<OSMNodo> nodos;

    @JacksonXmlProperty(localName = "way")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<OSMVia> vias;

    @JacksonXmlProperty(localName = "relation")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<OSMRelacion> relaciones;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGeneradorExportacionOSM() {
        return generadorExportacionOSM;
    }

    public void setGeneradorExportacionOSM(String generadorExportacionOSM) {
        this.generadorExportacionOSM = generadorExportacionOSM;
    }

    public String getCopyrightDatos() {
        return copyrightDatos;
    }

    public void setCopyrightDatos(String copyrightDatos) {
        this.copyrightDatos = copyrightDatos;
    }

    public String getAtribucion() {
        return atribucion;
    }

    public void setAtribucion(String atribucion) {
        this.atribucion = atribucion;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public OSMFrontera getFronteras() {
        return fronteras;
    }

    public void setFronteras(OSMFrontera fronteras) {
        this.fronteras = fronteras;
    }

    public List<OSMNodo> getNodos() {
        return nodos;
    }

    public void setNodos(List<OSMNodo> nodos) {
        this.nodos = nodos;
    }

    public List<OSMVia> getVias() {
        return vias;
    }

    public void setVias(List<OSMVia> vias) {
        this.vias = vias;
    }

    public List<OSMRelacion> getRelaciones() {
        return relaciones;
    }

    public void setRelaciones(List<OSMRelacion> relaciones) {
        this.relaciones = relaciones;
    }


}
