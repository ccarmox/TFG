
/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json;

import com.google.gson.annotations.SerializedName;

public class GeoJSONFeature {

    @SerializedName("properties")
    private GeoJSONPropiedades propiedades;
    @SerializedName("type")
    private String tipo;
    @SerializedName("geometry")
    private GeoJSONGeometriaGenerica geometria;

    public GeoJSONPropiedades getPropiedades() {
        return propiedades;
    }

    public void setPropiedades(GeoJSONPropiedades propiedades) {
        this.propiedades = propiedades;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public GeoJSONGeometriaGenerica getGeometria() {
        return geometria;
    }

    public void setGeometria(GeoJSONGeometriaGenerica geometria) {
        this.geometria = geometria;
    }

}
