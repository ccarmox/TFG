/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json.GeoJSONFeature;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json.geometrias.GeoJSONGeometriaListaListasPuntos;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json.geometrias.GeoJSONGeometriaListaPuntos;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.json.geometrias.GeoJSONGeometriaPunto;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Area;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Linea;

public class GEOJSON {


    public static boolean parse(String text, int resolucion, TipoVia tipoVia, int unible, InterfazVertices onDataParsed) {

        try {

            //Se carga la biblioteca para transformar el JSON en objetos de Java
            Gson gson = new Gson();

            //Se crea el objeto con los datos
            JsonElement element = JsonParser.parseString(text).getAsJsonObject().get("features");

            if (element != null) {
                JsonArray features = element.getAsJsonArray();
                for (JsonElement feature : features) {

                    GeoJSONFeature objeto = gson.fromJson(feature, GeoJSONFeature.class);


                    switch (objeto.getGeometria().getTipo()) {
                        case "Point":
                            //La geometria esta formada por un unico punto
                            GeoJSONGeometriaPunto geo1 = gson.fromJson(feature.getAsJsonObject().get("geometry"), GeoJSONGeometriaPunto.class);
                            //Se guarda directamente
                            onDataParsed.onNuevoVertice(new Vertice(objeto.getPropiedades().getSituacion(), resolucion, geo1.getCoordenadas().get(1), geo1.getCoordenadas().get(0), tipoVia, unible));
                            break;
                        case "MultiPoint":
                            //La geometria esta formada por varios puntos
                            GeoJSONGeometriaListaPuntos geo2 = gson.fromJson(feature.getAsJsonObject().get("geometry"), GeoJSONGeometriaListaPuntos.class);

                            //Se añaden todos de manera individual
                            for (List<Double> co : geo2.getCoordenadas()) {
                                onDataParsed.onNuevoVertice(new Vertice(objeto.getPropiedades().getSituacion(), resolucion, co.get(1), co.get(0), tipoVia, unible));
                            }
                            break;
                        case "LineString":
                            //La geometria esta formada por uno o mas puntos unidos entre si
                            GeoJSONGeometriaListaPuntos geo3 = gson.fromJson(feature.getAsJsonObject().get("geometry"), GeoJSONGeometriaListaPuntos.class);

                            //Se genera la lista de puntos
                            ArrayList<Vertice> nodos = new ArrayList<>();
                            for (List<Double> co : geo3.getCoordenadas()) {
                                nodos.add(new Vertice(objeto.getPropiedades().getSituacion(), resolucion, co.get(1), co.get(0), tipoVia, unible));
                            }

                            //Se importan como lista
                            Linea.addNudosIntermedios(nodos, resolucion, onDataParsed);
                            break;
                        case "MultiLineString":
                            //La geometria esta definida por varias lineas de puntos
                            GeoJSONGeometriaListaListasPuntos geo4 = gson.fromJson(feature.getAsJsonObject().get("geometry"), GeoJSONGeometriaListaListasPuntos.class);

                            //Se itera sobre las diferentes colecciones
                            for (List<List<Double>> vals : geo4.getCoordenadas()) {

                                //Se genera la lista de puntos de esta coleccion
                                ArrayList<Vertice> nodos4 = new ArrayList<>();
                                for (List<Double> co : vals) {
                                    nodos4.add(new Vertice(objeto.getPropiedades().getSituacion(), resolucion, co.get(1), co.get(0), tipoVia, unible));
                                }

                                //Se importan los puntos de esta coleccion como lista
                                Linea.addNudosIntermedios(nodos4, resolucion, onDataParsed);
                            }
                            break;
                        case "Polygon":
                            //La geometria esta formada por una lista de puntos que describen un area
                            GeoJSONGeometriaListaPuntos geo5 = gson.fromJson(feature.getAsJsonObject().get("geometry"), GeoJSONGeometriaListaPuntos.class);

                            //Se generan todos los puntos
                            ArrayList<Vertice> nodos5 = new ArrayList<>();
                            for (List<Double> co : geo5.getCoordenadas()) {
                                nodos5.add(new Vertice(objeto.getPropiedades().getSituacion(), resolucion, co.get(1), co.get(0), tipoVia, unible));
                            }

                            //Se añade la lista como area
                            Area.interpretarUnArea(nodos5, tipoVia, resolucion, onDataParsed);
                            break;
                        case "MultiPolygon":
                            //La geometria esta formada por una lista de poligonos
                            GeoJSONGeometriaListaListasPuntos geo6 = gson.fromJson(feature.getAsJsonObject().get("geometry"), GeoJSONGeometriaListaListasPuntos.class);

                            //Se itera sobre todos los poligonos
                            for (List<List<Double>> vals : geo6.getCoordenadas()) {

                                //Se obtienen todos los puntos del poligono actual
                                ArrayList<Vertice> nodos6 = new ArrayList<>();
                                for (List<Double> co : vals) {
                                    nodos6.add(new Vertice(objeto.getPropiedades().getSituacion(), resolucion, co.get(1), co.get(0), tipoVia, unible));
                                }

                                //Se importan los puntos como poligono
                                Area.interpretarUnArea(nodos6, tipoVia, resolucion, onDataParsed);
                            }
                            break;
                    }

                }

                return true;

            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return false;

    }

}
