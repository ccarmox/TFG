/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.exportacion;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;

public class ExportarGeoJSON {

    public static String getGeoJSONGenerado(@NonNull ArrayList<Vertice> lista) {
        // Construir un objeto de tipo FeatureCollection
        JSONObject featureCollection = new JSONObject();
        JSONArray featuresArray = new JSONArray();

        try {
            // Iterar sobre la lista de puntos y agregar cada punto como una característica al arreglo de características
            for (Vertice nudo : lista) {
                JSONObject feature = new JSONObject();
                feature.put("type", "Feature");
                feature.put("properties", new JSONObject().put("nombre", nudo.getInformacion())); // Propiedades adicionales del punto
                feature.put("geometry", new JSONObject().put("type", "Point")
                        .put("coordinates", new JSONArray().put(nudo.getLongitud()).put(nudo.getLatitud()))); // Coordenadas del punto

                featuresArray.put(feature);
            }

            // Agregar el arreglo de características al objeto FeatureCollection
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("features", featuresArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return featureCollection.toString();
    }

    public static String getGeoJSONGenerado(@NonNull String nudoDB, @NonNull Context context) {
        // Construir un objeto de tipo FeatureCollection
        JSONObject featureCollection = new JSONObject();
        JSONArray featuresArray = new JSONArray();

        try {

            SQLite db = SQLite.getBaseDeDatos(nudoDB, context);

            Cursor cursor = db.getCursorRuta();
            cursor.moveToFirst();

            // Iterar sobre la lista de puntos y agregar cada punto como una característica al arreglo de características
            while (cursor.moveToNext()) {
                Vertice nudo = SQLite.getVertice(cursor);
                JSONObject feature = new JSONObject();
                feature.put("type", "Feature");
                feature.put("properties", new JSONObject().put("nombre", nudo.getInformacion())); // Propiedades adicionales del punto
                feature.put("geometry", new JSONObject().put("type", "Point")
                        .put("coordinates", new JSONArray().put(nudo.getLongitud()).put(nudo.getLatitud()))); // Coordenadas del punto

                featuresArray.put(feature);
            }

            // Agregar el arreglo de características al objeto FeatureCollection
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("features", featuresArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return featureCollection.toString();
    }
}
