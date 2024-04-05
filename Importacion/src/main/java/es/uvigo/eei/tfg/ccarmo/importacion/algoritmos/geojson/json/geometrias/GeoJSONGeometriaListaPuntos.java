
/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json.geometrias;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeoJSONGeometriaListaPuntos {
    @SerializedName("coordinates")
    private List<List<Double>> coordenadas = null;

    public List<List<Double>> getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(List<List<Double>> coordenadas) {
        this.coordenadas = coordenadas;
    }
}
