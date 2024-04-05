
/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json;

import com.google.gson.annotations.SerializedName;

public class GeoJSONPropiedades {

    @SerializedName("id")
    private Integer id;
    @SerializedName("lon")
    private Double longitud;
    @SerializedName("situacion")
    private String situacion;
    @SerializedName("unidades")
    private Integer unidades;
    @SerializedName("lat")
    private Double latitud;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getSituacion() {
        return situacion;
    }

    public void setSituacion(String situacion) {
        this.situacion = situacion;
    }

    public Integer getUnidades() {
        return unidades;
    }

    public void setUnidades(Integer unidades) {
        this.unidades = unidades;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

}
