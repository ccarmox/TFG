/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.utils;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;

public class Area {


    /**
     * Transforma un poligono del mapa en una nube de nudos
     * para hacerlo se dibuja el poligono necesario y se comprueba si los pixeles solicitados
     * corresponden a un punto interior o exterior
     */
    public static void interpretarUnArea(ArrayList<Vertice> nudos, TipoVia tipo, int resolucion, InterfazVertices onDataParsed) {

        //Se selecciona el paso acorde a la resolucion
        double paso = 1D / Math.pow(10, resolucion);

        android.util.Log.v("Comenzando", "Area " + nudos.size() + " nodos");

        //Se buscan las coordenadas maximas y minimas
        double minLatitude = nudos.get(0).getLatitud();
        double maxLatitude = nudos.get(0).getLatitud();

        double minLongitude = nudos.get(0).getLongitud();
        double maxLongitude = nudos.get(0).getLongitud();


        for (Vertice p : nudos) {
            if (p.getLatitud() > maxLatitude) {
                maxLatitude = p.getLatitud();
            }
            if (p.getLatitud() < minLatitude) {
                minLatitude = p.getLatitud();
            }
            if (p.getLongitud() > maxLongitude) {
                maxLongitude = p.getLongitud();
            }
            if (p.getLongitud() < minLongitude) {
                minLongitude = p.getLongitud();
            }
        }

        //Se transforman los vertices en coordenadas de la biblioteca JTS
        Coordinate[] puntosPoligono = new Coordinate[nudos.size()];
        for (int i = 0; i < nudos.size(); i++) {
            puntosPoligono[i] = new Coordinate(nudos.get(i).getLongitud(), nudos.get(i).getLatitud());
        }

        //Se crea el poligono con las coordenadas
        GeometryFactory geometryFactory = new GeometryFactory();
        Polygon jtsPolygon = geometryFactory.createPolygon(puntosPoligono);

        //Se itera entre los valores minimos y maximos con el paso seleccionado
        for (double x = minLongitude; x < maxLongitude; x = x + paso) {
            for (double y = minLatitude; y < maxLatitude; y = y + paso) {

                //Se crea un punto con las coordenadas de la iteracion
                Point jtsPoint = geometryFactory.createPoint(new Coordinate(x, y));

                //Se comprueba que el punto esta dentro del poligono
                if (jtsPolygon.contains(jtsPoint)) {

                    //Se guarda el nuevo vertice
                    onDataParsed.onNuevoVertice(new Vertice(nudos.get(0).getInformacion(), resolucion, y, x, tipo, Vertice.Unible.UNIBLE_CON_TODO));
                }

            }
        }

        android.util.Log.v("Terminado", "Area " + nudos.size() + " nodos");

    }


}
