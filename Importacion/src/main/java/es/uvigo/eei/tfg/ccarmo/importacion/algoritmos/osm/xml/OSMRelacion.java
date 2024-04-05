
/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class OSMRelacion {

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
    @JacksonXmlProperty(localName = "member", isAttribute = true)
    private List<OSMMiembro> miembros = null;
    @JacksonXmlProperty(localName = "tag", isAttribute = true)
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

    public List<OSMMiembro> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<OSMMiembro> miembros) {
        this.miembros = miembros;
    }

    public List<OSMTag> getTags() {
        return tags;
    }

    public void setTags(List<OSMTag> tags) {
        this.tags = tags;
    }

    /*public boolean isPointInside(double latitude, double longitude, Osm osm){

        Polygon.Builder builder = Polygon.Builder();
        for(Member member:getMember()){
            if(member.getType().equals("way")){
                if(member.getRole().equals("outer")){
                    Way way = osm.getWay(member.getRef());
                    for(Nd nd:way.getNd()){
                        Node node = osm.getNode(nd.getRef());
                        builder.addVertex(new Point(node.getLatitude(), node.getLongitude()));
                    }
                    builder.close();
                }
            }
        }

        for(Member member:getMember()){
            if(member.getType().equals("way")){
                if(member.getRole().equals("inner")){
                    Way way = osm.getWay(member.getRef());
                    for(Nd nd:way.getNd()){
                        Node node = osm.getNode(nd.getRef());
                        builder.addVertex(new Point(node.getLatitude(), node.getLongitude()));
                    }
                    builder.close();
                }
            }
        }

        Polygon polygon = builder.build();

        Point point = new Point(latitude, longitude);

        return polygon.contains(point);
    }*/

}
