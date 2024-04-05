
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

public class OSMNodo {

    @JacksonXmlProperty(localName = "id", isAttribute = true)
    private String id;
    @JacksonXmlProperty(localName = "visible", isAttribute = true)
    private String visible;
    @JacksonXmlProperty(localName = "version", isAttribute = true)
    private String version;
    @JacksonXmlProperty(localName = "changeset", isAttribute = true)
    private String referenciaCambios;
    @JacksonXmlProperty(localName = "timestamp", isAttribute = true)
    private String fecha;
    @JacksonXmlProperty(localName = "user", isAttribute = true)
    private String autor;
    @JacksonXmlProperty(localName = "uid", isAttribute = true)
    private String uid;
    @JacksonXmlProperty(localName = "lat", isAttribute = true)
    private String latitud;
    @JacksonXmlProperty(localName = "lon", isAttribute = true)
    private String longitud;

    @JacksonXmlElementWrapper(localName = "tag", useWrapping = false)
    private List<OSMTag> tags = null;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReferenciaCambios() {
        return referenciaCambios;
    }

    public void setReferenciaCambios(String referenciaCambios) {
        this.referenciaCambios = referenciaCambios;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitud() {
        return Double.parseDouble(latitud);
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return Double.parseDouble(longitud);
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public List<OSMTag> getTags() {
        return tags;
    }

    public void setTags(List<OSMTag> tags) {
        this.tags = tags;
    }

}
